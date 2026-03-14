import userService from "../services/user.service.js";
import authService from "../services/auth.service.js";

const authController = {
  register: async function (req, res, next) {
    try {
      const user_data = req.body;

      const existing_user = await userService.getUserByEmail(user_data.email);
      if (existing_user) {
        console.log("Email already exists");
        return res.violate(null, "Email already exists");
      }

      const new_user_id = await userService.createUser(user_data);  
      if (!new_user_id) throw new Error("Created user account failed");
      
      return res.created({ user_id: new_user_id }, "User registered successfully");
    } catch (error) {
      next(error);
    }
  },

  login: async function (req, res, next) {
    try {
      const { email, password } = req.body;

      const user = await userService.getUserByEmail(email);
      if (!user) return res.unauthorized();

      const is_match = await authService.comparePassword(password, user.hashed_password);
      if (!is_match) return res.unauthorized();

      const token = authService.generateToken({
        user_id: user.user_id,
        emai: user.email,
        role: user.user_role
      })

      return res.ok(token, "Login successful");
    } catch (error) {
      next(error);
    }
  },

  logout: async function (req, res, next) {
    try {
      res.ok(null, "Logged out successfully");
    } catch (error) {
      next(error);
    }
  }
};

export default authController;