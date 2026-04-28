import 'dotenv/config';

import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';
import { initDatabase } from './utils/db/init.js';
import restResponse from './middlewares/restResponse.js';
import notFound from './middlewares/notFound.js';
import errorHandler from './middlewares/errorHandler.js';
import bodyParser from 'body-parser';
import cors from 'cors';

import { auth } from './middlewares/auth.middleware.js';
import authRoute from './routes/auth.route.js';
import followRoutes from './routes/follow.route.js';
import postRoutes from './routes/post.route.js';
import userRoute from './routes/user.route.js';
import recordRoute from './routes/record.route.js';
import notificationRouter from './routes/notification.route.js';
import deviceTokenRouter from './routes/device-token.route.js';
import clubRouter from './routes/club.route.js';

const app = express();
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
initDatabase();

app.use(cors());
app.use(restResponse);
app.use(bodyParser.json({ limit: '50mb' }));
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true }));
app.use('/uploads', express.static(path.resolve(__dirname, 'uploads')));

app.use('/api/auth', authRoute);

app.use(auth);

app.use('/api/users', userRoute);
app.use('/api/clubs', clubRouter);
app.use('/api/records', recordRoute);

app.use('/api/notifications', notificationRouter);
app.use('/api/device-tokens', deviceTokenRouter);

app.use(followRoutes);
app.use(postRoutes);
app.use(userRoute);
app.use(clubRoute);

app.use(notFound);
app.use(errorHandler);

export default app;
