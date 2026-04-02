import express from 'express';
import userController from '../controllers/user.controller.js';

const router = express.Router();

// API: GET /api/users/suggest
router.get("/api/users/suggest", userController.getSuggestedUsers);

// API: GET /api/users/search?search=john
router.get("/api/users/search", userController.getUsersBySearch);

// API: POST /api/users/123/follow
router.post('/api/users/:userId/follow', userController.followUser);

// API: DELETE /api/users/123/follow
router.delete('/api/users/:userId/follow', userController.unfollowUser);
export default router;
