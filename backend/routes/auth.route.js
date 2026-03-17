import express from 'express';
import authController from '../controllers/auth.controller.js';
import validation from '../middlewares/validation.js';
import { registerSchema, loginSchema } from '../utils/schemas/user.schema.js';

const router = express.Router();

router.post('/register', validation(registerSchema), authController.register);
router.post('/login', validation(loginSchema), authController.login);

export default router;
