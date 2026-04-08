import followRepo from '../repo/follow.repo.js';
import notificationService from './notification.service.js';
import { resolveImageUrl } from '../utils/s3/s3.js';

const DEFAULT_LIMIT = 20;
const FAR_FUTURE = '9999-12-31T23:59:59.999Z';

async function attachAvatarUrls(items) {
  return Promise.all(
    (items || []).map(async (item) => ({
      ...item,
      avatar_url: await resolveImageUrl(item.avatar_url),
    })),
  );
}

const followService = {
  async followUser(followerId, followingId, followerName) {
    if (followerId === followingId) {
      const error = new Error('You cannot follow yourself.');
      error.status = 409;
      throw error;
    }
    const newFollow = await followRepo.insertFollow(followerId, followingId);

    if (!newFollow) {
      throw new Error('Already following this user.');
    }

    try {
      await notificationService.createAndSend({
        userId: followingId,          
        type: "follow",
        actorId: followerId,          
        title: "New follower",
        message: `${followerName} started following you`,
      });
    } catch (err) {
      console.error("[follow][notification error]", err);
    }

    return newFollow;
  },

  async unfollowUser(followerId, followingId) {
    return await followRepo.deleteFollow(followerId, followingId);
  },

  async getFollowers(userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await followRepo.selectFollowers(
      userId,
      effectiveCursor,
      effectiveLimit,
    );
    const followers = await attachAvatarUrls(rows);

    const nextCursor =
      followers.length === effectiveLimit ? followers[followers.length - 1].created_at : null;

    return { followers, nextCursor };
  },

  async getFollowing(userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await followRepo.selectFollowing(
      userId,
      effectiveCursor,
      effectiveLimit,
    );
    const following = await attachAvatarUrls(rows);

    const nextCursor =
      following.length === effectiveLimit ? following[following.length - 1].created_at : null;

    return { following, nextCursor };
  },

  async countFollowers(userId) {
    return await followRepo.countFollowers(userId);
  },

  async countFollowings(userId) {
    return await followRepo.countFollowings(userId);
  }
};

export default followService;
