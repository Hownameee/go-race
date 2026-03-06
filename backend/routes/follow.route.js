import { Router } from "express";
import followController from "../controllers/follow.controller.js";
import validation from "../middlewares/validation.js";
import {
  followCreateSchema,
  followIdSchema,
  getFollowsQuerySchema,
} from "../utils/schemas/follow.schema.js";

const router = Router();

router.post(
  "/api/users/:userId/follow",
  validation(followIdSchema, "params"),
  validation(followCreateSchema, "body"),
  followController.follow,
);

router.delete(
  "/api/users/:userId/follow",
  validation(followIdSchema, "params"),
  validation(followCreateSchema, "body"),
  followController.unfollow,
);

router.get(
  "/api/users/:userId/followers",
  validation(followIdSchema, "params"),
  validation(getFollowsQuerySchema, "query"),
  followController.getFollowers,
);

router.get(
  "/api/users/:userId/following",
  validation(followIdSchema, "params"),
  validation(getFollowsQuerySchema, "query"),
  followController.getFollowing,
);

export default router;
