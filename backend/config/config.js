import dotenv from 'dotenv';
dotenv.config();

const config = {
  NODE_ENV: process.env.NODE_ENV,
  SALT_ROUNDS: parseInt(process.env.SALT_ROUNDS) || 10,
  JWT_SECRET: process.env.JWT_SECRET,
  JWT_EXPIRED_TIME: process.env.JWT_EXPIRED_TIME || '1h',
};

export default config;
