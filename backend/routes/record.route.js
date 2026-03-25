import express from 'express';
import recordController from '../controllers/record.controller.js';
import {
  recordIdSchema,
  recordSchema,
} from '../utils/schemas/record.schema.js';
import { userIdSchema } from '../utils/schemas/user.schema.js';
import validation from '../middlewares/validation.js';
import { offsetSchema } from '../utils/schemas/offset.schema.js';

const recordRouter = express.Router({ mergeParams: true });

recordRouter.use(validation(userIdSchema, 'params'));

recordRouter.get(
  '/',
  validation(offsetSchema, 'query'),
  recordController.getList,
);

recordRouter.get(
  '/:recordId',
  validation(recordIdSchema, 'params'),
  recordController.getRecord,
);

recordRouter.post('/', validation(recordSchema), recordController.createRecord);

export default recordRouter;
