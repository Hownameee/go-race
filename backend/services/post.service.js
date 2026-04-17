import postRepo from '../repo/post.repo.js';
import { getImageUrlS3 } from '../utils/s3/s3.js';
import followRepo from '../repo/follow.repo.js';
import userRepo from '../repo/user.repo.js';
import notificationService from './notification.service.js';

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

    const newPost = await postRepo.insertPost(payload);

    try {
      const fullname = payload.fullname;
      const effectiveCursor = FAR_FUTURE;

      const followers = await followRepo.selectFollowers(
        payload.owner_id,
        effectiveCursor,
        null,
      );

      for (const follower of followers) {
        await notificationService.createAndSend({
          userId: follower.user_id,
          type: "post",
          actorId: payload.owner_id,
          activityId: newPost.post_id,
          title: "New Post",
          message: `${fullname} just published a new post`,
        });
      }
    } catch (err) {
      console.error("[post][notification error]", err);
    }

    return newPost;
  },

  async getFeed(cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectFeed(effectiveCursor, effectiveLimit);

    const posts = await Promise.all(
      rows.map(async (row) => {
        const { s3_key, ...postWithoutS3Key } = row;
        if (s3_key) {
          const record_image_url = await getImageUrlS3(s3_key);
          return { ...postWithoutS3Key, record_image_url };
        }
        return postWithoutS3Key;
      }),
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { posts, nextCursor };
  },

  async getFollowingFeed(userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectFollowingFeed(
      userId,
      effectiveCursor,
      effectiveLimit,
    );

    const posts = await Promise.all(
      rows.map(async (row) => {
        const { s3_key, ...postWithoutS3Key } = row;
        if (s3_key) {
          const record_image_url = await getImageUrlS3(s3_key);
          return { ...postWithoutS3Key, record_image_url };
        }
        return postWithoutS3Key;
      }),
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { posts, nextCursor };
  },

  async getMyPosts(userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectMyPosts(
      userId,
      effectiveCursor,
      effectiveLimit,
    );

    const posts = await Promise.all(
      rows.map(async (row) => {
        const { s3_key, ...postWithoutS3Key } = row;
        if (s3_key) {
          const record_image_url = await getImageUrlS3(s3_key);
          return { ...postWithoutS3Key, record_image_url };
        }
        return postWithoutS3Key;
      }),
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { posts, nextCursor };
  },

  async likePost(postId, userId, fullname) {
    const changes = await postRepo.insertLike(postId, userId);

    const owner_id = await postRepo.getPostOwner(postId);
    try {
      await notificationService.createAndSend({
        userId: owner_id.owner_id,
        type: "post",
        actorId: userId,
        activityId: postId,
        title: "New Like Post",
        message: `${fullname} just liked your post`,
      });
    } catch (err) {
      console.error("[like post][notification error]", err);
    }

    return { liked: changes > 0 };
  },

  async unlikePost(postId, userId) {
    const changes = await postRepo.deleteLike(postId, userId);
    return { unliked: changes > 0 };
  },

  async createComment(postId, userId, content, parentId = null, fullname) {
    if (!content || content.trim().length === 0) {
      const error = new Error('Comment content must not be empty.');
      error.status = 409;
      throw error;
    }

    const newComment = await postRepo.insertComment(postId, userId, content.trim(), parentId);
    let owner_id;
    let message;
    if (parentId != null) {
      const parentComment = await postRepo.getCommentOwner(parentId);
      owner_id = parentComment.user_id;
      message = `${fullname} just replied to your comment`;
    } else {
      owner_id = await postRepo.getPostOwner(postId);
      owner_id = owner_id.owner_id;
      message = `${fullname} just commented on your post`;
    }

    try {
      await notificationService.createAndSend({
        userId: owner_id,
        type: "comment",
        actorId: userId,
        activityId: postId,
        title: "New Comment",
        message: `${message}`,
      });
    } catch (err) {
      console.error("[comment][notification error]", err);
    }

    return newComment;
  },

  async likeComment(commentId, userId, fullname) {
    const changes = await postRepo.insertCommentLike(commentId, userId);
    const postId = await postRepo.getPostFromCommentId(commentId);
    const owner_id = await postRepo.getCommentOwner(commentId);
    try {
      await notificationService.createAndSend({
        userId: owner_id.user_id,
        type: "comment",
        actorId: userId,
        activityId: postId.post_id,
        title: "New Like Comment",
        message: `${fullname} just liked your comment`,
      });
    } catch (err) {
      console.error("[like comment][notification error]", err);
    }

    return { liked: changes > 0 };
  },

  async unlikeComment(commentId, userId) {
    const changes = await postRepo.deleteCommentLike(commentId, userId);
    return { unliked: changes > 0 };
  },

  async getComments(postId, userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectComments(
      postId,
      userId,
      effectiveCursor,
      effectiveLimit,
    );

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { comments: rows, nextCursor };
  },

  async getReplies(commentId, userId, cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectReplies(
      commentId,
      userId,
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
