import express from 'express';
import recordController from '../controllers/record.controller.js';
import {
  recordIdSchema,
  recordSchema,
} from '../utils/schemas/record.schema.js';
import { userIdSchema } from '../utils/schemas/user.schema.js';
import validation from '../middlewares/validation.js';

const recordRouter = express.Router({ mergeParams: true });

recordRouter.use(validation(userIdSchema, 'params'));

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

export default recordRouter;
