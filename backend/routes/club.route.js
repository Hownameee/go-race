import express from 'express';
import clubController from '../controllers/club.controller.js';
import validation from '../middlewares/validation.js';
import { auth } from '../middlewares/auth.middleware.js';
import { createClubSchema, clubIdSchema, updateClubSchema } from '../utils/schemas/club.schema.js';

const clubRouter = express.Router();

clubRouter.get('/', clubController.getClubs);
clubRouter.get('/:clubId', validation(clubIdSchema, 'params'), clubController.getClubById);
clubRouter.post('/', validation(createClubSchema), clubController.createClub);
clubRouter.post('/:clubId/join', validation(clubIdSchema, 'params'), clubController.joinClub);
clubRouter.post('/:clubId/leave', validation(clubIdSchema, 'params'), clubController.leaveClub);
clubRouter.get('/:clubId/posts', auth, validation(clubIdSchema, 'params'), clubController.getClubPosts);
clubRouter.get('/:clubId/admins', auth, validation(clubIdSchema, 'params'), clubController.getAdmins);
clubRouter.get('/:clubId/is-leader', auth, validation(clubIdSchema, 'params'), clubController.checkIsLeader);
clubRouter.put('/:clubId', auth, validation(clubIdSchema, 'params'), validation(updateClubSchema), clubController.updateClub);

export default clubRouter;