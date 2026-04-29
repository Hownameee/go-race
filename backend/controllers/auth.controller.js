import userService from '../services/user.service.js';
import authService from '../services/auth.service.js';

function buildAuthPayload(user) {
  return {
    userId: user.user_id,
    role: user.role,
    username: user.username,
    fullname: user.fullname,
  };
}

function buildTokenResponse(tokens) {
  return {
    access_token: tokens.accessToken,
    refresh_token: tokens.refreshToken,
  };
}

const authController = {
  googleAuth: async function (req, res, next) {
    try {
      const { id_token, username, birthdate } = req.body;

      const googlePayload = await authService.verifyGoogleIdToken(id_token);

      if (
        !googlePayload.sub ||
        !googlePayload.email ||
        !googlePayload.email_verified
      ) {
        return res.unauthorized();
      }

      let user = await userService.getUserByGoogleSub(googlePayload.sub);

      if (user) {
        const tokens = authService.generateTokens(buildAuthPayload(user));
        return res.ok(buildTokenResponse(tokens), 'Google login successful');
      }

      const existingUser = await userService.getUserByEmail(
        googlePayload.email,
      );
      if (existingUser && !existingUser.google_sub) {
        return res.violate(
          null,
          'Email already exists with password login. Please login normally first.',
        );
      }

      if (!existingUser && (!username || !birthdate)) {
        return res.ok(
          {
            requires_profile_completion: true,
            profile: {
              email: googlePayload.email,
              fullname: googlePayload.name,
              avatar_url: googlePayload.picture,
            },
          },
          'Google verified. Additional profile information required.',
        );
      }

      if (!existingUser) {
        const hashedPassword =
          await authService.createOAuthPlaceholderPassword();
        const newUserId = await userService.createGoogleUser({
          username,
          fullname: googlePayload.name || username,
          email: googlePayload.email,
          hashedPassword,
          birthdate,
          googleSub: googlePayload.sub,
          avatarUrl: googlePayload.picture || null,
        });

        user = await userService.getUserById(newUserId);
      } else {
        user = existingUser;
      }

      const tokens = authService.generateTokens(buildAuthPayload(user));
      return res.ok(
        buildTokenResponse(tokens),
        'Google authentication successful',
      );
    } catch (error) {
      next(error);
    }
  },

  register: async function (req, res, next) {
    try {
      const userData = req.body;

      const existingUser = await userService.getUserByEmail(userData.email);
      if (existingUser) {
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

      const tokens = authService.generateTokens(buildAuthPayload(user));
      return res.ok(buildTokenResponse(tokens), 'Login successful');
    } catch (error) {
      next(error);
    }
  },

  refreshToken: async function (req, res) {
    try {
      const { refresh_token } = req.body;
      if (!refresh_token) {
        return res.badRequest(null, 'Refresh token is required');
      }

      const decoded = authService.verifyRefreshToken(refresh_token);
      const user = await userService.getUserById(decoded.userId);
      if (!user) return res.unauthorized();

      const tokens = authService.generateTokens(buildAuthPayload(user));
      return res.ok(buildTokenResponse(tokens), 'Token refreshed successfully');
    } catch (error) {
      return res.unauthorized(null, 'Invalid or expired refresh token');
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
