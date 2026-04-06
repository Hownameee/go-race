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

  async insertComment(postId, userId, content) {
    const tx = db.transaction((pId, uId, text) => {
      const stmt = db.prepare(
        `INSERT INTO COMMENT (post_id, user_id, content) VALUES (?, ?, ?) RETURNING *`,
      );
      const result = stmt.run(pId, uId, text);

      if (result.changes > 0) {
        postRepo.updateCommentCount(pId, 1);
      }

      return result;
    });

    return tx(postId, userId, content);
  },

  async deleteComment(postId, commentId, userId) {
    const tx = db.transaction((pId, cId, uId) => {
      const stmt = db.prepare(
        `DELETE FROM COMMENT WHERE comment_id = ? AND post_id = ? AND user_id = ?`,
      );
      const result = stmt.run(cId, pId, uId);

      if (result.changes > 0) {
        postRepo.updateCommentCount(pId, -1);
      }

      return result.changes;
    });

    return tx(postId, commentId, userId);
  },

  async selectComments(postId, cursor, limit) {
    const stmt = db.prepare(
      `SELECT c.comment_id, c.post_id, c.user_id, c.content, c.created_at,
              u.username, u.fullname, u.avatar_url
       FROM COMMENT c
       JOIN USERS u ON u.user_id = c.user_id
       WHERE c.post_id = ? AND c.created_at < ?
       ORDER BY c.created_at DESC
       LIMIT ?`,
    );
    return stmt.all(postId, cursor, limit);
  },
};

export default postRepo;
