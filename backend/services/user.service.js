import userRepo from '../repo/user.repo.js';
import authService from './auth.service.js';
import mailService from './mail.service.js';
import otpService from './otp.service.js';
import { resolveImageUrl } from '../utils/s3/s3.js';

const EMAIL_CHANGE_AUTH_TTL_MS = 10 * 60 * 1000;
const emailChangeAuthorizationStore = new Map();

async function attachAvatarUrl(entity) {
  if (!entity) {
    return entity;
  }

  return {
    ...entity,
    avatar_url: await resolveImageUrl(entity.avatar_url),
  };
}

async function attachAvatarUrls(items) {
  return Promise.all((items || []).map(attachAvatarUrl));
}

const userService = {
  getUserByGoogleSub: async function (googleSub) {
    return await userRepo.getUserByGoogleSub(googleSub);
  },

  createGoogleUser: async function (userData) {
    return userRepo.createGoogleUser(userData);
  },

  getAllUsers: async function (offset = 0, limit = 10) {
    const users = await userRepo.getAllUsers(offset, limit);
    return attachAvatarUrls(users);
  },

  getUserById: async function (userId) {
    const user = await userRepo.getUserById(userId);
    return attachAvatarUrl(user);
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

  getSuggestedUsers: async function (currentUserId, limit) {
    const users = await userRepo.getSuggestUser(currentUserId, limit);
    return attachAvatarUrls(users);
  },

  searchUsersByName: async function (currentUserId, searchQuery, limit) {
    const safeQuery = searchQuery || '';
    const users = await userRepo.searchUsersByName(
      currentUserId,
      safeQuery,
      limit,
    );
    return attachAvatarUrls(users);
  },
  updateUserById: async function (userId, updateData) {
    const dbUpdateData = {
      username: updateData.username,
      fullname: updateData.fullname,
      email: updateData.email,
      birthdate: updateData.birthdate,
      avatar_url: updateData.avatarUrl,
      bio: updateData.bio,
      province_city: updateData.provinceCity,
      country: updateData.country,
      height_cm: updateData.heightCm,
      weight_kg: updateData.weightKg,
    };

    if (updateData.password) {
      dbUpdateData.hashed_password = await authService.hashPassword(
        updateData.password,
      );
    }

    const filteredUpdateData = Object.fromEntries(
      Object.entries(dbUpdateData).filter(([, value]) => value !== undefined),
    );

    return await userRepo.updateUserById(userId, filteredUpdateData);
  },

  requestEmailChangeOtp: async function (userId) {
    const user = await userRepo.getUserById(userId);
    if (!user) {
      throw new Error('User not found');
    }

    const otpCode = otpService.createOtp(
      userId,
      'change-email-verify-current',
      user.email,
    );
    await mailService.sendEmail(
      user.email,
      'GoRace email change OTP',
      `<p>Your OTP to verify your current email is <b>${otpCode}</b>.</p><p>This code expires in 5 minutes.</p>`,
    );
  },

  verifyEmailChangeOtp: async function (userId, otpCode) {
    const user = await userRepo.getUserById(userId);
    if (!user) {
      throw new Error('User not found');
    }

    const isValid = otpService.verifyOtp(
      userId,
      'change-email-verify-current',
      otpCode,
      user.email,
    );
    if (!isValid) {
      throw new Error('Invalid or expired OTP');
    }

    emailChangeAuthorizationStore.set(userId, {
      expiresAt: Date.now() + EMAIL_CHANGE_AUTH_TTL_MS,
      currentEmailVerified: true,
    });

    return true;
  },

  requestNewEmailChangeOtp: async function (userId, newEmail) {
    const authorization = emailChangeAuthorizationStore.get(userId);
    if (
      !authorization ||
      authorization.expiresAt < Date.now() ||
      !authorization.currentEmailVerified
    ) {
      emailChangeAuthorizationStore.delete(userId);
      throw new Error('Email change verification required');
    }

    const existingUser = await userRepo.getUserByEmail(newEmail);
    if (existingUser) {
      throw new Error('Email already exists');
    }

    const otpCode = otpService.createOtp(
      userId,
      'change-email-verify-new',
      newEmail,
    );
    await mailService.sendEmail(
      newEmail,
      'GoRace new email verification OTP',
      `<p>Your OTP to verify your new email is <b>${otpCode}</b>.</p><p>This code expires in 5 minutes.</p>`,
    );

    emailChangeAuthorizationStore.set(userId, {
      ...authorization,
      expiresAt: Date.now() + EMAIL_CHANGE_AUTH_TTL_MS,
      pendingNewEmail: newEmail,
    });

    return true;
  },

  confirmEmailChange: async function (userId, newEmail, otpCode) {
    const authorization = emailChangeAuthorizationStore.get(userId);
    if (
      !authorization ||
      authorization.expiresAt < Date.now() ||
      !authorization.currentEmailVerified
    ) {
      emailChangeAuthorizationStore.delete(userId);
      throw new Error('Email change verification required');
    }

    if (
      !authorization.pendingNewEmail ||
      authorization.pendingNewEmail !== newEmail
    ) {
      throw new Error('New email verification required');
    }

    const existingUser = await userRepo.getUserByEmail(newEmail);
    if (existingUser) {
      throw new Error('Email already exists');
    }

    const isValid = otpService.verifyOtp(
      userId,
      'change-email-verify-new',
      otpCode,
      newEmail,
    );
    if (!isValid) {
      throw new Error('Invalid or expired OTP');
    }

    emailChangeAuthorizationStore.delete(userId);
    return await userRepo.updateUserById(userId, { email: newEmail });
  },

  changePasswordWithCurrentPassword: async function (
    userId,
    currentPassword,
    newPassword,
  ) {
    await userService.verifyCurrentPassword(userId, currentPassword);

    const hashedPassword = await authService.hashPassword(newPassword);
    return await userRepo.updateUserById(userId, {
      hashed_password: hashedPassword,
    });
  },

  verifyCurrentPassword: async function (userId, currentPassword) {
    const user = await userRepo.getUserAuthById(userId);
    if (!user) {
      throw new Error('User not found');
    }

    const isMatch = await authService.comparePassword(
      currentPassword,
      user.hashed_password,
    );
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

    const isValid = otpService.verifyOtp(
      userId,
      'reset-password',
      otpCode,
      user.email,
    );
    if (!isValid) {
      throw new Error('Invalid or expired OTP');
    }

    const hashedPassword = await authService.hashPassword(newPassword);
    return await userRepo.updateUserById(userId, {
      hashed_password: hashedPassword,
    });
  },

  deleteMyAccount: async function (userId) {
    return await userRepo.deleteUserById(userId);
  },
};

export default userService;
