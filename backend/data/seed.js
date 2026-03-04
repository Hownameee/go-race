import db from "../utils/db/db.js";
import { readFileByPath } from "../utils/fs/fs.js";

const schema = readFileByPath("./data/schema.sql");
db.exec(schema);
const sampleData = readFileByPath("./data/data.sql");
db.exec(sampleData);
