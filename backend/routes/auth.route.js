import express from 'express';
import authController from '../controllers/auth.controller.js';
import validation from '../middlewares/validation.js';
import {
  googleAuthSchema,
  loginSchema,
  refreshTokenSchema,
  registerSchema,
  requestPasswordResetOtpByEmailSchema,
  resetPasswordWithOtpSchema,
  verifyPasswordResetOtpSchema,
} from '../utils/schemas/user.schema.js';

const router = express.Router();

router.post('/google', validation(googleAuthSchema), authController.googleAuth);
router.post('/register', validation(registerSchema), authController.register);
router.post('/login', validation(loginSchema), authController.login);
router.post(
  '/refresh-token',
  validation(refreshTokenSchema),
  authController.refreshToken,
);
router.post(
  '/password/request-otp',
  validation(requestPasswordResetOtpByEmailSchema),
  authController.requestPasswordResetOtp,
);
router.post(
  '/password/verify-otp',
  validation(verifyPasswordResetOtpSchema),
  authController.verifyPasswordResetOtp,
);
router.patch(
  '/password/reset',
  validation(resetPasswordWithOtpSchema),
  authController.resetPasswordWithOtp,
);

export default router;
