import notificationService from '../services/notification.service.js';

const notificationController = {
  getList: async function (req, res) {
    const userId = 1; // hoặc req.user.id nếu auth
    const data = await notificationService.getNotifications(userId);
    res.ok(data);
  },

  createNotification: async function (req, res) {
    try {
      const io = req.app.get('io'); // Lấy socket.io server
      const { user_id, type, actor_id, activity_id, title, message } = req.body;
      console.log(user_id, " " , type, " ", actor_id, " ", activity_id, " ", title, " " ,message);

      // Tạo notification trong DB
      const notification = await notificationService.create({
        user_id,
        type,
        actor_id: actor_id || null,
        activity_id: activity_id || null,
        title,
        message,
      });

      // Gửi realtime qua socket
      notificationService.sendMessageByUserId(io, user_id, notification);
      // notificationService.sendMessageAllUsers(io, notification);

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
      res.ok({ success: true });
    } catch (err) {
      console.error(err);
      res.error(null, err.message);
    }
  },
};

export default notificationController;