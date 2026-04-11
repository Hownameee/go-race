import dotenv from 'dotenv';
dotenv.config();

const config = {
  NODE_ENV: process.env.NODE_ENV,
  SALT_ROUNDS: parseInt(process.env.SALT_ROUNDS) || 10,
  JWT_SECRET: process.env.JWT_SECRET || 'MY_SECRET_KEY',
  JWT_EXPIRED_TIME: process.env.JWT_EXPIRED_TIME || '1h',
  FIREBASE_SERVICE_ACCOUNT_PATH:
    process.env.FIREBASE_SERVICE_ACCOUNT_PATH ||
    './config/serviceAccountKey.json',
  GOOGLE_WEB_CLIENT_ID: process.env.GOOGLE_WEB_CLIENT_ID,
};

export default config;
