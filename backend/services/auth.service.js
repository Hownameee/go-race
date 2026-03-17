import bcrypt from 'bcrypt';
import config from '../config/config.js';
import jwt from 'jsonwebtoken';

const authService = {
  hashPassword: async (plainPassword) => {
    return await bcrypt.hash(plainPassword, config.SALT_ROUNDS);
  },
  comparePassword: async (plainPassword, hashedPassword) => {
    return await bcrypt.compare(plainPassword, hashedPassword);
  },
  generateToken: (payload) => {
    return jwt.sign(payload, config.JWT_SECRET, {
      expiresIn: config.JWT_EXPIRED_TIME,
    });
  },
  verifyToken: (token) => {
    return jwt.verify(token, config.JWT_SECRET);
  },
};

export default authService;
