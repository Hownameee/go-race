import express from 'express';
import { auth } from '../middlewares/auth.middleware.js';
import recordController from '../controllers/record.controller.js';
import {
  getWeeklySummarySchema,
  recordIdSchema,
  recordSchema,
  recordUpdateSchema,
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

export default router;
