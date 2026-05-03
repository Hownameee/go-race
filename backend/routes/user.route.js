import express from 'express';
import userController from '../controllers/user.controller.js';
import { uploadAvatar } from '../middlewares/upload.middleware.js';
import validation from '../middlewares/validation.js';
import {
  changePasswordSchema,
  confirmEmailChangeSchema,
  requestEmailOtpSchema,
  requestNewEmailOtpSchema,
  requestPasswordResetOtpSchema,
  resetPasswordWithOtpSchema,
  updateProfileSchema,
  verifyEmailOtpSchema,
  verifyCurrentPasswordSchema,
} from '../utils/schemas/user.schema.js';

const router = express.Router();

// Edit Profile
router.get('/me', userController.getMyInfo);
router.post('/me/avatar', uploadAvatar, userController.uploadMyAvatar);
router.patch(
  '/me',
  validation(updateProfileSchema),
  userController.updateMyInfo,
);
router.post(
  '/me/email/request-otp',
  validation(requestEmailOtpSchema),
  userController.requestEmailChangeOtp,
);
router.post(
  '/me/email/verify-otp',
  validation(verifyEmailOtpSchema),
  userController.verifyEmailChangeOtp,
);
router.post(
  '/me/email/request-new-otp',
  validation(requestNewEmailOtpSchema),
  userController.requestNewEmailChangeOtp,
);
router.patch(
  '/me/email',
  validation(confirmEmailChangeSchema),
  userController.confirmEmailChange,
);
router.post(
  '/me/password/verify-current',
  validation(verifyCurrentPasswordSchema),
  userController.verifyMyCurrentPassword,
);
router.patch(
  '/me/password',
  validation(changePasswordSchema),
  userController.changeMyPassword,
);
router.post(
  '/me/password/request-otp',
  validation(requestPasswordResetOtpSchema),
  userController.requestPasswordResetOtp,
);
router.patch(
  '/me/password/reset',
  validation(resetPasswordWithOtpSchema),
  userController.resetPasswordWithOtp,
);
router.delete('/me', userController.deleteMyAccount);

// Profile
router.get('/me/overview', userController.getMeOverview);
router.get('/:userId/overview', userController.getUserOverview);

// API: GET /api/users/suggest
router.get('/api/users/suggest', userController.getSuggestedUsers);

// API: GET /api/users/search?search=john
router.get('/api/users/search', userController.getUsersBySearch);
export default router;
