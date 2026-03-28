import express from 'express';
import { initDatabase } from './utils/db/init.js';
import restResponse from './middlewares/restResponse.js';
import notFound from './middlewares/notFound.js';
import errorHandler from './middlewares/errorHandler.js';
import bodyParser from 'body-parser';
import cors from 'cors';

import authRoute from './routes/auth.route.js';
import followRoutes from './routes/follow.route.js';
import postRoutes from './routes/post.route.js';

const app = express();
initDatabase();

app.use(bodyParser.json());
app.use(cors());
app.use(restResponse);

// routes here
app.use("/auth", authRoute);
app.use(followRoutes);
app.use(postRoutes);

app.use(notFound);
app.use(errorHandler);

export default app;
