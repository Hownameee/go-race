import notificationRepository from '../repo/notification.repo.js';

const notificationService = {
  // tạo notification trả về data
  create: async function (data) {
    const id = await notificationRepository.create(data);

    return {
      id,
      ...data,
    };
  },

  // lấy danh sách notification của user theo id
  getNotifications: async function (userId) {
    return await notificationRepository.findByUserId(userId);
  },

  // đánh dấu notification đã đọc
  markAsRead: async function (id) {
    await notificationRepository.markAsRead(id);
  },

  sendMessageByUserId: function (io, userId, data) {
    const payload = {
      ...data,
      createdAt: new Date(),
    };
    io.to(`user_${userId}`).emit('notification', payload);

    // Log thông báo
    console.log(`Notification sent to user_${userId}:`, payload);
  },
  
  sendMessageAllUsers: function (io, data) {
    const payload = {
      ...data,
      createdAt: new Date(),
    };

    io.emit('notification', payload);
    console.log(`Notification sent to all clients:`, payload);
  },
};

export default notificationService;