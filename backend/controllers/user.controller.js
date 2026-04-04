import userService from '../services/user.service.js';
import followService from '../services/follow.service.js';

const userController = {
  // getAllUsers: async function (req, res, next) {
  //   try {
  //     const offset = parseInt(req.query.offset) || 0;
  //     const limit = parseInt(req.query.limit) || 10;
  //     const users = await userService.getAllUsers(offset, limit);
  //     res.ok(users, "Users fetched successfully");
  //   } catch (error) {
  //     next(error);
  //   }
  // },
  getUserById: async function (req, res, next) {
    try {
      const user_id = req.params.userId;
      const user = await userService.getUserById(user_id);
      res.ok(user, "User fetched successfully");
    } catch (error) {
      if (error.message === "User not found") {
        return res.notFound();
      }
      next(error);
    }
  },
  // updateUser: async function (req, res, next) {
  //   try {
  //     const user_id = req.params.id;
  //     const updateData = req.body;
  //     await userService.updateUser(user_id, updateData);
  //     res.ok(null, "User updated successfully");
  //   } catch (error) {
  //     next(error);
  //   }
  // }

  // ===== PROFILE PAGES =====
  // Profile main page
  getMeOverview: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const user = await userService.getUserById(userId);
      if (!user) return res.notFound();

      const city = user.address ? user.address.split(",").slice(-2, -1)[0]?.trim() || null : null;
      const country = user.address ? user.address.split(",").slice(-1)[0]?.trim() || null : null;


      const followersCount = await followService.countFollowers(userId);
      const followingCount = await followService.countFollowings(userId);

      const returnUser = {
        user_id: user.user_id,
        fullname: user.fullname,
        avatar_url: user.avatar_url,
        city,
        country,
        total_followers: followersCount,
        total_followings: followingCount,
      };

      res.ok(
        returnUser,
        "User overview fetched successfully",
      );
    } catch (error) {
      if (error.message === "User not found") {
        return res.notFound();
      }
      next(error);
    }
  },

  // Profile edit page
  getMyInfo: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const user = await userService.getUserById(userId);
      res.ok(user, "User fetched successfully");
    } catch (error) {
      if (error.message === "User not found") {
        return res.notFound();
      }
      next(error);
    }
  },
  updateMyInfo: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const updateData = req.body;
      await userService.updateUserById(userId, updateData);
      res.ok(null, "User updated successfully");
    } catch (error) {
      next(error);
    }
  },
  requestEmailChangeOtp: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const { new_email } = req.body;
      await userService.requestEmailChangeOtp(userId, new_email);
      return res.ok(null, 'OTP sent to new email successfully');
    } catch (error) {
      if (error.message === 'Email already exists') {
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },
  confirmEmailChange: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const { new_email, otp_code } = req.body;
      await userService.confirmEmailChange(userId, new_email, otp_code);
      return res.ok(null, 'Email changed successfully');
    } catch (error) {
      if (error.message === 'Email already exists') {
        return res.violate(null, error.message);
      }
      if (error.message === 'Invalid or expired OTP') {
        return res.badRequest(null, error.message);
      }
      return next(error);
    }
  },
  changeMyPassword: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const { old_password, new_password } = req.body;
      await userService.changePasswordWithCurrentPassword(userId, old_password, new_password);
      return res.ok(null, 'Password changed successfully');
    } catch (error) {
      if (error.message === 'Current password is incorrect') {
        return res.badRequest(null, error.message);
      }
      return next(error);
    }
  },
  verifyMyCurrentPassword: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const { old_password } = req.body;
      await userService.verifyCurrentPassword(userId, old_password);
      return res.ok(null, 'Current password verified');
    } catch (error) {
      if (error.message === 'Current password is incorrect') {
        return res.badRequest(null, error.message);
      }
      return next(error);
    }
  },
  requestPasswordResetOtp: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      await userService.requestPasswordResetOtp(userId);
      return res.ok(null, 'OTP sent to your email successfully');
    } catch (error) {
      return next(error);
    }
  },
  resetPasswordWithOtp: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const { otp_code, new_password } = req.body;
      await userService.resetPasswordWithOtp(userId, otp_code, new_password);
      return res.ok(null, 'Password reset successfully');
    } catch (error) {
      if (error.message === 'Invalid or expired OTP') {
        return res.badRequest(null, error.message);
      }
      return next(error);
    }
  },
  deleteMyAccount: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      await userService.deleteMyAccount(userId);
      return res.ok(null, 'Account deleted successfully');
    } catch (error) {
      return next(error);
    }
  },
  uploadMyAvatar: async function (req, res, next) {
    try {
      const userId = req.user.userId;
      const avatarUrl = `${req.protocol}://${req.get('host')}/uploads/avatars/${req.file.filename}`;

      await userService.updateUserById(userId, { avatarUrl });

      res.ok(
        { avatar_url: avatarUrl },
        "Avatar uploaded successfully",
      );
    } catch (error) {
      next(error);
    }
  },
}


export default userController;
