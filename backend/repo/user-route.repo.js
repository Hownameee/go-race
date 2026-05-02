import db from '../utils/db/db.js';

const userRouteRepo = {
  create: async function (userId, data) {
    const sql = `
      INSERT INTO USER_ROUTES (user_id, name, route_mode, is_cycle, distance_km, duration_seconds, route_coordinates_json, waypoints_json)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;
    const params = [
      userId,
      data.name,
      data.routeMode,
      data.isCycle ? 1 : 0,
      data.distanceKm,
      data.durationSeconds,
      data.routeCoordinatesJson,
      data.waypointsJson
    ];

    const info = await db.prepare(sql).run(...params);
    return info.lastInsertRowid;
  },

  getById: async function (routeId) {
    const sql = `SELECT * FROM USER_ROUTES WHERE route_id = ?`;
    return await db.prepare(sql).get(routeId);
  },

  getByUserId: async function (userId) {
    const sql = `SELECT * FROM USER_ROUTES WHERE user_id = ? ORDER BY created_at DESC`;
    return await db.prepare(sql).all(userId);
  },

  delete: async function (userId, routeId) {
    const sql = `DELETE FROM USER_ROUTES WHERE user_id = ? AND route_id = ?`;
    return await db.prepare(sql).run(userId, routeId);
  },

  update: async function (userId, routeId, data) {
    const sql = `
      UPDATE USER_ROUTES
      SET name = ?, route_mode = ?, is_cycle = ?, distance_km = ?, duration_seconds = ?, route_coordinates_json = ?, waypoints_json = ?
      WHERE user_id = ? AND route_id = ?
    `;
    const params = [
      data.name,
      data.routeMode,
      data.isCycle ? 1 : 0,
      data.distanceKm,
      data.durationSeconds,
      data.routeCoordinatesJson,
      data.waypointsJson,
      userId,
      routeId
    ];
    return await db.prepare(sql).run(...params);
  }
};

export default userRouteRepo;
