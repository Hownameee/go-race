import { z } from 'zod';

export const clubIdSchema = z.object({
  clubId: z.coerce.number().int().positive('clubId must be a positive number'),
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

export const memberIdSchema = z.object({
  clubId: z.coerce.number().int().positive('clubId must be a positive number'),
  userId: z.coerce.number().int().positive('userId must be a positive number'),
});

export const updateMemberStatusSchema = z.object({
  status: z.enum(['approved', 'rejected', 'left'], {
    errorMap: () => ({
      message: "Status must be 'approved', 'rejected', or 'left'",
    }),
  }),
});

export const updateMemberRoleSchema = z.object({
  role: z.enum(['admin', 'member'], {
    errorMap: () => ({ message: "Role must be 'admin' or 'member'" }),
  }),
});
