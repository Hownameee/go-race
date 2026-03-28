import { Router } from "express";
import followController from "../controllers/follow.controller.js";
import validation from "../middlewares/validation.js";
import {
  followCreateSchema,
  getFollowsQuerySchema,
} from "../utils/schemas/follow.schema.js";

const router = Router();

router.post(
  "/api/users/follow",
  validation(followCreateSchema, "body"),
  followController.follow,
);

router.delete(
  "/api/users/follow",
  validation(followCreateSchema, "body"),
  followController.unfollow,
);

router.get(
  "/api/users/followers",
  validation(getFollowsQuerySchema, "query"),
  followController.getFollowers,
);

router.get(
  "/api/users/following",
  validation(getFollowsQuerySchema, "query"),
  followController.getFollowing,
);

export default router;
