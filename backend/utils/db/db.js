import Database from "better-sqlite3";

const db = new Database("./app.db", { verbose: console.log });

export default db;
