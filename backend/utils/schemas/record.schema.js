import { z } from 'zod';

export const getWeeklySummarySchema = z
  .object({
    activityType: z.enum(['Running', 'Walking']),
    weeks: z.coerce.number().int().min(1).max(12).optional(),
  })
  .strict();
