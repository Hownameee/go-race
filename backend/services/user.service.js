import userRepo from "../repo/user.repo.js";
import bcrypt from "bcrypt";
import config from "../config/config.js";
import authController from "../controllers/auth.controller.js";
import authService from "./auth.service.js";

const userService = {
  getAllUsers: async function(offset = 0, limit = 10) {
    return userRepo.getAllUsers(offset, limit);
  },

  getUserById: async function(user_id) {
    const user = await userRepo.getUserById(user_id);
    if (!user) throw new Error(`User with id: ${user_id} not found`);
    return user;
  },

  getUserByEmail: async function (email) {
    const user = await userRepo.getUserByEmail(email);
    if (!user) throw new Error(`User with email: ${email} not found`);
    return user;
  },

  createUser: async function(user_data) {
    const hashed_password = await authService.hashPassword(user_data.password);
    
    return userRepo.createUser({
      user_name: user_data.user_name,
      full_name: user_data.full_name,
      email: user_data.email,
      hashed_password: hashed_password,
      birthdate: user_data.birthdate,
    });
  },

  updateUser: async function(user_id, new_data) { 
    const existingUser = await userRepo.getUserById(user_id);
    if (!existingUser) throw new Error("User not found");

    const changes = userRepo.updateUser(user_id, new_data);
    if (changes === 0) throw new Error("No data updated");
    
    return changes;
  },

  changePassword: async function(user_id, old_password, new_password) {
    const user = await userRepo.getUserAuthById(user_id);
    if (!user) throw new Error("User not found");

    const isMatch = await bcrypt.compare(old_password, user.hashed_password);
    if (!isMatch) throw new Error("Incorrect old password");

    const new_hashed_password = await bcrypt.hash(new_password, config.SALT_ROUNDS);

    return userRepo.updatePassword(user_id, new_hashed_password);
  }
};

export default userService;