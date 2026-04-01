import { z } from 'zod';

export const recordSchema = z.object({
  activityType: z.enum(['Walking', 'Running'], {
    errorMap: () => ({
      message: "Activity type must be 'Walking' or 'Running'",
    }),
  }),
  title: z.string().max(100).optional(),
  startTime: z.string(),
  endTime: z.string().optional(),
  durationSeconds: z.number().int().nonnegative(),
  distanceKm: z.number().nonnegative(),
  caloriesBurned: z.number().nonnegative(),
  heartRateAvg: z.number().int().min(0).max(250).optional(),
  speed: z.number().nonnegative(),
  imageUrl: z.string().optional(),
  routePoints: z
    .array(
      z.object({
        latitude: z.number(),
        longitude: z.number(),
        altitude: z.number().optional().nullable(),
        timestamp: z.coerce.string().datetime().or(z.number()),
        accuracy: z.number().optional().nullable(),
      }),
    )
    .optional(),
});

export const recordIdSchema = z.object({
  recordId: z.coerce.number().int().min(0),
});

export const recordUpdateSchema = z.object({
  title: z.string().max(100).optional(),
});
