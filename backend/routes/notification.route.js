import express from 'express';
import notificationController from '../controllers/notification.controller.js';

const router = express.Router();

/**
 * @route   GET /notifications
 * @desc    Lấy tất cả notification của user
 * @access  Private (giả sử req.user.userId có sẵn)
 */
router.get('/', notificationController.getList);
3;
/**
 * @route   POST /notifications
 * @desc    Tạo notification mới
 * @body    { user_id, type, actor_id, activity_id, title, message }
 */
router.post('/', notificationController.createNotification);

/**
 * @route   PUT /notifications/:id/read
 * @desc    Đánh dấu notification đã đọc
 */
router.put('/:id/read', notificationController.markAsRead);

export default router;
