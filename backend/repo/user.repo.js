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
