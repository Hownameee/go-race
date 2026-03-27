import { Router } from "express";
import postController from "../controllers/post.controller.js";
import validation from "../middlewares/validation.js";
import { followIdSchema } from "../utils/schemas/follow.schema.js";
import {
    createPostSchema,
    getPostFeedQuerySchema,
    postIdSchema,
    likeBodySchema,
    createCommentBodySchema,
    getCommentsQuerySchema,
    commentIdParamsSchema,
    deleteCommentBodySchema,
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
    "/api/posts/following/feed/:userId",
    validation(followIdSchema, "params"),
    validation(getPostFeedQuerySchema, "query"),
    postController.getFollowingFeed,
);

// Likes

router.post(
    "/api/posts/:postId/like",
    validation(postIdSchema, "params"),
    validation(likeBodySchema, "query"), // <- Delete here
    postController.likePost,
);

router.delete(
    "/api/posts/:postId/like",
    validation(postIdSchema, "params"),
    validation(likeBodySchema, "query"), // <- Delete here
    postController.unlikePost,
);

// Comments

router.post(
    "/api/posts/:postId/comments",
    validation(postIdSchema, "params"),
    validation(createCommentBodySchema, "body"),
    validation(likeBodySchema, "query"),
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
    validation(likeBodySchema, "query"), // <- Delete here
    postController.deleteComment,
);

export default router;
