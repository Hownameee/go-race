import express from 'express';
import userController from '../controllers/user.controller.js';

const router = express.Router();

router.get("/api/users", userController.getUsersBySearch);

export default router;
