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
      LEFT JOIN POST p ON p.record_id = r.record_id
      WHERE
        r.activity_type = ?
        AND datetime(r.start_time) >= datetime(?)
        AND datetime(r.start_time) <= datetime(?)
        AND (
          r.user_id = ?
          OR (r.user_id IS NULL AND p.owner_id = ?)
        )
      GROUP BY week_start, week_end
      ORDER BY week_start ASC
    `;

    return db.prepare(sql).all(activityType, fromDate, toDate, userId, userId);
  },
};

export default recordRepo;
