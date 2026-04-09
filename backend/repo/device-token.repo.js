import db from '../utils/db/db.js';

const deviceTokenRepository = {
  upsert: async function ({ userId, token, platform = 'android' }) {
    const sql = `
      INSERT INTO device_tokens (user_id, token, platform, updated_at)
      VALUES (?, ?, ?, CURRENT_TIMESTAMP)
      ON CONFLICT(token) DO UPDATE SET
        user_id = excluded.user_id,
        platform = excluded.platform,
        updated_at = CURRENT_TIMESTAMP
    `;
    const info = await db.prepare(sql).run(userId, token, platform);
    return info.lastInsertRowid;
  },

  findByUserId: async function (userId) {
    const sql = `
      SELECT token
      FROM device_tokens
      WHERE user_id = ?
      ORDER BY updated_at DESC
    `;
    return await db.prepare(sql).all(userId);
  },

  findAllTokens: async function () {
    const sql = `
      SELECT token
      FROM device_tokens
      WHERE token IS NOT NULL AND token != ''
    `;
    return await db.prepare(sql).all();
  },

  deleteByTokens: async function (tokens) {
    if (!Array.isArray(tokens) || tokens.length === 0) return;
    const placeholders = tokens.map(() => '?').join(',');
    const sql = `DELETE FROM device_tokens WHERE token IN (${placeholders})`;
    await db.prepare(sql).run(...tokens);
  },
};

export default deviceTokenRepository;
