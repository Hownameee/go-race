import db from '../utils/db/db.js';

const recordRepo = {
  getWeeklySummaryRows(userId, activityType, fromDate, toDate) {
    const sql = `
      SELECT
        date(r.start_time, 'weekday 1', '-7 days') AS week_start,
        date(date(r.start_time, 'weekday 1', '-7 days'), '+6 days') AS week_end,
        ROUND(COALESCE(SUM(r.distance_km), 0), 2) AS total_distance_km,
        COALESCE(SUM(r.duration_seconds), 0) AS total_duration_seconds,
        ROUND(COALESCE(SUM(r.elevation_gain_m), 0), 2) AS total_elevation_gain_m
      FROM RECORD r
      WHERE
        r.activity_type = ?
        AND datetime(r.start_time) >= datetime(?)
        AND datetime(r.start_time) <= datetime(?)
        AND (
          r.owner_id = ?
          OR r.user_id = ?
        )
      GROUP BY week_start, week_end
      ORDER BY week_start ASC
    `;

    return db.prepare(sql).all(activityType, fromDate, toDate, userId, userId);
  },

  findRecordsByUserId: async function (userId, offset, quantity) {
    const sql = `
      SELECT * FROM Record 
      WHERE owner_id = ? 
      ORDER BY record_id DESC 
      LIMIT ? OFFSET ?
    `;

    return await db.prepare(sql).all(userId, quantity, offset);
  },

  findRecordByRecordId: async function (userId, recordId) {
    const sql = `SELECT * FROM Record WHERE owner_id = ? AND record_id = ?`;
    return await db.prepare(sql).get(userId, recordId);
  },

  create: async function (userId, recordData) {
    const sql = `INSERT INTO Record (owner_id, activity_type, title, start_time, end_time, duration_seconds, distance_km, calories_burned, heart_rate_avg, speed) 
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`;
    const params = [
      userId,
      recordData.activityType,
      recordData.title,
      recordData.startTime,
      recordData.endTime,
      recordData.duration,
      recordData.distance,
      recordData.calories,
      recordData.heartRate,
      recordData.speed,
    ];

    const info = await db.prepare(sql).run(...params);
    return info.lastInsertRowid;
  },

  update: async function (userId, recordId, updateData) {
    const keys = Object.keys(updateData);
    if (keys.length === 0) return;

    const setClause = keys.map((key) => `${key} = ?`).join(', ');
    const sql = `UPDATE Record SET ${setClause} WHERE owner_id = ? AND record_id = ?`;
    const params = [...Object.values(updateData), userId, recordId];

    await db.prepare(sql).run(...params);
  },
};

export default recordRepo;
