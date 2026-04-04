import { Router } from 'express';
import followController from '../controllers/follow.controller.js';
import validation from '../middlewares/validation.js';
import {
  followCreateSchema,
  getFollowsQuerySchema,
} from '../utils/schemas/follow.schema.js';

const router = Router();

router.post(
  '/api/users/:followingId/follow',
  validation(followCreateSchema, 'params'),
  followController.follow,
);

router.delete(
  '/api/users/:followingId/follow',
  validation(followCreateSchema, 'params'),
  followController.unfollow,
);

router.get(
  '/api/users/followers',
  validation(getFollowsQuerySchema, 'query'),
  followController.getFollowers,
);

router.get(
  '/api/users/following',
  validation(getFollowsQuerySchema, 'query'),
  followController.getFollowing,
);

export default router;
