import db from '../utils/db/db.js';

const SAFE_COLUMNS = `
  user_id, role, username, fullname, email, birthdate, 
  avatar_url, bio, province_city, country, height_cm, weight_kg,
  created_at, updated_at
`;

const userRepo = {
  getUserByGoogleSub: (googleSub) => {
    const sql = `SELECT * FROM USERS WHERE google_sub = ?`;
    return db.prepare(sql).get(googleSub);
  },

  createGoogleUser: (user) => {
    const {
      username,
      fullname,
      email,
      hashedPassword,
      birthdate,
      googleSub,
      avatarUrl,
    } = user;

    const sql = `
      INSERT INTO USERS (
        username, fullname, email, hashed_password, birthdate,
        auth_provider, google_sub, avatar_url
      )
      VALUES (?, ?, ?, ?, ?, 'google', ?, ?);
    `;

    return db
      .prepare(sql)
      .run(
        username,
        fullname,
        email,
        hashedPassword,
        birthdate,
        googleSub,
        avatarUrl,
      ).lastInsertRowid;
  },

  getAllUsers: (offset = 0, limit = 10) => {
    const sql = `SELECT ${SAFE_COLUMNS} FROM USERS LIMIT ? OFFSET ?`;
    return db.prepare(sql).all(limit, offset);
  },
  getUserById: (userId) => {
    const sql = `SELECT ${SAFE_COLUMNS} FROM USERS WHERE user_id = ?`;
    return db.prepare(sql).get(userId);
  },

  getUserAuthById: (userId) => {
    const sql = `SELECT user_id, email, hashed_password FROM USERS WHERE user_id = ?`;
    return db.prepare(sql).get(userId);
  },

  // Need to login so need password
  getUserByEmail: (email) => {
    const sql = `SELECT * FROM USERS WHERE email = ?`;
    return db.prepare(sql).get(email);
  },

  getUserByUsername: (username) => {
    const sql = `SELECT * FROM USERS WHERE username = ? COLLATE NOCASE`;
    return db.prepare(sql).get(username);
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
        CASE
          WHEN u.province_city IS NOT NULL AND u.country IS NOT NULL THEN u.province_city || ', ' || u.country
          WHEN u.province_city IS NOT NULL THEN u.province_city
          WHEN u.country IS NOT NULL THEN u.country
          ELSE NULL
        END AS address,
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

    return db
      .prepare(sql)
      .all(currentUserId, currentUserId, currentUserId, limit);
  },

  searchUsersByName: (currentUserId, search, limit) => {
    const keyword = `%${search.trim()}%`;
    const prefixKeyword = `${search.trim()}%`;

    const sql = `
      SELECT 
          u.user_id, 
          u.fullname, 
          CASE
              WHEN u.province_city IS NOT NULL AND u.country IS NOT NULL THEN u.province_city || ', ' || u.country
              WHEN u.province_city IS NOT NULL THEN u.province_city
              WHEN u.country IS NOT NULL THEN u.country
              ELSE NULL
          END AS address,
          u.avatar_url,
          EXISTS (
              SELECT 1 FROM FOLLOW 
              WHERE follower_id = ? AND following_id = u.user_id
          ) AS is_following
      FROM USERS u
      WHERE u.fullname LIKE ? COLLATE NOCASE
        AND u.user_id != ? -- Exclude current user only
      ORDER BY
        CASE WHEN u.fullname LIKE ? COLLATE NOCASE THEN 0 ELSE 1 END,
        u.fullname COLLATE NOCASE ASC
      LIMIT ?
    `;

    return db
      .prepare(sql)
      .all(currentUserId, keyword, currentUserId, prefixKeyword, limit);
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

    const sql = `UPDATE USERS SET ${fields.join(', ')}, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?`;
    return db.prepare(sql).run(...values);
  },

  deleteUserById: (userId) => {
    const sql = `DELETE FROM USERS WHERE user_id = ?`;
    return db.prepare(sql).run(userId);
  },
};

export default userRepo;
