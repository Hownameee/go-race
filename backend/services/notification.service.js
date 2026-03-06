import notificationRepository from '../repo/notification.repo.js';

const notificationService = {
  getNotifications: async function (userId) {
    const data = await notificationRepository.getList(userId);
    console.log(data);
    return data;
  },

  sendNotification: async function (userId, title, message) {
    const data = {
      userId,
      title,
      message,
      isRead: false,
      createdAt: new Date(),
    };

    return notificationRepository.create(data);
  },
};

export default notificationService;
