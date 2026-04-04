import { readFileByPath } from '../fs/fs.js';
import db from './db.js';

function ensureColumn(tableName, columnName, definition) {
  const columns = db.prepare(`PRAGMA table_info(${tableName})`).all();
  const hasColumn = columns.some((column) => column.name === columnName);

  if (!hasColumn) {
    db.exec(`ALTER TABLE ${tableName} ADD COLUMN ${definition}`);
  }
}

export function initDatabase() {
  const schema = readFileByPath('./data/schema.sql');
  db.exec(schema);

  ensureColumn('RECORD', 'owner_id', 'owner_id INTEGER REFERENCES USERS(user_id) ON DELETE CASCADE');
  ensureColumn('RECORD', 'user_id', 'user_id INTEGER REFERENCES USERS(user_id) ON DELETE CASCADE');
  ensureColumn('RECORD', 'elevation_gain_m', 'elevation_gain_m REAL DEFAULT 0');
  db.exec(`
    UPDATE RECORD
    SET owner_id = user_id
    WHERE owner_id IS NULL AND user_id IS NOT NULL
  `);
  db.exec(`
    UPDATE RECORD
    SET user_id = owner_id
    WHERE user_id IS NULL AND owner_id IS NOT NULL
  `);
  db.exec('CREATE INDEX IF NOT EXISTS idx_record_owner_start_time ON RECORD(owner_id, start_time)');
  db.exec('CREATE INDEX IF NOT EXISTS idx_record_user_start_time ON RECORD(user_id, start_time)');
}
