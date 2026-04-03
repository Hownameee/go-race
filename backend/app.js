import 'dotenv/config';

import express from 'express';
import { initDatabase } from './utils/db/init.js';
import restResponse from './middlewares/restResponse.js';
import notFound from './middlewares/notFound.js';
import errorHandler from './middlewares/errorHandler.js';
import bodyParser from 'body-parser';
import cors from 'cors';
import recordRouter from './routes/record.route.js';

import authRoute from './routes/auth.route.js';
import followRoutes from './routes/follow.route.js';
import postRoutes from './routes/post.route.js';

import notificationRouter from './routes/notification.route.js';
import deviceTokenRouter from './routes/device-token.route.js';
import { auth } from './middlewares/auth.middleware.js';

const app = express();
initDatabase();

app.use(bodyParser.json());
app.use(cors());
app.use(restResponse);
app.use('/public', express.static('public'));

app.use('/auth', authRoute);

const apiRoute = express.Router();
app.use(auth);
apiRoute.use('/records', recordRouter);
apiRoute.use('/notifications', notificationRouter);
apiRoute.use('/device-tokens', deviceTokenRouter);

app.use('/api', apiRoute);

app.use(followRoutes);
app.use(postRoutes);

app.use(notFound);
app.use(errorHandler);

export default app;
