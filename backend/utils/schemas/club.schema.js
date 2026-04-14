import { z } from 'zod';

export const joinClubSchema = z.object({
  clubId: z.coerce
    .number()
    .int()
    .positive('clubId must be a positive number'),
});

export const createClubSchema = z.object({
  name: z.string().min(1, 'Name is required'),
  description: z.string().optional(),
  privacy_type: z.enum(['public', 'private']).default('public'),
});
