import db from '../utils/db/db.js';

const recordRepo = {
  getAggregateStats(
    userId,
    activityType = null,
    fromDate = null,
    toDate = null,
  ) {
    const sql = `
      SELECT
        COUNT(*) AS total_activities,
        ROUND(COALESCE(SUM(r.distance_km), 0), 2) AS total_distance_km,
        COALESCE(SUM(r.duration_seconds), 0) AS total_duration_seconds
      FROM RECORD r
      WHERE
        (? IS NULL OR r.activity_type = ?)
        AND (? IS NULL OR datetime(r.start_time) >= datetime(?))
        AND (? IS NULL OR datetime(r.start_time) <= datetime(?))
        AND r.owner_id = ?
    `;

    return db
      .prepare(sql)
      .get(
        activityType,
        activityType,
        fromDate,
        fromDate,
        toDate,
        toDate,
        userId,
      );
  },

  getWeeklySummaryRows(userId, activityType, fromDate, toDate) {
    const sql = `
      SELECT
        date(r.start_time, 'weekday 1', '-7 days') AS week_start,
        date(date(r.start_time, 'weekday 1', '-7 days'), '+6 days') AS week_end,
        ROUND(COALESCE(SUM(r.distance_km), 0), 2) AS total_distance_km,
        COALESCE(SUM(r.duration_seconds), 0) AS total_duration_seconds
      FROM RECORD r
      WHERE
        r.activity_type = ?
        AND datetime(r.start_time) >= datetime(?)
        AND datetime(r.start_time) <= datetime(?)
        AND r.owner_id = ?
      GROUP BY week_start, week_end
      ORDER BY week_start ASC
    `;

    return db.prepare(sql).all(activityType, fromDate, toDate, userId);
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

  getActiveRecordDates(userId) {
    const sql = `
      SELECT DISTINCT date(start_time) AS activity_date
      FROM RECORD
      WHERE owner_id = ?
      ORDER BY activity_date DESC
    `;
    return db.prepare(sql).all(userId);
  },
};

export default recordRepo;
