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
      SELECT 
        u.user_id, 
        u.fullname, 
        u.address,
        u.avatar_url,
        (
          -- 1. Mutual Connections: Count how many people the current user follows 
          -- who ALSO follow this suggested user.
          SELECT COUNT(*)
          FROM FOLLOW f1
          JOIN FOLLOW f2 ON f1.following_id = f2.follower_id
          WHERE f1.follower_id = ? AND f2.following_id = u.user_id
        ) AS mutual_count,
        (
          -- 2. Popularity Fallback: Total number of followers this user has.
          SELECT COUNT(*)
          FROM FOLLOW
          WHERE following_id = u.user_id
        ) AS follower_count
      FROM USERS u
      WHERE u.user_id != ? -- Exclude the current user
        AND u.user_id NOT IN (
          -- Exclude users the current user is already following
          SELECT following_id 
          FROM FOLLOW 
          WHERE follower_id = ?
        )
      -- Rank by mutuals first, then popularity, then randomize ties to keep it fresh
      ORDER BY mutual_count DESC, follower_count DESC, RANDOM()
      LIMIT ?
    `;

    return db
      .prepare(sql)
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
      AND u.user_id != ? -- Loại bỏ chính mình khỏi kết quả tìm kiếm
      ORDER BY bm25(USER_FTS)
      LIMIT ?
    `;

    return db.prepare(sql).all(currentUserId, keyword, currentUserId, limit);
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
