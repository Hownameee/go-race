import postRepo from '../repo/post.repo.js';

const DEFAULT_LIMIT = 20;
const FAR_FUTURE = '9999-12-31T23:59:59.999Z';

const postService = {
  async createPost(payload) {
    if (!payload.title && !payload.description) {
      const error = new Error(
        'A post must have at least a title or description.',
      );
      error.status = 409;
      throw error;
    }

    return await postRepo.insertPost(payload);
  },

  async getFeed(cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectFeed(effectiveCursor, effectiveLimit);
    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { posts: rows, nextCursor };
  },

  async getFollowingFeed(userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectFollowingFeed(
      userId,
      effectiveCursor,
      effectiveLimit,
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { posts: rows, nextCursor };
  },

  async getMyPosts(userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectMyPosts(
      userId,
      effectiveCursor,
      effectiveLimit,
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { posts: rows, nextCursor };
  },

  async likePost(postId, userId) {
    const changes = await postRepo.insertLike(postId, userId);
    return { liked: changes > 0 };
  },

  async unlikePost(postId, userId) {
    const changes = await postRepo.deleteLike(postId, userId);
    return { unliked: changes > 0 };
  },

  async createComment(postId, userId, content) {
    if (!content || content.trim().length === 0) {
      const error = new Error('Comment content must not be empty.');
      error.status = 409;
      throw error;
    }

    return await postRepo.insertComment(postId, userId, content.trim());
  },

  async getComments(postId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectComments(
      postId,
      effectiveCursor,
      effectiveLimit,
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { comments: rows, nextCursor };
  },

  async deleteComment(postId, commentId, userId) {
    const changes = await postRepo.deleteComment(postId, commentId, userId);
    if (changes === 0) {
      const error = new Error('Comment not found or unauthorized to delete.');
      error.status = 404;
      throw error;
    }
    return { deleted: true };
  },
};

export default postService;
