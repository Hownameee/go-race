import dotenv from 'dotenv';
dotenv.config();

const config = {
  NODE_ENV: process.env.NODE_ENV,
  SALT_ROUNDS: parseInt(process.env.SALT_ROUNDS) || 10,

  JWT_ACCESS_SECRET: process.env.JWT_ACCESS_SECRET || 'MY_SECRET_KEY',
  JWT_ACCESS_EXPIRED_TIME: process.env.JWT_ACCESS_EXPIRED_TIME || '1h',

  JWT_REFRESH_SECRET: process.env.JWT_REFRESH_SECRET || 'MY_REFRESH_SECRET_KEY',
  JWT_REFRESH_EXPIRED_TIME: process.env.JWT_REFRESH_EXPIRED_TIME || '7d',

  FIREBASE_SERVICE_ACCOUNT_PATH:
    process.env.FIREBASE_SERVICE_ACCOUNT_PATH ||
    './config/serviceAccountKey.json',
  GOOGLE_WEB_CLIENT_ID: process.env.GOOGLE_WEB_CLIENT_ID,
};

export default config;
