import express from 'express';
import deviceTokenController from '../controllers/device-token.controller.js';

const router = express.Router();

// POST /api/device-tokens
router.post('/', deviceTokenController.register);

// DELETE /api/device-tokens
router.delete('/', deviceTokenController.unregister);

export default router;
