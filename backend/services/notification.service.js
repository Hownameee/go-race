import notificationRepository from '../repo/notification.repo.js';
import deviceTokenService from './device-token.service.js';
import { getFirebaseAdmin } from '../utils/firebase/admin.js';

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

  sendPushByUserId: async function (userId, data) {
    const admin = getFirebaseAdmin();
    if (!admin) return;

    const tokens = await deviceTokenService.getTokensByUserId(userId);
    if (!tokens.length) return;

    const message = {
      tokens,
      android: {
        priority: 'high',
      },
      data: {
        id: String(data?.id ?? ''),
        user_id: String(data?.user_id ?? userId),
        type: String(data?.type ?? 'system'),
        actor_id: data?.actor_id == null ? '' : String(data.actor_id),
        activity_id: data?.activity_id == null ? '' : String(data.activity_id),
        title: String(data?.title ?? ''),
        message: String(data?.message ?? ''),
      },
    };

    try {
      const resp = await admin.messaging().sendEachForMulticast(message);
      console.log(
        '[fcm] sent:',
        resp.successCount,
        'failed:',
        resp.failureCount,
      );
    } catch (e) {
      console.warn('[fcm] send failed:', e?.message || e);
    }
  },

  sendPushAllUsers: async function (data) {
    const admin = getFirebaseAdmin();
    if (!admin) return;

    const message = {
      topic: 'system-notifications',
      android: {
        priority: 'high',
      },
      data: {
        id: String(data?.id ?? ''),
        user_id: String(data?.user_id ?? ''),
        type: String(data?.type ?? 'system'),
        actor_id: data?.actor_id == null ? '' : String(data.actor_id),
        activity_id: data?.activity_id == null ? '' : String(data.activity_id),
        title: String(data?.title ?? ''),
        message: String(data?.message ?? ''),
      },
      notification: {
        title: String(data?.title ?? 'Thông báo hệ thống'),
        body: String(data?.message ?? ''),
      },
    };

    try {
      const response = await admin.messaging().send(message);
      console.log('[fcm] Topic broadcast sent successfully:', response);
    } catch (e) {
      console.warn('[fcm] Topic broadcast failed:', e?.message || e);
    }
  },
};

export default notificationService;
