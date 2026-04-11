import express from 'express';
import clubController from '../controllers/club.controller.js';

const router = express.Router();

// API: GET /api/clubs/suggest
router.get('/api/clubs/suggest', clubController.getSuggestedClubs);

// API: GET /api/clubs/search?search=john
router.get('/api/clubs/search', clubController.getClubsBySearch);

// API: POST /api/clubs/:clubId/join
router.post('/api/clubs/:clubId/join', clubController.joinClub);

export default router;
