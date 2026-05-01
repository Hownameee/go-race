import path from 'path';
import postRepo from '../repo/post.repo.js';
import clubRepo from '../repo/club.repo.js';
import { getImageUrlS3, uploadImageS3, resolveImageUrl } from '../utils/s3/s3.js';

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

const postService = {
  async createPost(payload, files = []) {
    if (!payload.title && !payload.description) {
      const error = new Error(
        'A post must have at least a title or description.',
      );
      error.status = 409;
      throw error;
    }

    if (payload.club_id) {
      const club = await clubRepo.findById(payload.club_id);
      if (!club) {
        const error = new Error('Club not found.');
        error.status = 404;
        throw error;
      }
      const member = await clubRepo.findByIdAndUserId(payload.owner_id, payload.club_id);
      if (!member || member.status !== 'approved') {
        const error = new Error('You must be a member of this club to post.');
        error.status = 403;
        throw error;
      }
    }

    const post = await postRepo.insertPost(payload);

    if (files && files.length > 0) {
      const uploadPromises = files.map(async (file, index) => {
        const extension = path.extname(file.originalname || '') || '.jpg';
        const s3Key = `posts/post-${post.post_id}-${Date.now()}-${index}${extension}`;
        
        await uploadImageS3(file.buffer, s3Key, file.mimetype);
        await postRepo.insertPostImage(post.post_id, s3Key);
        return s3Key;
      });
      await Promise.all(uploadPromises);
    }

    return post;
  },

  async resolvePostPhotos(rows) {
    return Promise.all(
      rows.map(async (row) => {
        const { record_s3_key, photos, ...postWithoutExtras } = row;
        
        let record_image_url = null;
        if (record_s3_key) {
          record_image_url = await getImageUrlS3(record_s3_key);
        }

        let photo_urls = [];
        if (photos) {
          const photoKeys = photos.split(',');
          photo_urls = await Promise.all(
            photoKeys.map(async (key) => await getImageUrlS3(key))
          );
        }

        return { 
          ...postWithoutExtras, 
          record_image_url,
          photo_urls 
        };
      }),
    );
  },

  async getFeed(cursor, limit) {
    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectFeed(effectiveCursor, effectiveLimit);
    const posts = await this.resolvePostPhotos(rows);

    const nextCursor =
      posts.length === effectiveLimit ? posts[posts.length - 1].created_at : null;

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

    const posts = await this.resolvePostPhotos(rows);

    const nextCursor =
      posts.length === effectiveLimit ? posts[posts.length - 1].created_at : null;

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

    const posts = await this.resolvePostPhotos(rows);

    const nextCursor =
      posts.length === effectiveLimit ? posts[posts.length - 1].created_at : null;

    return { posts, nextCursor };
  },

  async checkPostAccess(postId, userId) {
    const post = await postRepo.selectPostWithAccess(postId, userId);
    if (!post) {
      const error = new Error('Post not found.');
      error.status = 404;
      throw error;
    }

    if (post.privacy_type === 'private' && post.membership_status !== 'approved') {
      const error = new Error('You must be a member of this private club to perform this action.');
      error.status = 403;
      throw error;
    }

    return post;
  },

  async getClubPosts(clubId, userId, cursor, limit) {
    const club = await clubRepo.findById(clubId);
    if (!club) {
      const error = new Error('Club not found.');
      error.status = 404;
      throw error;
    }

    if (club.privacy_type === 'private') {
      const member = await clubRepo.findByIdAndUserId(userId, clubId);
      if (!member || member.status !== 'approved') {
        const error = new Error('You must be a member of this private club to see its posts.');
        error.status = 403;
        throw error;
      }
    }

    const effectiveCursor = cursor || FAR_FUTURE;
    const effectiveLimit = Math.min(parseInt(limit) || DEFAULT_LIMIT, 100);

    const rows = await postRepo.selectClubPosts(clubId, effectiveCursor, effectiveLimit);
    const posts = await this.resolvePostPhotos(rows);

    const nextCursor =
      rows.length === effectiveLimit ? rows[rows.length - 1].created_at : null;

    return { posts, nextCursor };
  },

  async likePost(postId, userId) {
    await this.checkPostAccess(postId, userId);
    const changes = await postRepo.insertLike(postId, userId);
    return { liked: changes > 0 };
  },

  async unlikePost(postId, userId) {
    await this.checkPostAccess(postId, userId);
    const changes = await postRepo.deleteLike(postId, userId);
    return { unliked: changes > 0 };
  },

  async createComment(postId, userId, content, parentId = null) {
    await this.checkPostAccess(postId, userId);
    if (!content || content.trim().length === 0) {
      const error = new Error('Comment content must not be empty.');
      error.status = 409;
      throw error;
    }

    return await postRepo.insertComment(postId, userId, content.trim(), parentId);
  },

  async likeComment(commentId, userId) {
    // Note: Comment like also ideally checks post access, but for simplicity we rely on comment existence
    // If strictness is needed, we'd find post_id from comment_id
    const changes = await postRepo.insertCommentLike(commentId, userId);
    return { liked: changes > 0 };
  },

  async unlikeComment(commentId, userId) {
    const changes = await postRepo.deleteCommentLike(commentId, userId);
    return { unliked: changes > 0 };
  },

  async getComments(postId, userId, cursor, limit) {
    await this.checkPostAccess(postId, userId);
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

    const comments = await attachAvatarUrls(rows);
    const nextCursor =
      comments.length === effectiveLimit ? comments[comments.length - 1].created_at : null;

    return { comments, nextCursor };
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
