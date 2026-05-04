import bcrypt from 'bcrypt';
import config from '../config/config.js';
import jwt from 'jsonwebtoken';
import { randomUUID } from 'node:crypto';
import userRepo from '../repo/user.repo.js';
import mailService from './mail.service.js';
import otpService from './otp.service.js';
import { OAuth2Client } from 'google-auth-library';

const googleClient = new OAuth2Client(config.GOOGLE_WEB_CLIENT_ID);

const authService = {
  hashPassword: async (plainPassword) => {
    return await bcrypt.hash(plainPassword, config.SALT_ROUNDS);
  },
  comparePassword: async (plainPassword, hashedPassword) => {
    return await bcrypt.compare(plainPassword, hashedPassword);
  },
  generateTokens: (payload) => {
    const accessToken = jwt.sign(payload, config.JWT_ACCESS_SECRET, {
      expiresIn: config.JWT_ACCESS_EXPIRED_TIME,
    });
    const refreshToken = jwt.sign(payload, config.JWT_REFRESH_SECRET, {
      expiresIn: config.JWT_REFRESH_EXPIRED_TIME,
    });
    return { accessToken, refreshToken };
  },
  verifyToken: (token) => {
    return jwt.verify(token, config.JWT_ACCESS_SECRET);
  },
  verifyRefreshToken: (token) => {
    return jwt.verify(token, config.JWT_REFRESH_SECRET);
  },
  verifyGoogleIdToken: async (idToken) => {
    if (!config.GOOGLE_WEB_CLIENT_ID) {
      throw new Error('GOOGLE_WEB_CLIENT_ID is missing');
    }

    const ticket = await googleClient.verifyIdToken({
      idToken,
      audience: config.GOOGLE_WEB_CLIENT_ID,
    });

    return ticket.getPayload();
  },
  createOAuthPlaceholderPassword: async () => {
    return authService.hashPassword(`google:${randomUUID()}`);
  },
  requestPasswordResetOtpByEmail: async (email) => {
    const user = await userRepo.getUserByEmail(email);
    if (!user) {
      throw new Error('User not found');
    }

    const otpCode = otpService.createOtp(user.user_id, 'reset-password', email);
    await mailService.sendEmail(
      email,
      'GoRace password reset OTP',
      `<p>Your OTP to reset password is <b>${otpCode}</b>.</p><p>This code expires in 5 minutes.</p>`,
    );
  },
  verifyPasswordResetOtp: async (email, otpCode) => {
    const user = await userRepo.getUserByEmail(email);
    if (!user) {
      throw new Error('User not found');
    }

    const isValid = otpService.verifyOtp(
      user.user_id,
      'reset-password',
      otpCode,
      email,
      false,
    );
    if (!isValid) {
      throw new Error('Invalid or expired OTP');
    }

    return true;
  },
  resetPasswordWithOtp: async (email, otpCode, newPassword) => {
    const user = await userRepo.getUserByEmail(email);
    if (!user) {
      throw new Error('User not found');
    }

    const isValid = otpService.verifyOtp(
      user.user_id,
      'reset-password',
      otpCode,
      email,
    );
    if (!isValid) {
      throw new Error('Invalid or expired OTP');
    }

    const hashedPassword = await authService.hashPassword(newPassword);
    return await userRepo.updateUserById(user.user_id, {
      hashed_password: hashedPassword,
    });
  },
};

export default authService;
