import clubEventService from '../services/clubEvent.service.js';

const clubEventController = {
  async createEvent(req, res) {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const result = clubEventService.createEvent(clubId, userId, req.body);
      res.created(result, 'Event created successfully');
    } catch (error) {
      if (error.status === 403) return res.violate(null, error.message);
      if (error.status === 400) return res.badRequest(null, error.message);
      console.error('Create event error:', error);
      res.error(null, error.message);
    }
  },

  async getEvents(req, res) {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const events = clubEventService.getEvents(clubId, userId);
      res.ok(events);
    } catch (error) {
      if (error.status === 404) return res.notFound();
      console.error('Get events error:', error);
      res.error(null, error.message);
    }
  },

  async joinEvent(req, res) {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const eventId = parseInt(req.params.eventId);
      clubEventService.joinEvent(clubId, userId, eventId);
      res.ok(null, 'Successfully joined the event');
    } catch (error) {
      if (error.status === 404) return res.notFound();
      if (error.status === 403) return res.violate(null, error.message);
      if (error.status === 400) return res.badRequest(null, error.message);
      console.error('Join event error:', error);
      res.error(null, error.message);
    }
  },

  async getEventStats(req, res) {
    try {
      const clubId = parseInt(req.params.clubId);
      const eventId = parseInt(req.params.eventId);
      const stats = clubEventService.getEventStats(clubId, eventId);
      res.ok(stats);
    } catch (error) {
      if (error.status === 404) return res.notFound();
      console.error('Get event stats error:', error);
      res.error(null, error.message);
    }
  },
};

export default clubEventController;
