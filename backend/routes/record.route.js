import express from 'express';
import { auth } from '../middlewares/auth.middleware.js';
import validation from '../middlewares/validation.js';
import recordController from '../controllers/record.controller.js';
import { getWeeklySummarySchema } from '../utils/schemas/record.schema.js';

const router = express.Router();

router.get('/me/weekly-summary', auth, validation(getWeeklySummarySchema, 'query'), recordController.getMyWeeklySummary);

export default router;
