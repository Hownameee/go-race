import { z } from "zod";

export const followCreateSchema = z.object({
  followingId: z
    .number()
    .int()
    .positive("followingId must be a positive number"),
});

export const followIdSchema = z.object({
  userId: z.string().regex(/^\d+$/, "userId must be a numeric string"),
});

export const getFollowsQuerySchema = z.object({
  cursor: z.string().optional(),
  limit: z.string().regex(/^\d+$/, "limit must be a numeric string").optional(),
});
