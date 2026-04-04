import userRepo from '../repo/user.repo.js';
import authService from './auth.service.js';
import mailService from './mail.service.js';
import otpService from './otp.service.js';

const userService = {
  getAllUsers: async function (offset = 0, limit = 10) {
    return userRepo.getAllUsers(offset, limit);
  },

  getUserById: async function (userId) { 
    return await userRepo.getUserById(userId);
  },

  getUserByEmail: async function (email) {
    return await userRepo.getUserByEmail(email);
  },

  createUser: async function (userData) {
    const hashedPassword = await authService.hashPassword(userData.password); 

    return userRepo.createUser({
      username: userData.username,
      fullname: userData.fullname,
      email: userData.email,
      hashedPassword: hashedPassword,
      birthdate: userData.birthdate,
    });
  },
  updateUserById: async function (userId, updateData) {
    const dbUpdateData = {
      username: updateData.username,
      fullname: updateData.fullname,
      email: updateData.email,
      birthdate: updateData.birthdate,
      avatar_url: updateData.avatarUrl,
      nationality: updateData.nationality,
      address: updateData.address,
      height_cm: updateData.heightCm,
      weight_kg: updateData.weightKg,
    };

    if (updateData.password) {
      dbUpdateData.hashed_password = await authService.hashPassword(updateData.password);
    }

    const filteredUpdateData = Object.fromEntries(
      Object.entries(dbUpdateData).filter(([, value]) => value !== undefined)
    );

    return await userRepo.updateUserById(userId, filteredUpdateData);
  },

  requestEmailChangeOtp: async function (userId, newEmail) {
    const existingUser = await userRepo.getUserByEmail(newEmail);
    if (existingUser) {
      throw new Error('Email already exists');
    }

    const user = await userRepo.getUserById(userId);
    if (!user) {
      throw new Error('User not found');
    }

    const otpCode = otpService.createOtp(userId, 'change-email', newEmail);
    await mailService.sendEmail(
      user.email,
      'GoRace email change OTP',
      `<p>Your OTP to change email to <b>${newEmail}</b> is <b>${otpCode}</b>.</p><p>This code expires in 5 minutes.</p>`,
    );
  },

  confirmEmailChange: async function (userId, newEmail, otpCode) {
    const isValid = otpService.verifyOtp(userId, 'change-email', otpCode, newEmail);
    if (!isValid) {
      throw new Error('Invalid or expired OTP');
    }

    const existingUser = await userRepo.getUserByEmail(newEmail);
    if (existingUser) {
      throw new Error('Email already exists');
    }

    return await userRepo.updateUserById(userId, { email: newEmail });
  },

  changePasswordWithCurrentPassword: async function (userId, currentPassword, newPassword) {
    await userService.verifyCurrentPassword(userId, currentPassword);

    const hashedPassword = await authService.hashPassword(newPassword);
    return await userRepo.updateUserById(userId, { hashed_password: hashedPassword });
  },

  verifyCurrentPassword: async function (userId, currentPassword) {
    const user = await userRepo.getUserById(userId);
    if (!user) {
      throw new Error('User not found');
    }

    const isMatch = await authService.comparePassword(currentPassword, user.hashed_password);
    if (!isMatch) {
      throw new Error('Current password is incorrect');
    }

    return true;
  },

  requestPasswordResetOtp: async function (userId) {
    const user = await userRepo.getUserById(userId);
    if (!user) {
      throw new Error('User not found');
    }

    const otpCode = otpService.createOtp(userId, 'reset-password', user.email);
    await mailService.sendEmail(
      user.email,
      'GoRace password reset OTP',
      `<p>Your OTP to reset password is <b>${otpCode}</b>.</p><p>This code expires in 5 minutes.</p>`,
    );
  },

  resetPasswordWithOtp: async function (userId, otpCode, newPassword) {
    const user = await userRepo.getUserById(userId);
    if (!user) {
      throw new Error('User not found');
    }

    const isValid = otpService.verifyOtp(userId, 'reset-password', otpCode, user.email);
    if (!isValid) {
      throw new Error('Invalid or expired OTP');
    }

    const hashedPassword = await authService.hashPassword(newPassword);
    return await userRepo.updateUserById(userId, { hashed_password: hashedPassword });
  },

  deleteMyAccount: async function (userId) {
    return await userRepo.deleteUserById(userId);
  },
};

export default userService;
