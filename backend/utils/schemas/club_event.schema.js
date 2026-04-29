import { z } from 'zod';

export const eventIdSchema = z.object({
  eventId: z.coerce
    .number()
    .int()
    .positive('eventId must be a positive number'),
});

export const createClubEventSchema = z
  .object({
    title: z.string().min(1, 'Title is required'),
    description: z.string().optional(),
    target_distance: z
      .number()
      .nonnegative('Distance must be non-negative')
      .optional(),
    target_duration_seconds: z
      .number()
      .int()
      .nonnegative('Duration must be non-negative')
      .optional(),
    start_time: z
      .string()
      .datetime({ message: 'Invalid datetime string! Must be UTC.' }),
    end_time: z
      .string()
      .datetime({ message: 'Invalid datetime string! Must be UTC.' })
      .optional(),
  })
  .refine(
    (data) => {
      if (data.end_time) {
        return new Date(data.start_time) < new Date(data.end_time);
      }
      return true;
    },
    {
      message: 'End time must be after start time',
      path: ['end_time'],
    },
  )
  .refine(
    (data) => {
      return (
        (data.target_distance !== undefined && data.target_distance > 0) ||
        (data.target_duration_seconds !== undefined &&
          data.target_duration_seconds > 0)
      );
    },
    {
      message: 'Event must have a distance or duration target greater than 0',
      path: ['target_distance'],
    },
  );
