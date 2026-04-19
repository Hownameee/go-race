import followService from '../services/follow.service.js';

const followController = {
  async follow(req, res, next) {
    try {
      const followerId = req.user.userId;
      const followingId = req.params.followingId;
      const followerName = req.user.fullname;
      const newFollow = await followService.followUser(followerId, followingId, followerName);
      return res.created(newFollow, 'Followed successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async unfollow(req, res, next) {
    try {
      const followerId = req.user.userId;
      const followingId = req.params.followingId;
      await followService.unfollowUser(followerId, followingId);
      return res.ok(null, 'Unfollowed successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getFollowers(req, res, next) {
    try {
      const userId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await followService.getFollowers(userId, cursor, limit);
      return res.ok(result, 'Get followers successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getFollowing(req, res, next) {
    try {
      const userId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await followService.getFollowing(userId, cursor, limit);
      return res.ok(result, 'Get following successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getUserFollowers(req, res, next) {
    try {
      const userId = Number(req.params.userId);
      const { cursor, limit } = req.query;
      const result = await followService.getFollowers(userId, cursor, limit);
      return res.ok(result, 'Get user followers successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getUserFollowing(req, res, next) {
    try {
      const userId = Number(req.params.userId);
      const { cursor, limit } = req.query;
      const result = await followService.getFollowing(userId, cursor, limit);
      return res.ok(result, 'Get user following successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },
};

export default followController;
