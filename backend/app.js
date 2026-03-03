import express from "express"
import { initDatabase } from "./utils/db/init.js";

const app = express()
initDatabase();

export default app;