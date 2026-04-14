import express from 'express';
import clubController from '../controllers/club.controller.js';
import validation from '../middlewares/validation.js';
import { createClubSchema, joinClubSchema } from '../utils/schemas/club.schema.js';

const clubRouter = express.Router();

clubRouter.get('/', clubController.getClubs);
clubRouter.post('/', validation(createClubSchema), clubController.createClub);
clubRouter.post('/:clubId/join', validation(joinClubSchema, 'params'), clubController.joinClub);

export default clubRouter;