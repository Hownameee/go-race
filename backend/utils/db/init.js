import { readFileByPath } from '../fs/fs.js';
import db from './db.js';

export function initDatabase() {
  const schema = readFileByPath('./data/schema.sql');
  db.exec(schema);
}
