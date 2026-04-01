import notificationService from '../services/notification.service.js';

const notificationController = {
  getList: async function (req, res) {
    const userId = 1; // hoặc req.user.id nếu auth
    const data = await notificationService.getNotifications(userId);
    console.log(data);
    res.ok({
      notifications: data,
      nextCursor: null,
    });
  },

  createNotification: async function (req, res) {
    try {
      const { user_id, type, actor_id, activity_id, title, message } =
        req.body;

      // Tạo notification trong DB
      const notification = await notificationService.create({
        user_id,
        type,
        actor_id: actor_id || null,
        activity_id: activity_id || null,
        title,
        message,
      });

      const shouldSendAll = type === 'system';
      console.log(`Notification created for user_id=${user_id}, type=${type}, shouldSendAll=${shouldSendAll}`);
      if (shouldSendAll) {
        // Send push (FCM) so all users receive when app is inactive
        await notificationService.sendPushAllUsers(notification);
      } else {
        // Send push (FCM) so user receives when app is inactive
        await notificationService.sendPushByUserId(user_id, notification);
      }

      res.created(notification, 'Notification created');
    } catch (err) {
      console.error(err);
      res.error(null, err.message);
    }
  },

  markAsRead: async function (req, res) {
    try {
      const id = req.params.id;
      await notificationService.markAsRead(id);
      res.ok();
    } catch (err) {
      res.error(null, err.message);
    }
  },
};

export default notificationController;
