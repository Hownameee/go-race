import { Router } from 'express';
import postController from '../controllers/post.controller.js';
import validation from '../middlewares/validation.js';
import { auth } from '../middlewares/auth.middleware.js';
import {
  createPostSchema,
  getPostFeedQuerySchema,
  postIdSchema,
  createCommentBodySchema,
  getCommentsQuerySchema,
  commentIdParamsSchema,
} from '../utils/schemas/post.schema.js';

const router = Router();

router.post(
  '/api/posts',
  auth,
  validation(createPostSchema, 'body'),
  postController.createPost,
);

router.get(
  '/api/posts/feed',
  validation(getPostFeedQuerySchema, 'query'),
  postController.getFeed,
);

router.get(
  '/api/posts/following/feed',
  auth,
  validation(getPostFeedQuerySchema, 'query'),
  postController.getFollowingFeed,
);

router.get(
  '/api/posts/me',
  auth,
  validation(getPostFeedQuerySchema, 'query'),
  postController.getMyPosts,
);

router.post(
  '/api/posts/:postId/like',
  auth,
  validation(postIdSchema, 'params'),
  postController.likePost,
);

router.delete(
  '/api/posts/:postId/like',
  auth,
  validation(postIdSchema, 'params'),
  postController.unlikePost,
);

router.post(
  '/api/posts/:postId/comments',
  auth,
  validation(postIdSchema, 'params'),
  validation(createCommentBodySchema, 'body'),
  postController.createComment,
);

router.get(
  '/api/posts/:postId/comments',
  auth,
  validation(postIdSchema, 'params'),
  validation(getCommentsQuerySchema, 'query'),
  postController.getComments,
);

router.delete(
  '/api/posts/:postId/comments/:commentId',
  auth,
  validation(commentIdParamsSchema, 'params'),
  postController.deleteComment,
);

export default router;
