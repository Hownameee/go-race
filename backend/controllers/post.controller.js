import postService from '../services/post.service.js';

const postController = {
  async createPost(req, res, next) {
    try {
      const userId = req.user.userId;
      const fullname = req.user.fullname;
      const newPost = await postService.createPost({
        owner_id: userId,
        fullname: fullname,
        ...req.body,
        owner_id: userId,
      }, req.files);
      return res.created(newPost, 'Post created successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getFeed(req, res, next) {
    try {
      const userId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await postService.getFeed(userId, cursor, limit);
      return res.ok(result, 'Feed retrieved successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getFollowingFeed(req, res, next) {
    try {
      const userId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await postService.getFollowingFeed(userId, cursor, limit);
      return res.ok(result, 'Following feed retrieved successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getMyPosts(req, res, next) {
    try {
      const userId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await postService.getMyPosts(userId, cursor, limit);
      return res.ok(result, 'My posts retrieved successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getUserPosts(req, res, next) {
    try {
      const targetUserId = parseInt(req.params.userId);
      const currentUserId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await postService.getUserPosts(targetUserId, currentUserId, cursor, limit);
      return res.ok(result, 'User posts retrieved successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async likePost(req, res, next) {
    try {
      const postId = parseInt(req.params.postId);
      const userId = req.user.userId;
      const fullname = req.user.fullname;
      const result = await postService.likePost(postId, userId, fullname);
      return res.created(result, 'Post liked successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async unlikePost(req, res, next) {
    try {
      const postId = parseInt(req.params.postId);
      const userId = req.user.userId;
      const result = await postService.unlikePost(postId, userId);
      return res.ok(result, 'Post unliked successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async createComment(req, res, next) {
    try {
      const postId = parseInt(req.params.postId);
      const userId = req.user.userId;
      const fullname = req.user.fullname;
      const { content, parentId } = req.body;
      const comment = await postService.createComment(
        postId,
        userId,
        content,
        parentId,
        fullname,
      );
      return res.created(comment, 'Comment created successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getComments(req, res, next) {
    try {
      const postId = parseInt(req.params.postId);
      const userId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await postService.getComments(
        postId,
        userId,
        cursor,
        limit,
      );
      return res.ok(result, 'Comments retrieved successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async deleteComment(req, res, next) {
    try {
      const postId = parseInt(req.params.postId);
      const commentId = parseInt(req.params.commentId);
      const userId = req.user.userId;
      const result = await postService.deleteComment(postId, commentId, userId);
      return res.ok(result, 'Comment deleted successfully.');
    } catch (error) {
      if (error.status === 409 || error.status === 404) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async likeComment(req, res, next) {
    try {
      const commentId = parseInt(req.params.commentId);
      const userId = req.user.userId;
      const fullname = req.user.fullname;
      const result = await postService.likeComment(commentId, userId, fullname);
      return res.created(result, 'Comment liked successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async unlikeComment(req, res, next) {
    try {
      const commentId = parseInt(req.params.commentId);
      const userId = req.user.userId;
      const result = await postService.unlikeComment(commentId, userId);
      return res.ok(result, 'Comment unliked successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  async getReplies(req, res, next) {
    try {
      const commentId = parseInt(req.params.commentId);
      const userId = req.user.userId;
      const { cursor, limit } = req.query;
      const result = await postService.getReplies(
        commentId,
        userId,
        cursor,
        limit,
      );
      return res.ok(result, 'Replies retrieved successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },
};

export default postController;
