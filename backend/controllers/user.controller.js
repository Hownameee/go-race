import userService from '../services/user.service.js';

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
  // getUserById: async function (req, res, next) {
  //   try {
  //     const user_id = req.params.id;
  //     const user = await userService.getUserById(user_id);
  //     res.ok(user, "User fetched successfully");
  //   } catch (error) {
  //     if (error.message === "User not found") {
  //       return res.notFound();
  //     }
  //     next(error);
  //   }
  // },
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
};

export default userController;
