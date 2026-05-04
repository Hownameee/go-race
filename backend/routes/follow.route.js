import { Router } from 'express';
import followController from '../controllers/follow.controller.js';
import { auth } from '../middlewares/auth.middleware.js';
import validation from '../middlewares/validation.js';
import {
  followCreateSchema,
  followUserIdSchema,
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
  auth,
  validation(getFollowsQuerySchema, 'query'),
  followController.getFollowers,
);

router.get(
  '/api/users/following',
  auth,
  validation(getFollowsQuerySchema, 'query'),
  followController.getFollowing,
);

router.get(
  '/api/users/:userId/followers',
  auth,
  validation(followUserIdSchema, 'params'),
  validation(getFollowsQuerySchema, 'query'),
  followController.getUserFollowers,
);

router.get(
  '/api/users/:userId/following',
  auth,
  validation(followUserIdSchema, 'params'),
  validation(getFollowsQuerySchema, 'query'),
  followController.getUserFollowing,
);

export default router;
