import { Router } from 'express';
import userRouteController from '../controllers/user-route.controller.js';
import { auth } from '../middlewares/auth.middleware.js';

const router = Router();

router.post('/api/routes', auth, userRouteController.createRoute);
router.get('/api/routes', auth, userRouteController.getRoutes);
router.delete('/api/routes/:id', auth, userRouteController.deleteRoute);
router.patch('/api/routes/:id', auth, userRouteController.updateRoute);

export default router;
