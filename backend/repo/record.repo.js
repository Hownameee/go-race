import db from '../utils/db/db.js';

const recordRepo = {
  findRecordsByUserId: async function (userId, offset, quantity) {
    const sql = `
        SELECT * FROM Record 
        WHERE owner_id = ? 
        ORDER BY record_id DESC 
        LIMIT ? OFFSET ?
        `;

    return await db.prepare(sql).all(userId, quantity, offset);
  },

  findNewRecordsByCurrentId: async function (userId, currentId) {
    const sql = `
        SELECT * FROM Record 
        WHERE owner_id = ? AND record_id > ?
        ORDER BY record_id DESC
        `;

    return await db.prepare(sql).all(userId, currentId);
  },

  findRecordByRecordId: async function (userId, recordId) {
    const sql = `SELECT * FROM Record WHERE owner_id = ? AND record_id = ?`;
    return await db.prepare(sql).get(userId, recordId);
  },

  create: async function (userId, recordData) {
    const sql = `INSERT INTO Record (owner_id, activity_type, start_time, end_time, duration_seconds, distance_km, calories_burned, heart_rate_avg, speed) 
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`;
    const params = [
      userId,
      recordData.activityType,
      recordData.startTime,
      recordData.endTime,
      recordData.duration,
      recordData.distance,
      recordData.calories,
      recordData.heartRate,
      recordData.speed,
    ];

    await db.prepare(sql).run(...params);
  },
};

export default recordRepo;
