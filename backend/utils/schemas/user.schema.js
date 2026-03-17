import { z } from 'zod';

const STRONG_PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
const STRONG_PASSWORD_MSG = 'Password must be at least 8 characters and include uppercase, lowercase, numbers, and special characters';

const SHIRT_SIZES = ['2XS', 'XS', 'S', 'M', 'L', 'XL', '2XL', '3XL', '4XL', '5XL'];

export const registerSchema = z
  .object({
    username: z
      .string()
      .min(3, 'Username must be at least 3 characters long')
      .max(50, 'Username cannot exceed 50 characters')
      .regex(
        /^[a-zA-Z0-9_]+$/,
        'Username can only contain letters, numbers, and underscores',
      ),
    fullname: z
      .string()
      .min(1, 'Full name cannot be empty')
      .max(100, 'Full name is too long'),
    email: z.email('Invalid email format'),
    password: z.string().regex(STRONG_PASSWORD_REGEX, STRONG_PASSWORD_MSG),
    birthdate: z
      .string()
      .regex(/^\d{4}-\d{2}-\d{2}$/, 'Birthdate must be in YYYY-MM-DD format'),
  })
  .strict();

export const loginSchema = z
  .object({
    email: z.email('Invalid email format'),
    password: z.string().min(1, 'Password is required'),
  })
  .strict();

export const updateProfileSchema = z
  .object({
    fullname: z
      .string()
      .min(1, 'Full name cannot be empty')
      .max(100, 'Full name is too long')
      .optional(),
    birthdate: z
      .string()
      .regex(/^\d{4}-\d{2}-\d{2}$/, 'Birthdate must be in YYYY-MM-DD format')
      .optional(),
    avatar_url: z.string().url('Invalid avatar URL format').optional(),
    nationality: z.string().max(50, 'Nationality is too long').optional(),
    address: z.string().max(255, 'Address is too long').optional(),
    height_cm: z
      .number()
      .positive('Height must be a positive number')
      .max(300, 'Unrealistic height value')
      .optional(),
    weight_kg: z
      .number()
      .positive('Weight must be a positive number')
      .max(500, 'Unrealistic weight value')
      .optional(),
    shirt_size: z
      .enum(SHIRT_SIZES, {
        errorMap: () => ({
          message: 'Invalid shirt size. Must be between 2XS and 5XL.',
        }),
      })
      .optional(),
  })
  .strict();

export const changePasswordSchema = z
  .object({
    old_password: z.string().min(1, 'Old password is required'),
    new_password: z.string().regex(STRONG_PASSWORD_REGEX, STRONG_PASSWORD_MSG),
    confirm_new_password: z.string(),
  })
  .strict()
  .refine((data) => data.new_password === data.confirm_new_password, {
    message: 'Confirm password does not match',
    path: ['confirm_new_password'],
  })
  .refine((data) => data.old_password !== data.new_password, {
    message: 'New password must be different from the old password',
    path: ['new_password'],
  });
