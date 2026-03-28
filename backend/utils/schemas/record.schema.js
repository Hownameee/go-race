import { z } from 'zod';

export const recordSchema = z.object({
  activityType: z.enum(['Walking', 'Running'], {
    errorMap: () => ({
      message: "Activity type must be 'Walking' or 'Running'",
    }),
  }),
  startTime: z.iso.datetime({ message: 'Invalid startTime format (ISO 8601)' }),
  endTime: z.iso
    .datetime({ message: 'Invalid endTime format (ISO 8601)' })
    .optional(),
  duration: z.number().int().positive(),
  distance: z.number().nonnegative(),
  calories: z.number().nonnegative(),
  heartRate: z.number().int().min(30).max(250).optional(),
  speed: z.number().nonnegative(),
});

export const recordIdSchema = z.object({
  recordId: z.coerce.number().int().min(0),
});
