import crypto from 'crypto';
import clubRepo from '../repo/club.repo.js';
import { getImageUrlS3, uploadImageS3 } from '../utils/s3/s3.js';

async function attachAvatarUrls(clubs) {
  return Promise.all(
    clubs.map(async (item) => {
      const { avatar_s3_key, ...itemWithoutS3Key } = item;
      let avatar_url = null;
      if (avatar_s3_key) {
        avatar_url = await getImageUrlS3(avatar_s3_key);
      }
      return {
        ...itemWithoutS3Key,
        avatar_url,
      };
    }),
  );
}

const clubService = {
  async getClubs(userId, offset, limit) {
    let clubs = await clubRepo.findMyClubs(userId, offset, limit);
    let type = 'my clubs';
    if (clubs.length === 0) {
      clubs = await clubRepo.findDiscoverClubs(userId, offset, limit);
      type = 'discover clubs';
    }

    const result = await attachAvatarUrls(clubs);
    return { clubs: result, type };
  },

  async getClubByIdAndUserId(userId, clubId) {
    const club = await clubRepo.findByIdAndUserId(userId, clubId);
    if (!club) {
      throw new Error('Club not found');
    }
    const { avatar_s3_key, ...itemWithoutS3Key } = club;
    let avatar_url = null;
    if (avatar_s3_key) {
      avatar_url = await getImageUrlS3(avatar_s3_key);
    }
    const res = { ...itemWithoutS3Key, avatar_url };
    return res;
  },

  async joinClub(clubId, userId) {
    const club = await clubRepo.findById(clubId);
    if (!club) {
      throw new Error('Club not found');
    }

    if (club.privacy_type === 'public') {
      await clubRepo.addMember(clubId, userId, 'approved');
      return { message: 'Joined', status: 'approved' };
    } else {
      await clubRepo.addMember(clubId, userId, 'pending');
      return { message: 'Request sent', status: 'pending' };
    }
  },

  async leaveClub(clubId, userId) {
    const club = await clubRepo.findById(clubId);
    if (!club) {
      throw new Error('Club not found');
    }

    if (club.leader_id === userId) {
      const error = new Error(
        'Club leader cannot leave the club. Please transfer leadership to another member first.',
      );
      error.status = 400;
      throw error;
    }

    clubRepo.cleanupOngoingEvents(clubId, userId);
    clubRepo.removeMember(clubId, userId);
    return { message: 'Left' };
  },

  async transferLeadership(clubId, currentLeaderId, newLeaderId) {
    const club = await clubRepo.findById(clubId);
    const newLeaderMemberPromise = clubRepo.findMemberStatus(clubId, newLeaderId);
    if (!club) {
      const error = new Error('Club not found');
      error.status = 404;
      throw error;
    }

    if (club.leader_id !== currentLeaderId) {
      const error = new Error('Only the current leader can transfer leadership');
      error.status = 403;
      throw error;
    }

    if (currentLeaderId === newLeaderId) {
      const error = new Error('You are already the leader');
      error.status = 400;
      throw error;
    }

    const newLeaderMember = await newLeaderMemberPromise;
    if (!newLeaderMember || newLeaderMember.status !== 'approved') {
      const error = new Error(
        'The new leader must be an approved member of the club',
      );
      error.status = 400;
      throw error;
    }

    await Promise.all([
      clubRepo.updateLeader(clubId, newLeaderId),
      clubRepo.updateMemberRole(clubId, newLeaderId, 'admin'),
      clubRepo.updateMemberRole(clubId, currentLeaderId, 'member'),
    ]);

    return { message: 'Leadership transferred successfully' };
  },

  async createClub(name, description, privacyType, leaderId) {
    if (!name) throw new Error('Club name is required');
    try {
      const clubId = clubRepo.createClub(
        name,
        description,
        privacyType,
        leaderId,
      );
      return { club_id: clubId, message: 'Club created successfully' };
    } catch (error) {
      throw error;
    }
  },

  async getClubAdmins(clubId) {
    const admins = await clubRepo.findAdmins(clubId);
    return admins;
  },

  async isLeader(clubId, userId) {
    return clubRepo.checkIsLeader(clubId, userId);
  },

  async isAdminOrLeader(clubId, userId) {
    const club = await clubRepo.findById(clubId);
    if (!club) return false;

    if (club.leader_id === userId) return true;

    const member = await clubRepo.findMemberStatus(clubId, userId);
    return member && member.role === 'admin' && member.status === 'approved';
  },

  async updateClub(
    userId,
    clubId,
    { name, description, imageBase64, imageContentType },
  ) {
    const isLeader = clubRepo.checkIsLeader(clubId, userId);
    if (!isLeader) {
      const error = new Error(
        'Only the club leader can update club information.',
      );
      error.status = 403;
      throw error;
    }

    // Synchronous update for text fields
    if (name || description) {
      await clubRepo.updateClub(clubId, { name, description });
    }

    // Background task for image upload
    if (imageBase64) {
      const validTypes = ['image/png', 'image/jpeg'];
      if (!validTypes.includes(imageContentType)) {
        const error = new Error(
          'Invalid image type. Only PNG and JPEG are allowed.',
        );
        error.status = 400;
        throw error;
      }

      // Fire and forget the upload process
      (async () => {
        try {
          const buffer = Buffer.from(imageBase64, 'base64');
          const ext = imageContentType === 'image/png' ? 'png' : 'jpg';
          const key = `club-avatars/${clubId}-${Date.now()}-${crypto.randomUUID()}.${ext}`;

          await uploadImageS3(buffer, key, imageContentType);

          clubRepo.updateClub(clubId, { avatarS3Key: key });
        } catch (err) {
          console.error(
            `[Background Error] Failed to upload avatar for club ${clubId}:`,
            err,
          );
        }
      })();
    }
  },

  async getClubStats(clubId, userId) {
    const stats = await clubRepo.getClubStats(clubId);

    // Find the current user's stats in the leaderboard
    const myStats = stats.leaderboard.find((item) => item.member_id === userId);

    const formatDuration = (seconds) => {
      const h = Math.floor(seconds / 3600);
      const m = Math.floor((seconds % 3600) / 60);
      const s = seconds % 60;
      if (h > 0) return `${h}h ${m}m ${s}s`;
      return `${m}m ${s}s`;
    };

    const personalBestDistanceStr = myStats
      ? `${myStats.total_distance.toFixed(2)} km`
      : '0.00 km';
    const personalBestDurationStr = myStats
      ? formatDuration(myStats.total_duration)
      : '0m 0s';

    return {
      totalDistance: stats.totalDistance,
      totalActivities: stats.totalActivities,
      clubRecordDistanceStr: `${stats.clubRecordDistance.toFixed(2)} km`,
      clubRecordDurationStr: formatDuration(stats.clubRecordDuration),
      personalBestDistanceStr: personalBestDistanceStr,
      personalBestDurationStr: personalBestDurationStr,
      leaderboard: stats.leaderboard.map((item) => ({
        memberId: item.member_id.toString(),
        memberName: item.member_name,
        avatarUrl: item.avatar_url,
        distance: item.total_distance,
      })),
    };
  },

  async getMembers(clubId, requesterId) {
    const club = await clubRepo.findById(clubId);
    if (!club)
      throw Object.assign(new Error('Club not found'), { status: 404 });

    const requesterStatus = clubRepo.findMemberStatus(clubId, requesterId);
    const isAdminOrLeader =
      requesterStatus &&
      requesterStatus.status === 'approved' &&
      (requesterStatus.role === 'admin' || club.leader_id === requesterId);

    let members = await clubRepo.findAllMembers(clubId);

    // Nếu không phải Admin/Leader, ẩn các thành viên đang 'pending'
    if (!isAdminOrLeader) {
      members = members.filter((m) => m.status === 'approved');
    }

    return await Promise.all(
      members.map(async (m) => {
        return {
          userId: m.user_id,
          fullname: m.fullname,
          avatarUrl: m.avatar_url,
          role: m.role,
          status: m.status,
          joinedAt: m.joined_at,
          isLeader: !!m.is_leader,
        };
      }),
    );
  },

  async updateMemberRole(clubId, requesterId, targetUserId, newRole) {
    const club = await clubRepo.findById(clubId);
    if (club.leader_id !== requesterId) {
      throw Object.assign(new Error('Only the club leader can change roles'), {
        status: 403,
      });
    }

    if (requesterId === targetUserId) {
      throw Object.assign(new Error('You cannot change your own role'), {
        status: 400,
      });
    }

    const validRoles = ['admin', 'member'];
    if (!validRoles.includes(newRole)) {
      throw Object.assign(new Error('Invalid role'), { status: 400 });
    }

    await clubRepo.updateMemberRole(clubId, targetUserId, newRole);
    return { message: `Role updated to ${newRole}` };
  },

  async updateMemberStatus(clubId, requesterId, targetUserId, newStatus) {
    const club = await clubRepo.findById(clubId);
    if (!club)
      throw Object.assign(new Error('Club not found'), { status: 404 });

    const requester = clubRepo
      .findAllMembers(clubId)
      .find((m) => m.user_id === requesterId);
    const target = clubRepo
      .findAllMembers(clubId)
      .find((m) => m.user_id === targetUserId);

    if (!requester || requester.status !== 'approved') {
      throw Object.assign(new Error('Unauthorized'), { status: 403 });
    }

    const isRequesterLeader = club.leader_id === requesterId;
    const isRequesterAdmin = requester.role === 'admin';

    if (!isRequesterLeader && !isRequesterAdmin) {
      throw Object.assign(new Error('Permission denied'), { status: 403 });
    }

    if (newStatus === 'approved' || newStatus === 'rejected') {
      if (!target || target.status !== 'pending') {
        throw Object.assign(
          new Error('No pending request found for this user'),
          { status: 400 },
        );
      }
    } else if (newStatus === 'left') {
      if (!target || target.status !== 'approved') {
        throw Object.assign(new Error('User is not an active member'), {
          status: 400,
        });
      }
      if (targetUserId === club.leader_id) {
        throw Object.assign(new Error('Cannot kick the club leader'), {
          status: 400,
        });
      }
      if (!isRequesterLeader && target.role === 'admin') {
        throw Object.assign(new Error('Admins cannot kick other admins'), {
          status: 403,
        });
      }
    } else {
      throw Object.assign(new Error('Invalid status update'), { status: 400 });
    }

    if (newStatus === 'left') {
      clubRepo.cleanupOngoingEvents(clubId, targetUserId);
      clubRepo.removeMember(clubId, targetUserId);
    } else {
      await clubRepo.updateMemberStatus(clubId, targetUserId, newStatus);
    }
    return { message: `Member status updated to ${newStatus}` };
  },

  getSuggestClubs: async (currentUserId, limit) => {
    const clubs = await clubRepo.getSuggestClubs(currentUserId, limit);
    return attachAvatarUrls(clubs);
  },

  searchClubsByName: async (currentUserId, search, limit) => {
    if (!search || search.trim() === '') {
      return [];
    }
    const clubs = await clubRepo.searchClubsByName(currentUserId, search, limit);
    return attachAvatarUrls(clubs);
  },
};

export default clubService;
