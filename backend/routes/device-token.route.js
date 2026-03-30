import express from 'express';
import deviceTokenController from '../controllers/device-token.controller.js';

const router = express.Router();

// POST /api/device-tokens
// body: { user_id, token, platform }
router.post('/', deviceTokenController.register);

export default router;
