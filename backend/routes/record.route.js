import express from 'express';
import recordController from '../controllers/record.controller.js';
import {
  recordIdSchema,
  recordSchema,
  recordUpdateSchema,
} from '../utils/schemas/record.schema.js';
import validation from '../middlewares/validation.js';

const recordRouter = express.Router();

recordRouter.get(
  '/',
  validation(recordIdSchema, 'query'),
  recordController.getList,
);

recordRouter.get(
  '/sync',
  validation(recordIdSchema, 'query'),
  recordController.getNewList,
);

recordRouter.get(
  '/:recordId',
  validation(recordIdSchema, 'params'),
  recordController.getRecord,
);

recordRouter.post('/', validation(recordSchema), recordController.createRecord);

recordRouter.patch(
  '/:recordId',
  validation(recordIdSchema, 'params'),
  validation(recordUpdateSchema),
  recordController.updateRecord,
);

export default recordRouter;
