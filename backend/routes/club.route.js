import express from 'express';
import clubController from '../controllers/club.controller.js';

const clubRouter = express.Router();

clubRouter.get('/', clubController.getClubs);

export default clubRouter;