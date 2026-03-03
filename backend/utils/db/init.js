import { readFileByPath } from "../fs/fs.js";
import db from "./db.js";

export function initDatabase() {
  const schema = readFileByPath("./data/schema.sql");
  db.exec(schema);
  const sampleData = readFileByPath("./data/data.sql");
  db.exec(sampleData);
}
