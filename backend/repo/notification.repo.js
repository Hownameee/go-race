import db from '../utils/db/db.js';

const notificationRepository = {
  findByUserId: async function (userId, offset = 0, limit = 20) {
    const sql = `
      SELECT n.*, u.avatar_url AS actor_avatar_url 
      FROM notifications n
      LEFT JOIN USERS u ON n.actor_id = u.user_id
      WHERE n.user_id = ?
      ORDER BY n.created_at DESC
      LIMIT ? OFFSET ?
    `;
    return await db.prepare(sql).all(userId, limit, offset);
  },

  findById: async function (notificationId) {
    const sql = `SELECT * FROM notifications WHERE id = ?`;
    return await db.prepare(sql).get(notificationId);
  },

  create: async function ({
    user_id,
    type,
    actor_id,
    activity_id,
    title,
    message,
  }) {
    const sql = `
      INSERT INTO notifications (user_id, type, actor_id, activity_id, title, message)
      VALUES (?, ?, ?, ?, ?, ?)
    `;
    const params = [user_id, type, actor_id, activity_id, title, message];
    const info = await db.prepare(sql).run(...params);
    return info.lastInsertRowid;
  },

  markAsRead: async function (notificationId) {
    const sql = `UPDATE notifications SET \`read\` = 1 WHERE id = ?`;
    await db.prepare(sql).run(notificationId);
  },

  markAllAsRead: async function (userId) {
    const sql = `UPDATE notifications SET \`read\` = 1 WHERE user_id = ?`;
    const info = await db.prepare(sql).run(userId);
    return info.changes;
  },

  countUnreadByUserId: async function (userId) {
    const sql = `SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND \`read\` = 0`;
    const row = await db.prepare(sql).get(userId);
    return row.count;
  },
};

export default notificationRepository;
