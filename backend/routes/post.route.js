import { Router } from "express";
import postController from "../controllers/post.controller.js";
import validation from "../middlewares/validation.js";
import {
    createPostSchema,
    getPostFeedQuerySchema,
    postIdSchema,
    createCommentBodySchema,
    getCommentsQuerySchema,
    commentIdParamsSchema,
} from "../utils/schemas/post.schema.js";

const router = Router();

router.post(
    "/api/posts",
    validation(createPostSchema, "body"),
    postController.createPost,
);

router.get(
    "/api/posts/feed",
    validation(getPostFeedQuerySchema, "query"),
    postController.getFeed,
);

router.get(
    "/api/posts/following/feed",
    validation(getPostFeedQuerySchema, "query"),
    postController.getFollowingFeed,
);

// Likes

router.post(
    "/api/posts/:postId/like",
    validation(postIdSchema, "params"),
    postController.likePost,
);

router.delete(
    "/api/posts/:postId/like",
    validation(postIdSchema, "params"),
    postController.unlikePost,
);

// Comments

router.post(
    "/api/posts/:postId/comments",
    validation(postIdSchema, "params"),
    validation(createCommentBodySchema, "body"),
    postController.createComment,
);

router.get(
    "/api/posts/:postId/comments",
    validation(postIdSchema, "params"),
    validation(getCommentsQuerySchema, "query"),
    postController.getComments,
);

router.delete(
    "/api/posts/:postId/comments/:commentId",
    validation(commentIdParamsSchema, "params"),
    postController.deleteComment,
);

export default router;
