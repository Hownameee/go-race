import clubRepo from '../repo/club.repo.js';
import notificationService from './notification.service.js';

const clubService = {
  getSuggestClubs: async (currentUserId, limit) => {
    return clubRepo.getSuggestClubs(currentUserId, limit);
  },

  searchClubsByName: async (currentUserId, search, limit) => {
    if (!search || search.trim() === '') {
      return [];
    }
    return clubRepo.searchClubsByName(currentUserId, search, limit);
  },

  joinClub: async (currentUserId, clubId) => {
    const club = clubRepo.getClubById(clubId);

    try {
      const status = club.privacy_type === 'private' ? 'pending' : 'approved';
      clubRepo.joinClub(clubId, currentUserId, status);

      const isPending = status === 'pending';
      const notificationTitle = isPending ? 'Join Request' : 'New Member';
      const notificationMessage = isPending
        ? `Someone requested to join your club "${club.name}".`
        : `Someone joined your club "${club.name}".`;
      const notificationType = isPending ? 'club_join_request' : 'system';

      if (club.leader_id !== currentUserId) {
        await notificationService.createAndSend({
          userId: club.leader_id,
          type: notificationType,
          actorId: currentUserId,
          activityId: club.club_id,
          title: notificationTitle,
          message: notificationMessage,
        });
      }

      return { status, message: isPending ? 'Join request sent.' : 'Joined club successfully.' };
    } catch (e) {
      if (e.message.includes('UNIQUE constraint failed')) {
        const error = new Error('You have already joined or requested to join this club.');
        error.status = 409;
        throw error;
      }
      throw e;
    }
  },
};

export default clubService;
