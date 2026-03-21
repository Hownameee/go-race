import notificationRepository from '../repo/notification.repo.js';

const notificationService = {
  create: async function (data) {
    const id = await notificationRepository.create(data);

    return {
      id,
      ...data,
    };
  },

  getNotifications: async function (userId) {
    return await notificationRepository.findByUserId(userId);
  },

  markAsRead: async function (id) {
    await notificationRepository.markAsRead(id);
  },
};

export default notificationService;