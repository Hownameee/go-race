import express from 'express';
import { auth } from '../middlewares/auth.middleware.js';
import recordController from '../controllers/record.controller.js';
import {
  getProfileStatisticsSchema,
  getWeeklySummarySchema,
  recordIdSchema,
  recordSchema,
  recordUpdateSchema,
  userIdSchema,
} from '../utils/schemas/record.schema.js';
import validation from '../middlewares/validation.js';

const router = express.Router();

router.get('/', auth, recordController.getList);

router.get(
  '/me/weekly-summary',
  auth,
  validation(getWeeklySummarySchema, 'query'),
  recordController.getMyWeeklySummary,
);

router.get(
  '/me/profile-statistics',
  auth,
  validation(getProfileStatisticsSchema, 'query'),
  recordController.getMyProfileStatistics,
);

router.get(
  '/users/:userId',
  auth,
  recordController.getUserRecords,
);

router.get(
  '/users/:userId/weekly-summary',
  auth,
  validation(getWeeklySummarySchema, 'query'),
  recordController.getUserWeeklySummary,
);

router.get(
  '/users/:userId/profile-statistics',
  auth,
  validation(getProfileStatisticsSchema, 'query'),
  recordController.getUserProfileStatistics,
);

router.get(
  '/:recordId',
  auth,
  validation(recordIdSchema, 'params'),
  recordController.getRecord,
);

router.post('/', auth, validation(recordSchema), recordController.createRecord);

router.patch(
  '/:recordId',
  auth,
  validation(recordIdSchema, 'params'),
  validation(recordUpdateSchema),
  recordController.updateRecord,
);

router.get(
  '/me/streak',
  auth,
  recordController.getMyStreak,
);

router.get(
  '/users/:userId/streak',
  auth,
  validation(userIdSchema, 'params'),
  recordController.getUserStreak,
);

export default router;
