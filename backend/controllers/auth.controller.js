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

      return res.created(
        { userId: newUserId },
        'User registered successfully',
      );
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
        email: user.email,
        role: user.role,
      });

      return res.ok({token}, 'Login successful');
    } catch (error) {
      next(error);
    }
  },
};

export default authController;
