import express from 'express';
import { auth } from '../middlewares/auth.middleware.js';
import validation from '../middlewares/validation.js';
import { AIChatRequestSchema } from '../utils/schemas/ai.schema.js';
import aiController from '../controllers/ai.controller.js';

const router = express.Router();

router.post('/routing', validation(AIChatRequestSchema), aiController.chat);

export default router;
