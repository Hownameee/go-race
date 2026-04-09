import db from '../utils/db/db.js';

const SAFE_COLUMNS = `
  user_id, role, username, fullname, email, birthdate, 
  avatar_url, nationality, address, height_cm, weight_kg,
  created_at, updated_at
`;

const userRepo = {
  getAllUsers: (offset = 0, limit = 10) => {
    const sql = `SELECT ${SAFE_COLUMNS} FROM USERS LIMIT ? OFFSET ?`;
    return db.prepare(sql).all(limit, offset);
  },
  getUserById: (userId) => {
    const sql = `SELECT ${SAFE_COLUMNS} FROM USERS WHERE user_id = ?`;
    return db.prepare(sql).get(userId);
  },

  // Need to login so need password
  getUserByEmail: (email) => {
    const sql = `SELECT * FROM USERS WHERE email = ?`;
    return db.prepare(sql).get(email);
  },

  createUser: (user) => {
    const { username, fullname, email, hashedPassword, birthdate } = user;
    const sql = `
      INSERT INTO USERS (username, fullname, email, hashed_password, birthdate)
      VALUES (?, ?, ?, ?, ?);
    `;
    return db
      .prepare(sql)
      .run(username, fullname, email, hashedPassword, birthdate)
      .lastInsertRowid;
  },

  getSuggestUser: (currentUserId, limit) => {
    const sql = `
      WITH mutuals AS (
        SELECT 
          f2.following_id AS user_id,
          COUNT(*) AS mutual_count
        FROM FOLLOW f1
        JOIN FOLLOW f2 
          ON f1.following_id = f2.follower_id
        WHERE f1.follower_id = ?  -- currentUserId
        GROUP BY f2.following_id
      ),
      followers AS (
        SELECT 
          following_id AS user_id,
          COUNT(*) AS follower_count
        FROM FOLLOW
        GROUP BY following_id
      )
      SELECT 
        u.user_id,
        u.fullname,
        u.address,
        u.avatar_url,
        COALESCE(m.mutual_count, 0) AS mutual_count,
        COALESCE(f.follower_count, 0) AS follower_count
      FROM USERS u
      LEFT JOIN mutuals m ON u.user_id = m.user_id
      LEFT JOIN followers f ON u.user_id = f.user_id
      WHERE u.user_id != ?  -- Exclude current user
        AND u.user_id NOT IN (
          SELECT following_id 
          FROM FOLLOW 
          WHERE follower_id = ?  -- Exclude already followed
        )
      ORDER BY mutual_count DESC, follower_count DESC
      LIMIT ?
    `;

    return db.prepare(sql)
      .all(currentUserId, currentUserId, currentUserId, limit);
  },

  searchUsersByName: (currentUserId, search, limit) => {
    const keyword = search
      .trim()
      .split(/\s+/)
      .filter(Boolean)
      .map((w) => w + '*')
      .join(' ');

    const sql = `
      SELECT 
          u.user_id, 
          u.fullname, 
          u.address, 
          u.avatar_url,
          EXISTS (
              SELECT 1 FROM FOLLOW 
              WHERE follower_id = ? AND following_id = u.user_id
          ) AS is_following
      FROM USERS u
      JOIN USER_FTS fts ON u.user_id = fts.rowid
      WHERE USER_FTS MATCH ?
        AND u.user_id != ? -- Exclude current user only
      ORDER BY bm25(USER_FTS)
      LIMIT ?
    `;

    return db.prepare(sql)
      .all(currentUserId, keyword, currentUserId, limit);
  },

  updateUserById: (userId, updateData) => {
    const fields = [];
    const values = [];
    for (const key in updateData) {
      fields.push(`${key} = ?`);
      values.push(updateData[key]);
    }

    if (fields.length === 0) {
      return { changes: 0 };
    }

    values.push(userId);

    const sql = `UPDATE USERS SET ${fields.join(", ")}, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?`;
    return db.prepare(sql).run(...values);
  },

  deleteUserById: (userId) => {
    const sql = `DELETE FROM USERS WHERE user_id = ?`;
    return db.prepare(sql).run(userId);
  },
};

export default userRepo;
