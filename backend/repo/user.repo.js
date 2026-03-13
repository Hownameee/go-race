import db from "../utils/db/db.js";  

// ĐÃ THÊM 'role' VÀO ĐÂY
const SAFE_COLUMNS = `
  user_id, role, user_name, full_name, email, birthdate, 
  avatar_url, nationality, address, height_cm, weight_kg, shirt_size, 
  created_at, updated_at
`;

const userRepo = {
  getAllUsers: (offset = 0, limit = 10) => {
    const sql = `SELECT ${SAFE_COLUMNS} FROM USERS LIMIT ? OFFSET ?`;
    return db.prepare(sql).all(limit, offset);
  },
  
  getUserById: (user_id) => {
    const sql = `SELECT ${SAFE_COLUMNS} FROM USERS WHERE user_id = ?`;
    return db.prepare(sql).get(user_id);
  },
  
  getUserAuthById: (user_id) => {
    const sql = `SELECT * FROM USERS WHERE user_id = ?`;
    return db.prepare(sql).get(user_id);
  },
  
  getUserByEmail: (email) => {
    const sql = `SELECT * FROM USERS WHERE email = ?`;
    return db.prepare(sql).get(email);
  },
  
  createUser: (user) => {
    const { user_name, full_name, email, hashed_password, birthdate } = user;
    const sql = `
      INSERT INTO USERS (user_name, full_name, email, hashed_password, birthdate)
      VALUES (?, ?, ?, ?, ?);
    `;
    return db.prepare(sql).run(user_name, full_name, email, hashed_password, birthdate).lastInsertRowid;
  },
  
  updateUser: (user_id, updateData) => {
    const fields = [];
    const values = [];

    const allowedColumns = [
      'full_name', 'birthdate', 'avatar_url', 'nationality', 
      'address', 'height_cm', 'weight_kg', 'shirt_size'
    ];

    for (const [key, value] of Object.entries(updateData)) {
      if (value !== undefined && allowedColumns.includes(key)) {
        fields.push(`${key} = ?`);
        values.push(value);
      }
    }

    if (fields.length === 0) return 0;

    const sql = `
      UPDATE USERS
      SET ${fields.join(', ')}, updated_at = CURRENT_TIMESTAMP
      WHERE user_id = ?;
    `;
    values.push(user_id);

    return db.prepare(sql).run(...values).changes;
  },

  updatePassword: (user_id, new_hashed_password) => {
    const sql = `
      UPDATE USERS 
      SET hashed_password = ?, updated_at = CURRENT_TIMESTAMP 
      WHERE user_id = ?
    `;
    return db.prepare(sql).run(new_hashed_password, user_id).changes;
  }
};

export default userRepo;