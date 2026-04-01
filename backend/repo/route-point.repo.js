import db from '../utils/db/db.js';

const routePointRepo = {
  createMany: async function (recordId, points) {
    const sql = `INSERT INTO ROUTE_POINTS (record_id, latitude, longitude, altitude, timestamp, accuracy) 
                 VALUES (?, ?, ?, ?, ?, ?)`;
    const stmt = db.prepare(sql);

    // Using a simple loop as the db utility seems to be better-sqlite3 or similar
    // normally we would use a transaction for this.
    const insertTransaction = db.transaction((points) => {
      for (const point of points) {
        stmt.run(
          recordId,
          point.latitude,
          point.longitude,
          point.altitude ?? null,
          point.timestamp,
          point.accuracy ?? null,
        );
      }
    });

    insertTransaction(points);
  },
};

export default routePointRepo;
