import { z } from 'zod';

export const createNotificationSchema = z.object({
  user_id: z.number().int().positive(),

  type: z.enum(['follow', 'like', 'comment', 'system']),

  actor_id: z.number().int().positive().optional().nullable(),

  activity_id: z.number().int().positive().optional().nullable(),

  title: z.string().min(1).max(255).optional(),

  message: z.string().min(1).max(500).optional(),
});

export const markAsReadSchema = z.object({
  id: z.coerce.number().int().positive(),
});
