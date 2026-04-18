import { z } from 'zod';

export const clubIdSchema = z.object({
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

export const updateClubSchema = z.object({
  name: z.string().min(1, 'Name cannot be empty').optional(),
  description: z.string().optional(),
  image_base64: z.string().optional(),
  image_content_type: z.string().optional(),
});
