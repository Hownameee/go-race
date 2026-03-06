import notificationService from '../services/notification.service.js';

const notificationController = {
  getList: async function (req, res) {
    const userId = req.user.userId;
    const data = await notificationService.getNotifications(userId);
    res.ok(data);
  },

  createNotification: async function (req, res) {
    const userId = req.params.userId;
    const { title, message } = req.body;
    await notificationService.sendNotification(userId, title, message);
    res.created();
  },
};

export default notificationController;
