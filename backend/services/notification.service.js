import notificationRepository from '../repo/notification.repo.js';
import deviceTokenService from './device-token.service.js';
import { getFirebaseAdmin } from '../utils/firebase/admin.js';
import userRepo from '../repo/user.repo.js';

function buildPayload(data, fallbackUserId) {
  return {
    id: String(data?.id ?? ''),
    user_id: String(data?.user_id ?? fallbackUserId ?? ''),
    type: String(data?.type ?? 'system'),
    actor_id: data?.actor_id == null ? '' : String(data.actor_id),
    activity_id: data?.activity_id == null ? '' : String(data.activity_id),
    title: String(data?.title ?? ''),
    message: String(data?.message ?? ''),
  };
}

async function handleInvalidTokens(responses, tokens) {
  const invalidTokens = [];

  responses.forEach((r, idx) => {
    if (!r.success) {
      const errorCode = r.error?.code;

      if (
        errorCode === 'messaging/registration-token-not-registered' ||
        errorCode === 'messaging/invalid-registration-token'
      ) {
        invalidTokens.push(tokens[idx]);
      }
    }
  });

  if (invalidTokens.length > 0) {
    console.log('[fcm] removing invalid tokens:', invalidTokens.length);
    await deviceTokenService.removeInvalidTokens(invalidTokens);
  }
}

const notificationService = {
  async createAndSend({
    userId,
    type,
    actorId = null,
    activityId = null,
    title,
    message,
  }) {
    if (userId === actorId) return;
    let user = null;
    if (actorId !== null) {
      user = userRepo.getUserById(userId);
    }
    const id = await notificationRepository.create({
      user_id: userId,
      type,
      actor_id: actorId,
      activity_id: activityId,
      title,
      message,
    });

    const notification = {
      id,
      user_id: userId,
      type,
      actor_id: actorId,
      activity_id: activityId,
      actor_avatar_url: user ? user.actor_avatar_url : null,
      title,
      message,
    };

    console.log(notification)

    if (type === 'system') {
      await this.sendPushAllUsers(notification);
    } else {
      console.log("send firebase");
      await this.sendPushByUserId(userId, notification);
    }

    return notification;
  },

  async getNotifications(userId) {
    return await notificationRepository.findByUserId(userId);
  },

  async markAsRead(id) {
    await notificationRepository.markAsRead(id);
  },

  async sendPushByUserId(userId, data) {
    const admin = getFirebaseAdmin();
    if (!admin) return;

    const tokens = await deviceTokenService.getTokensByUserId(userId);
    console.log(tokens);
    if (!tokens.length) return;

    const message = {
      tokens,
      android: { priority: 'high' },
      data: buildPayload(data, userId),
    };

    console.log("vao");

    try {
      const resp = await admin.messaging().sendEachForMulticast(message);

      console.log('[fcm] success:', resp.successCount, 'fail:', resp.failureCount);

      await handleInvalidTokens(resp.responses, tokens);

    } catch (e) {
      console.warn('[fcm] send failed:', e?.message || e);
    }
  },

  async sendPushAllUsers(data) {
    const admin = getFirebaseAdmin();
    if (!admin) return;

    const message = {
      topic: 'system-notifications',
      android: { priority: 'high' },
      data: buildPayload(data),
      notification: {
        title: String(data?.title ?? 'System'),
        body: String(data?.message ?? ''),
      },
    };

    try {
      const res = await admin.messaging().send(message);
      console.log('[fcm] broadcast success:', res);
    } catch (e) {
      console.warn('[fcm] broadcast failed:', e?.message || e);
    }
  },
};

export default notificationService;