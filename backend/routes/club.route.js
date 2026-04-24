import express from 'express';
import clubController from '../controllers/club.controller.js';
import validation from '../middlewares/validation.js';
import { createClubSchema, clubIdSchema, updateClubSchema } from '../utils/schemas/club.schema.js';
import { eventIdSchema, createClubEventSchema } from '../utils/schemas/club_event.schema.js';
import clubEventController from '../controllers/club_event.controller.js';

const clubRouter = express.Router();

clubRouter.get('/', clubController.getClubs);
clubRouter.get('/:clubId', validation(clubIdSchema, 'params'), clubController.getClubById);
clubRouter.post('/', validation(createClubSchema), clubController.createClub);
clubRouter.post('/:clubId/join', validation(clubIdSchema, 'params'), clubController.joinClub);
clubRouter.post('/:clubId/leave', validation(clubIdSchema, 'params'), clubController.leaveClub);
clubRouter.get('/:clubId/posts', validation(clubIdSchema, 'params'), clubController.getClubPosts);
clubRouter.get('/:clubId/admins', validation(clubIdSchema, 'params'), clubController.getAdmins);
clubRouter.get('/:clubId/is-leader', validation(clubIdSchema, 'params'), clubController.checkIsLeader);
clubRouter.put('/:clubId', validation(clubIdSchema, 'params'), validation(updateClubSchema), clubController.updateClub);
clubRouter.get('/:clubId/stats', validation(clubIdSchema, 'params'), clubController.getClubStats);

// Event routes
clubRouter.post('/:clubId/events', validation(clubIdSchema, 'params'), validation(createClubEventSchema), clubEventController.createEvent);
clubRouter.get('/:clubId/events', validation(clubIdSchema, 'params'), clubEventController.getEvents);
clubRouter.post('/:clubId/events/:eventId/join', validation(clubIdSchema, 'params'), validation(eventIdSchema, 'params'), clubEventController.joinEvent);
clubRouter.get('/:clubId/events/:eventId/stats', validation(clubIdSchema, 'params'), validation(eventIdSchema, 'params'), clubEventController.getEventStats);

export default clubRouter;