import notificationService from '../services/notification.service.js';

const notificationController = {
  getList: async function (req, res) {
    const userId = 1;

    const data = await notificationService.getNotifications(userId);

    res.ok(data);
  },

  createNotification: async function (req, res) {
    const { user_id, type, actor_id, activity_id, title, message } = req.body;

    const notification = await notificationService.create({
      user_id,
      type,
      actor_id,
      activity_id,
      title,
      message,
    });

    res.created(notification);
  },

  markAsRead: async function (req, res) {
    const id = req.params.id;

    await notificationService.markAsRead(id);

    res.ok({ success: true });
  },
};

export default notificationController;