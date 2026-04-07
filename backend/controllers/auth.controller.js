import userService from '../services/user.service.js';
import authService from '../services/auth.service.js';

const authController = {
  register: async function (req, res, next) {
    try {
      const userData = req.body;

      const existingUser = await userService.getUserByEmail(userData.email);
      if (existingUser) {
        console.log('Email already exists');
        return res.violate(null, 'Email already exists');
      }

      const newUserId = await userService.createUser(userData);
      if (!newUserId) throw new Error('Created user account failed');

      return res.created(null, 'User registered successfully');
    } catch (error) {
      next(error);
    }
  },

  login: async function (req, res, next) {
    try {
      const { email, password } = req.body;

      const user = await userService.getUserByEmail(email);
      if (!user) return res.unauthorized();

      const isMatch = await authService.comparePassword(
        password,
        user.hashed_password,
      );
      if (!isMatch) return res.unauthorized();

      const token = authService.generateToken({
        userId: user.user_id,
        role: user.role,
        username: user.username,
        fullname: user.fullname,
      });

      return res.ok({ token: token }, 'Login successful');
    } catch (error) {
      next(error);
    }
  },
  requestPasswordResetOtp: async function (req, res, next) {
    try {
      const { email } = req.body;
      await authService.requestPasswordResetOtpByEmail(email);
      return res.ok(null, 'OTP sent to email successfully');
    } catch (error) {
      if (error.message === 'User not found') {
        return res.notFound();
      }
      next(error);
    }
  },
  verifyPasswordResetOtp: async function (req, res, next) {
    try {
      const { email, otp_code } = req.body;
      await authService.verifyPasswordResetOtp(email, otp_code);
      return res.ok(null, 'OTP verified successfully');
    } catch (error) {
      if (error.message === 'User not found') {
        return res.notFound();
      }
      if (error.message === 'Invalid or expired OTP') {
        return res.badRequest(null, error.message);
      }
      next(error);
    }
  },
  resetPasswordWithOtp: async function (req, res, next) {
    try {
      const { email, otp_code, new_password } = req.body;
      await authService.resetPasswordWithOtp(email, otp_code, new_password);
      return res.ok(null, 'Password reset successfully');
    } catch (error) {
      if (error.message === 'User not found') {
        return res.notFound();
      }
      if (error.message === 'Invalid or expired OTP') {
        return res.badRequest(null, error.message);
      }
      next(error);
    }
  },
};

export default authController;
