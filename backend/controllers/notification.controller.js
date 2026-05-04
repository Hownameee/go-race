import notificationService from '../services/notification.service.js';

const notificationController = {
  async getList(req, res) {
    try {
      const userId = req.user.userId;
      const limit = req.query.limit ? parseInt(req.query.limit) : 20;
      const cursor = req.query.cursor ? parseInt(req.query.cursor) : null;
      const data = await notificationService.getNotifications(userId, cursor, limit);
      
      const nextCursor = data.length === limit ? data[data.length - 1].id : null;
      
      return res.ok({
        notifications: data,
        nextCursor: nextCursor,
      });
    } catch (err) {
      console.error('[Notification][getList]', err);
      return res.error(null, err.message);
    }
  },

  async createNotification(req, res) {
    try {
      const { user_id, type, actor_id, activity_id, title, message } = req.body;

      const notification = await notificationService.createAndSend({
        userId: user_id,
        type,
        actorId: actor_id ?? null,
        activityId: activity_id ?? null,
        title,
        message,
      });

      return res.created(notification, 'Notification created');
    } catch (err) {
      console.error('[Notification][create]', err);
      return res.error(null, err.message);
    }
  },

  async markAsRead(req, res) {
    try {
      const { id } = req.params;

      await notificationService.markAsRead(id);

      return res.ok({
        message: 'Marked as read',
      });
    } catch (err) {
      console.error('[Notification][markAsRead]', err);
      return res.error(null, err.message);
    }
  },

  async getUnreadCount(req, res) {
    try {
      const userId = req.user.userId;
      const count = await notificationService.getUnreadCount(userId);
      return res.ok({ count });
    } catch (err) {
      console.error('[Notification][getUnreadCount]', err);
      return res.error(null, err.message);
    }
  },
};

export default notificationController;
