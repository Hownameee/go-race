import express from 'express';
import notificationController from '../controllers/notification.controller.js';
import { createNotificationSchema, markAsReadSchema } from '../utils/schemas/notification.schema.js';
import validation from '../middlewares/validation.js';

const router = express.Router();

router.get('/', notificationController.getList);

router.post(
  '/',
  validation(createNotificationSchema, "body"),
  notificationController.createNotification
);

router.put(
  '/:id/read',
  validation(markAsReadSchema, "params"),
  notificationController.markAsRead
);

export default router;