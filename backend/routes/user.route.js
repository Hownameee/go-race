import express from 'express';
import userController from '../controllers/user.controller.js';

const router = express.Router();

// API: GET /api/users/suggest
router.get('/api/users/suggest', userController.getSuggestedUsers);

// API: GET /api/users/search?search=john
router.get('/api/users/search', userController.getUsersBySearch);
export default router;
