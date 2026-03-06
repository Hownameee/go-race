import 'dotenv/config';

import express from 'express';
import { initDatabase } from './utils/db/init.js';
import restResponse from './middlewares/restResponse.js';
import notFound from './middlewares/notFound.js';
import errorHandler from './middlewares/errorHandler.js';
import bodyParser from 'body-parser';
import cors from 'cors';
import mailController from './controllers/mail.controller.js';
import notificationController from './controllers/notification.controller.js';

const app = express();
initDatabase();

app.use(bodyParser.json());
app.use(cors());
app.use(restResponse);

// routes here

app.use(notFound);
app.use(errorHandler);

app.post('/send-mail', mailController.sendEmail);
app.get('/notification/list', notificationController.getList);

export default app;
