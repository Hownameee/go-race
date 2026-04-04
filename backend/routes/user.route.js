import express from 'express';
import userController from '../controllers/user.controller.js';
import { auth } from '../middlewares/auth.middleware.js';
import { uploadAvatar } from '../middlewares/upload.middleware.js';
import validation from '../middlewares/validation.js';
import {
  changePasswordSchema,
  confirmEmailChangeSchema,
  requestEmailOtpSchema,
  requestPasswordResetOtpSchema,
  resetPasswordWithOtpSchema,
  updateProfileSchema,
  verifyCurrentPasswordSchema,
} from '../utils/schemas/user.schema.js';

const router = express.Router();

// Edit Profile
router.get('/me', auth, userController.getMyInfo);
router.post('/me/avatar', auth, uploadAvatar, userController.uploadMyAvatar);
router.patch('/me', auth, validation(updateProfileSchema), userController.updateMyInfo);
router.post('/me/email/request-otp', auth, validation(requestEmailOtpSchema), userController.requestEmailChangeOtp);
router.patch('/me/email', auth, validation(confirmEmailChangeSchema), userController.confirmEmailChange);
router.post('/me/password/verify-current', auth, validation(verifyCurrentPasswordSchema), userController.verifyMyCurrentPassword);
router.patch('/me/password', auth, validation(changePasswordSchema), userController.changeMyPassword);
router.post('/me/password/request-otp', auth, validation(requestPasswordResetOtpSchema), userController.requestPasswordResetOtp);
router.patch('/me/password/reset', auth, validation(resetPasswordWithOtpSchema), userController.resetPasswordWithOtp);
router.delete('/me', auth, userController.deleteMyAccount);

// Profile
router.get('/me/overview', auth, userController.getMeOverview);

export default router;
