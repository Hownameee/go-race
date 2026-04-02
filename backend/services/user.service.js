import userRepo from '../repo/user.repo.js';
import authService from './auth.service.js';

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

  getSuggestedUsers: async function (currentUserId, limit) {
    return await userRepo.getSuggestUser(currentUserId, limit);
   
  },

  searchUsersByName: async function (currentUserId, searchQuery, limit) {
    const safeQuery = searchQuery || '';
    return await userRepo.searchUsersByName(currentUserId, safeQuery, limit);
  },

  followUser: async (followerId, followingId) => {
    return await userRepo.follow(followerId, followingId);
  },

  unfollowUser: async (followerId, followingId) => {
    return  await userRepo.unfollow(followerId, followingId);
  }
};

export default userService;