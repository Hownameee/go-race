import express from 'express';
import notificationController from '../controllers/notification.controller.js';

const router = express.Router();

router.get('/', notificationController.getList);
router.post('/', notificationController.createNotification);
router.put('/:id/read', notificationController.markAsRead);

export default router;
