import db from '../utils/db/db.js';

const postRepo = {
  async insertPost({
    owner_id,
    record_id,
    title,
    description,
    photo_url,
    view_mode,
  }) {
    const stmt = db.prepare(
      `INSERT INTO POST (owner_id, record_id, title, description, photo_url, view_mode)
       VALUES (?, ?, ?, ?, ?, ?)
       RETURNING *`,
    );
    return stmt.get(
      owner_id,
      record_id ?? null,
      title ?? null,
      description ?? null,
      photo_url ?? null,
      view_mode,
    );
  },

  async selectFeed(cursor, limit) {
    const stmt = db.prepare(
      `SELECT p.*, u.username, u.fullname, u.avatar_url,
              r.activity_type, r.duration_seconds, r.distance_km, r.speed, r.s3_key
       FROM POST p
       JOIN USERS u ON u.user_id = p.owner_id
       LEFT JOIN RECORD r ON r.record_id = p.record_id
       WHERE p.created_at < ?
       ORDER BY p.created_at DESC
       LIMIT ?`,
    );
    return stmt.all(cursor, limit);
  },

  async selectFollowingFeed(userId, cursor, limit) {
    const stmt = db.prepare(
      `SELECT p.*, u.username, u.fullname, u.avatar_url,
              r.activity_type, r.duration_seconds, r.distance_km, r.speed, r.s3_key
       FROM POST p
       JOIN FOLLOW f ON f.following_id = p.owner_id
       JOIN USERS u ON u.user_id = p.owner_id
       LEFT JOIN RECORD r ON r.record_id = p.record_id
       WHERE f.follower_id = ? AND p.created_at < ?
       ORDER BY p.created_at DESC
       LIMIT ?`,
    );
    return stmt.all(userId, cursor, limit);
  },

  async selectMyPosts(userId, cursor, limit) {
    const stmt = db.prepare(
      `SELECT p.*, u.username, u.fullname, u.avatar_url,
              r.activity_type, r.duration_seconds, r.distance_km, r.speed, r.s3_key
       FROM POST p
       JOIN USERS u ON u.user_id = p.owner_id
       LEFT JOIN RECORD r ON r.record_id = p.record_id
       WHERE p.owner_id = ? AND p.created_at < ?
       ORDER BY p.created_at DESC
       LIMIT ?`,
    );
    return stmt.all(userId, cursor, limit);
  },

  updateLikeCount(postId, delta) {
    const stmt = db.prepare(
      `UPDATE POST SET like_count = like_count + ? WHERE post_id = ?`,
    );
    return stmt.run(delta, postId);
  },

  updateCommentCount(postId, delta) {
    const stmt = db.prepare(
      `UPDATE POST SET comment_count = comment_count + ? WHERE post_id = ?`,
    );
    return stmt.run(delta, postId);
  },

  updateReplyCount(commentId, delta) {
    const stmt = db.prepare(
      `UPDATE COMMENT SET reply_count = reply_count + ? WHERE comment_id = ?`,
    );
    return stmt.run(delta, commentId);
  },

  updateCommentLikeCount(commentId, delta) {
    const stmt = db.prepare(
      `UPDATE COMMENT SET like_count = like_count + ? WHERE comment_id = ?`,
    );
    return stmt.run(delta, commentId);
  },

  async insertLike(postId, userId) {
    const tx = db.transaction((pId, uId) => {
      const stmt = db.prepare(
        `INSERT OR IGNORE INTO LIKE (post_id, user_id) VALUES (?, ?)`,
      );
      const result = stmt.run(pId, uId);

      if (result.changes > 0) {
        postRepo.updateLikeCount(pId, 1);
      }

      return result.changes;
    });

    return tx(postId, userId);
  },

  async deleteLike(postId, userId) {
    const tx = db.transaction((pId, uId) => {
      const stmt = db.prepare(
        `DELETE FROM LIKE WHERE post_id = ? AND user_id = ?`,
      );
      const result = stmt.run(pId, uId);

      if (result.changes > 0) {
        postRepo.updateLikeCount(pId, -1);
      }

      return result.changes;
    });

    return tx(postId, userId);
  },

  async insertCommentLike(commentId, userId) {
    const tx = db.transaction((cId, uId) => {
      const stmt = db.prepare(
        `INSERT OR IGNORE INTO COMMENT_LIKE (comment_id, user_id) VALUES (?, ?)`,
      );
      const result = stmt.run(cId, uId);

      if (result.changes > 0) {
        postRepo.updateCommentLikeCount(cId, 1);
      }

      return result.changes;
    });

    return tx(commentId, userId);
  },

  async deleteCommentLike(commentId, userId) {
    const tx = db.transaction((cId, uId) => {
      const stmt = db.prepare(
        `DELETE FROM COMMENT_LIKE WHERE comment_id = ? AND user_id = ?`,
      );
      const result = stmt.run(cId, uId);

      if (result.changes > 0) {
        postRepo.updateCommentLikeCount(cId, -1);
      }

      return result.changes;
    });

    return tx(commentId, userId);
  },

  async insertComment(postId, userId, content, parentId = null) {
    const tx = db.transaction((pId, uId, text, parId) => {
      const stmt = db.prepare(
        `INSERT INTO COMMENT (post_id, user_id, content, parent_id) VALUES (?, ?, ?, ?) RETURNING *`,
      );
      const result = stmt.run(pId, uId, text, parId);

      if (result.changes > 0) {
        postRepo.updateCommentCount(pId, 1);
        if (parId) {
          postRepo.updateReplyCount(parId, 1);
        }
      }

      return result;
    });

    return tx(postId, userId, content, parentId);
  },

  async deleteComment(postId, commentId, userId) {
    const tx = db.transaction((pId, cId, uId) => {
      const checkStmt = db.prepare(`SELECT parent_id FROM COMMENT WHERE comment_id = ? AND post_id = ? AND user_id = ?`);
      const commentTarget = checkStmt.get(cId, pId, uId);
      if (!commentTarget) return 0;

      const stmt = db.prepare(
        `DELETE FROM COMMENT WHERE comment_id = ? AND post_id = ? AND user_id = ?`,
      );
      const result = stmt.run(cId, pId, uId);

      if (result.changes > 0) {
        postRepo.updateCommentCount(pId, -1);
        if (commentTarget.parent_id) {
          postRepo.updateReplyCount(commentTarget.parent_id, -1);
        }
      }

      return result.changes;
    });

    return tx(postId, commentId, userId);
  },

  async selectComments(postId, currentUserId, cursor, limit) {
    const stmt = db.prepare(
      `SELECT c.comment_id, c.post_id, c.user_id, c.content, c.created_at, c.like_count, c.reply_count, c.parent_id,
              u.username, u.fullname, u.avatar_url,
              CASE WHEN cl.user_id IS NOT NULL THEN 1 ELSE 0 END as is_liked
       FROM COMMENT c
       JOIN USERS u ON u.user_id = c.user_id
       LEFT JOIN COMMENT_LIKE cl ON cl.comment_id = c.comment_id AND cl.user_id = ?
       WHERE c.post_id = ? AND c.parent_id IS NULL AND c.created_at < ?
       ORDER BY c.created_at DESC
       LIMIT ?`,
    );
    return stmt.all(currentUserId, postId, cursor, limit);
  },

  async selectReplies(commentId, currentUserId, cursor, limit) {
    const stmt = db.prepare(
      `SELECT c.comment_id, c.post_id, c.user_id, c.content, c.created_at, c.like_count, c.reply_count, c.parent_id,
              u.username, u.fullname, u.avatar_url,
              CASE WHEN cl.user_id IS NOT NULL THEN 1 ELSE 0 END as is_liked
       FROM COMMENT c
       JOIN USERS u ON u.user_id = c.user_id
       LEFT JOIN COMMENT_LIKE cl ON cl.comment_id = c.comment_id AND cl.user_id = ?
       WHERE c.parent_id = ? AND c.created_at < ?
       ORDER BY c.created_at DESC
       LIMIT ?`,
    );
    return stmt.all(currentUserId, commentId, cursor, limit);
  },

  async getPostOwner(postId) {
    const stmt = db.prepare(
      `SELECT owner_id FROM POST WHERE post_id = ?`
    );
    return stmt.get(postId);
  },

  async getCommentOwner(commentId) {
    const stmt = db.prepare(
      `SELECT user_id FROM COMMENT WHERE comment_id = ?`
    );
    return stmt.get(commentId);
  },

  async getPostFromCommentId(commentId) {
    const stmt = db.prepare(
      `SELECT post_id FROM COMMENT WHERE comment_id = ?`
    );
    return stmt.get(commentId);
  },
};

export default postRepo;
