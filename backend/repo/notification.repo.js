import db from '../utils/db/db.js';

const notificationRepository = {
  getList: async function (userId) {
    const sql = `
      SELECT * 
      FROM NOTIFICATION
      WHERE user_id = ?
      ORDER BY created_at DESC
    `;

    return await db.prepare(sql).all(userId);
  },

  create: async function (data) {
    const sql = `
      INSERT INTO NOTIFICATION (user_id, title, message, type)
      VALUES (?, ?, ?, ?)
    `;

    const result = await db
      .prepare(sql)
      .run(data.userId, data.title, data.message, data.type);

    return result;
  },

  markAsRead: async function (notificationId) {
    const sql = `
      UPDATE NOTIFICATION
      SET is_read = 1
      WHERE notification_id = ?
    `;

    return await db.prepare(sql).run(notificationId);
  },
};

export default notificationRepository;
