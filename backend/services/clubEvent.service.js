import clubEventRepo from '../repo/clubEvent.repo.js';
import clubRepo from '../repo/club.repo.js';

const clubEventService = {
  createEvent(
    clubId,
    userId,
    {
      title,
      description,
      target_distance,
      target_duration_seconds,
      start_time,
      end_time,
    },
  ) {
    const isLeader = clubRepo.checkIsLeader(clubId, userId);
    if (!isLeader) {
      const error = new Error('Only club leader can create events');
      error.status = 403;
      throw error;
    }

    const eventId = clubEventRepo.createEvent(
      clubId,
      userId,
      title,
      description,
      target_distance || 0,
      target_duration_seconds || 0,
      start_time,
      end_time,
    );

    return { eventId };
  },

  getEvents(clubId, userId) {
    const club = clubRepo.findById(clubId);
    if (!club) {
      const error = new Error('Club not found');
      error.status = 404;
      throw error;
    }

    return clubEventRepo.findEventsByClubId(clubId, userId);
  },

  joinEvent(clubId, userId, eventId) {
    const event = clubEventRepo.findEventByIdAndClubId(eventId, clubId);
    if (!event) {
      const error = new Error('Event not found in this club');
      error.status = 404;
      throw error;
    }

    const clubWithMemberStatus = clubRepo.findByIdAndUserId(userId, clubId);
    if (!clubWithMemberStatus || clubWithMemberStatus.status !== 'approved') {
      const error = new Error(
        'You must be an approved member of the club to join its events',
      );
      error.status = 403;
      throw error;
    }

    try {
      clubEventRepo.joinEvent(eventId, userId);
    } catch (err) {
      if (err.code === 'SQLITE_CONSTRAINT_PRIMARYKEY') {
        const error = new Error('You have already joined this event');
        error.status = 400;
        throw error;
      }
      throw err;
    }
  },

  getEventStats(clubId, eventId) {
    const event = clubEventRepo.findEventByIdAndClubId(eventId, clubId);
    if (!event) {
      const error = new Error('Event not found in this club');
      error.status = 404;
      throw error;
    }

    const stats = clubEventRepo.getEventStats(eventId);
    if (!stats) {
      const error = new Error('Event stats not found');
      error.status = 404;
      throw error;
    }

    return stats;
  },
};

export default clubEventService;
