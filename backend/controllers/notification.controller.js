import notificationService from '../services/notification.service.js';

const notificationController = {
  async getList(req, res) {
    try {
      const userId = req.user.userId;

      const data = await notificationService.getNotifications(userId);

      return res.ok({
        notifications: data,
        nextCursor: null, 
      });

    } catch (err) {
      console.error('[Notification][getList]', err);
      return res.error(null, err.message);
    }
  },

  async createNotification(req, res) {
    try {
      const {
        user_id,
        type,
        actor_id,
        activity_id,
        title,
        message,
      } = req.validated;

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
      const { id } = req.validated;

      await notificationService.markAsRead(Number(id));

      return res.ok({
        message: 'Marked as read',
      });

    } catch (err) {
      console.error('[Notification][markAsRead]', err);
      return res.error(null, err.message);
    }
  },

};

export default notificationController;