import db from '../utils/db/db.js';

const followRepo = {
  async insertFollow(followerId, followingId) {
    const stmt = db.prepare(
      `INSERT OR IGNORE INTO FOLLOW (follower_id, following_id) VALUES (?, ?) RETURNING *`,
    );
    return stmt.get(followerId, followingId);
  },

  async deleteFollow(followerId, followingId) {
    const stmt = db.prepare(
      `DELETE FROM FOLLOW WHERE follower_id = ? AND following_id = ?`,
    );
    return stmt.run(followerId, followingId);
  },

  async selectFollowers(userId, cursor, limit) {
    let sql = `
      SELECT u.user_id, u.username, u.fullname, u.avatar_url, f.created_at
      FROM FOLLOW f
      JOIN USERS u ON u.user_id = f.follower_id
      WHERE f.following_id = ? AND f.created_at < ?
      ORDER BY f.created_at DESC
    `;

    const params = [userId, cursor];

    if (limit != null) {
      sql += ` LIMIT ?`;
      params.push(limit);
    }

    const stmt = db.prepare(sql);
    return stmt.all(...params);
  },

  async selectFollowing(userId, cursor, limit) {
    const stmt = db.prepare(
      `SELECT u.user_id, u.username, u.fullname, u.avatar_url, f.created_at
         FROM FOLLOW f
         JOIN USERS u ON u.user_id = f.following_id
         WHERE f.follower_id = ? AND f.created_at < ?
         ORDER BY f.created_at DESC
         LIMIT ?`,
    );
    return stmt.all(userId, cursor, limit);
  },

  countFollowers: async function (userId) {
    const sql = `
    SELECT COUNT(*) AS total
    FROM FOLLOW
    WHERE following_id = ?
  `;
    const row = await db.prepare(sql).get(userId);
    return row.total;
  },

  countFollowings: async function (userId) {
    const sql = `
    SELECT COUNT(*) AS total
    FROM FOLLOW
    WHERE follower_id = ?
  `;
    const row = await db.prepare(sql).get(userId);
    return row.total;
  },

};

export default followRepo;
