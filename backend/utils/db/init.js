import { readFileByPath } from '../fs/fs.js';
import db from './db.js';

function ensureColumn(tableName, columnName, definition) {
  const columns = db.prepare(`PRAGMA table_info(${tableName})`).all();
  const hasColumn = columns.some((column) => column.name === columnName);

  if (!hasColumn) {
    db.exec(`ALTER TABLE ${tableName} ADD COLUMN ${definition}`);
  }
}

function migrateLegacyUserLocation() {
  const columns = db.prepare('PRAGMA table_info(USERS)').all();
  const hasAddressColumn = columns.some((column) => column.name === 'address');

  if (!hasAddressColumn) {
    return;
  }

  const users = db.prepare(`
    SELECT user_id, address, province_city, country
    FROM USERS
    WHERE address IS NOT NULL
      AND trim(address) != ''
      AND (province_city IS NULL OR trim(province_city) = '' OR country IS NULL OR trim(country) = '')
  `).all();

  const updateStatement = db.prepare(`
    UPDATE USERS
    SET province_city = COALESCE(?, province_city),
        country = COALESCE(?, country)
    WHERE user_id = ?
  `);

  for (const user of users) {
    const parts = user.address
      .split(',')
      .map((part) => part.trim())
      .filter((part) => part.length > 0);

    if (parts.length === 0) {
      continue;
    }

    const nextCountry = !user.country && parts.length >= 1 ? parts[parts.length - 1] : null;
    const nextProvinceCity = !user.province_city && parts.length >= 2 ? parts[parts.length - 2] : null;

    if (nextProvinceCity || nextCountry) {
      updateStatement.run(nextProvinceCity, nextCountry, user.user_id);
    }
  }
}

export function initDatabase() {
  const schema = readFileByPath('./data/schema.sql');
  db.exec(schema);
  
  ensureColumn('USERS', 'auth_provider', "auth_provider TEXT DEFAULT 'local'");
  ensureColumn('USERS', 'google_sub', 'google_sub TEXT');
  ensureColumn('USERS', 'bio', 'bio TEXT');
  ensureColumn('USERS', 'province_city', 'province_city TEXT');
  ensureColumn('USERS', 'country', 'country TEXT');
  db.exec('CREATE UNIQUE INDEX IF NOT EXISTS idx_users_google_sub ON USERS(google_sub) WHERE google_sub IS NOT NULL');
  migrateLegacyUserLocation();

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
