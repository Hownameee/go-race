import z from 'zod';

const STRONG_PASSWORD_REGEX =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
const STRONG_PASSWORD_MSG =
  'Password must be at least 8 characters and include uppercase, lowercase, numbers, and special characters';

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
    username: z
      .string()
      .min(3, 'Username must be at least 3 characters long')
      .max(50, 'Username cannot exceed 50 characters')
      .regex(
        /^[a-zA-Z0-9_]+$/,
        'Username can only contain letters, numbers, and underscores',
      )
      .optional(),
    fullname: z
      .string()
      .min(1, 'Full name cannot be empty')
      .max(100, 'Full name is too long')
      .optional(),
    email: z.email('Invalid email format').optional(),
    birthdate: z
      .string()
      .regex(/^\d{4}-\d{2}-\d{2}$/, 'Birthdate must be in YYYY-MM-DD format')
      .optional(),
    avatarUrl: z.string().url('Invalid avatar URL format').optional(),
    avatar_url: z.string().url('Invalid avatar URL format').optional(),
    nationality: z.string().max(50, 'Nationality is too long').optional(),
    address: z.string().max(255, 'Address is too long').optional(),
    heightCm: z
      .number()
      .positive('Height must be a positive number')
      .max(300, 'Unrealistic height value')
      .optional(),
    height_cm: z
      .number()
      .positive('Height must be a positive number')
      .max(300, 'Unrealistic height value')
      .optional(),
    weightKg: z
      .number()
      .positive('Weight must be a positive number')
      .max(500, 'Unrealistic weight value')
      .optional(),
    weight_kg: z
      .number()
      .positive('Weight must be a positive number')
      .max(500, 'Unrealistic weight value')
      .optional(),
    password: z
      .string()
      .regex(STRONG_PASSWORD_REGEX, STRONG_PASSWORD_MSG)
      .optional(),
  })
  .strict()
  .transform((data) => ({
    username: data.username,
    fullname: data.fullname,
    email: data.email,
    birthdate: data.birthdate,
    avatarUrl: data.avatarUrl ?? data.avatar_url,
    nationality: data.nationality,
    address: data.address,
    heightCm: data.heightCm ?? data.height_cm,
    weightKg: data.weightKg ?? data.weight_kg,
    password: data.password,
  }));

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

export const verifyCurrentPasswordSchema = z
  .object({
    old_password: z.string().min(1, 'Current password is required'),
  })
  .strict();

export const requestEmailOtpSchema = z
  .object({})
  .strict();

export const verifyEmailOtpSchema = z
  .object({
    otp_code: z.string().length(6, 'OTP must be 6 digits'),
  })
  .strict();

export const confirmEmailChangeSchema = z
  .object({
    new_email: z.email('Invalid email format'),
  })
  .strict();

export const requestPasswordResetOtpSchema = z.object({}).strict();

export const requestPasswordResetOtpByEmailSchema = z
  .object({
    email: z.email('Invalid email format'),
  })
  .strict();

export const verifyPasswordResetOtpSchema = z
  .object({
    email: z.email('Invalid email format'),
    otp_code: z.string().length(6, 'OTP must be 6 digits'),
  })
  .strict();

export const resetPasswordWithOtpSchema = z
  .object({
    email: z.email('Invalid email format').optional(),
    otp_code: z.string().length(6, 'OTP must be 6 digits'),
    new_password: z.string().regex(STRONG_PASSWORD_REGEX, STRONG_PASSWORD_MSG),
    confirm_new_password: z.string(),
  })
  .strict()
  .refine((data) => data.new_password === data.confirm_new_password, {
    message: 'Confirm password does not match',
    path: ['confirm_new_password'],
  });
