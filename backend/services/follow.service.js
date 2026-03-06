import followRepo from "../repo/follow.repo.js";

const DEFAULT_LIMIT = 20;
const FAR_FUTURE = "9999-12-31T23:59:59.999Z";

const followService = {
  async followUser(followerId, followingId) {
    if (followerId === followingId) {
      const error = new Error("You cannot follow yourself.");
      error.status = 409;
      throw error;
    }
    const newFollow = await followRepo.insertFollow(followerId, followingId);

    if (!newFollow) {
      throw new Error("Already following this user.");
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

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { followers: rows, nextCursor };
  },

  async getFollowing(userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await followRepo.selectFollowing(
      userId,
      effectiveCursor,
      effectiveLimit,
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { following: rows, nextCursor };
  },
};

export default followService;
