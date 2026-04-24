import crypto from 'crypto';
import clubRepo from '../repo/club.repo.js';
import { getImageUrlS3, uploadImageS3 } from '../utils/s3/s3.js';

const clubService = {
    async getClubs(userId, offset, limit) {
        let clubs = await clubRepo.findMyClubs(userId, offset, limit);
        let type = 'my clubs';
        if (clubs.length === 0) {
            clubs = await clubRepo.findDiscoverClubs(userId, offset, limit);
            type = 'discover clubs';
        }

        const result = await Promise.all(
            clubs.map(async (item) => {
                const { avatar_s3_key, ...itemWithoutS3Key } = item;
                if (avatar_s3_key) {
                    const avatar_url = await getImageUrlS3(avatar_s3_key);
                    return {
                        ...itemWithoutS3Key,
                        avatar_url,
                        status: item.status,
                    };
                }
                return {
                    ...itemWithoutS3Key,
                    status: item.status,
                };
            }),
        );

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
            return { message: "Joined", status: "approved" };
        } else {
            await clubRepo.addMember(clubId, userId, 'pending');
            return { message: "Request sent", status: "pending" };
        }
    },

    async leaveClub(clubId, userId) {
        const club = await clubRepo.findById(clubId);
        if (!club) {
            throw new Error('Club not found');
        }
        await clubRepo.removeMember(clubId, userId);
        return { message: "Left" };
    },

    async createClub(name, description, privacyType, leaderId) {
        if (!name) throw new Error('Club name is required');
        try {
            const clubId = clubRepo.createClub(name, description, privacyType, leaderId);
            return { club_id: clubId, message: 'Club created successfully' };
        } catch (error) {
            throw error;
        }
    },

    async getClubAdmins(clubId) {
        const admins = await clubRepo.findAdmins(clubId);
        return admins;
    },

    async checkIsLeader(clubId, userId) {
        return clubRepo.checkIsLeader(clubId, userId);
    },

    async updateClub(userId, clubId, { name, description, imageBase64, imageContentType }) {
        const isLeader = clubRepo.checkIsLeader(clubId, userId);
        if (!isLeader) {
            const error = new Error('Only the club leader can update club information.');
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
                const error = new Error('Invalid image type. Only PNG and JPEG are allowed.');
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
                    console.error(`[Background Error] Failed to upload avatar for club ${clubId}:`, err);
                }
            })();
        }
    },

    async getClubStats(clubId, userId) {
        const stats = await clubRepo.getClubStats(clubId);

        // Find the current user's stats in the leaderboard
        const myStats = stats.leaderboard.find(item => item.member_id === userId);

        const formatDuration = (seconds) => {
            const h = Math.floor(seconds / 3600);
            const m = Math.floor((seconds % 3600) / 60);
            const s = seconds % 60;
            if (h > 0) return `${h}h ${m}m ${s}s`;
            return `${m}m ${s}s`;
        };

        const personalBestDistanceStr = myStats ? `${myStats.total_distance.toFixed(2)} km` : "0.00 km";
        const personalBestDurationStr = myStats ? formatDuration(myStats.total_duration) : "0m 0s";

        return {
            totalDistance: stats.totalDistance,
            totalActivities: stats.totalActivities,
            clubRecordDistanceStr: `${stats.clubRecordDistance.toFixed(2)} km`,
            clubRecordDurationStr: formatDuration(stats.clubRecordDuration),
            personalBestDistanceStr: personalBestDistanceStr,
            personalBestDurationStr: personalBestDurationStr,
            leaderboard: stats.leaderboard.map(item => ({
                memberId: item.member_id.toString(),
                memberName: item.member_name,
                avatarUrl: item.avatar_url,
                distance: item.total_distance
            }))
        };
    },
};

export default clubService;