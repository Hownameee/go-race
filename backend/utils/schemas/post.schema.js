import { z } from 'zod';

export const createPostSchema = z.object({
  record_id: z
    .number()
    .int()
    .positive('record_id must be a positive number')
    .optional(),
  title: z.string().max(255, 'title must be at most 255 characters').optional(),
  description: z
    .string()
    .max(2000, 'description must be at most 2000 characters')
    .optional(),
  photo_url: z.string().optional(),
  view_mode: z.enum(['Everyone', 'Followers', 'Self']).default('Everyone'),
});

export const getPostFeedQuerySchema = z.object({
  cursor: z.string().optional(),
  limit: z.string().regex(/^\d+$/, 'limit must be a numeric string').optional(),
});

export const postIdSchema = z.object({
  postId: z.string().regex(/^\d+$/, 'postId must be a numeric string'),
});

export const createCommentBodySchema = z.object({
  content: z
    .string()
    .min(1, 'content must not be empty')
    .max(2000, 'content must be at most 2000 characters'),
});

export const getCommentsQuerySchema = z.object({
  cursor: z.string().optional(),
  limit: z.string().regex(/^\d+$/, 'limit must be a numeric string').optional(),
});

export const commentIdParamsSchema = z.object({
  postId: z.string().regex(/^\d+$/, 'postId must be a numeric string'),
  commentId: z.string().regex(/^\d+$/, 'commentId must be a numeric string'),
});
