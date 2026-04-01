import db from '../utils/db/db.js';

const SAFE_COLUMNS = `
  user_id, role, username, fullname, email, birthdate, 
  avatar_url, nationality, address, height_cm, weight_kg, shirt_size, 
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

  searchUsers: (search, limit) => {
    // Case 1: empty search → get all users
    if (!search || search.trim() === '') {
      const sql = `
        SELECT user_id, username, avatar_url
        FROM USERS
        ORDER BY created_at DESC
        LIMIT ?
      `;
      return db.prepare(sql).all(limit); 
    }

    // Case 2: FTS search
    const keyword = search
      .trim()
      .split(/\s+/)
      .filter(Boolean)
      .map(w => w + '*')
      .join(' ');

    const sql = `
      SELECT u.user_id, u.username, u.avatar_url
      FROM USERS u
      JOIN USER_FTS fts ON u.user_id = fts.rowid
      WHERE USER_FTS MATCH ?
      ORDER BY bm25(USER_FTS)
      LIMIT ?
    `;

    return db.prepare(sql).all(keyword, limit); 
  }
};

export default userRepo;
