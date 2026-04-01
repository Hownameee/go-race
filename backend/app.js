import 'dotenv/config';

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

import notificationRouter from './routes/notification.route.js';
import deviceTokenRouter from './routes/device-token.route.js';
import { auth } from './middlewares/auth.middleware.js';
import userRoutes from './routes/user.route.js';

const app = express();
initDatabase();

app.use(bodyParser.json());
app.use(cors());
app.use(restResponse);


// routes here
app.use('/api/notifications', notificationRouter);
app.use('/api/device-tokens', deviceTokenRouter);
app.use("/auth", authRoute);
app.use(userRoutes)

// app.use(auth)
app.use(followRoutes);
app.use(postRoutes);

// app.use(notFound);
app.use(errorHandler);

export default app;
