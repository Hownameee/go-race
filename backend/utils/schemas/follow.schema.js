import { z } from 'zod';

export const followCreateSchema = z.object({
  followingId: z.coerce
    .number()
    .int()
    .positive('followingId must be a positive number'),
});

export const followUserIdSchema = z.object({
  userId: z.coerce.number().int().positive('userId must be a positive number'),
});

export const getFollowsQuerySchema = z.object({
  cursor: z.string().optional(),
  limit: z.string().regex(/^\d+$/, 'limit must be a numeric string').optional(),
});
