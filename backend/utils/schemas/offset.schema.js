import z from 'zod';

export const offsetSchema = z.object({
  offset: z.coerce.number().min(0).int(),
});
