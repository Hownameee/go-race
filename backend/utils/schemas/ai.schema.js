import { z } from 'zod';

export const RouteResponseSchema = z.object({
  explanation: z.string(),
  waypoints: z.array(z.object({
    name: z.string(),
    latitude: z.number(),
    longitude: z.number(),
    description: z.string().optional()
  }))
});

export const AIChatRequestSchema = z.object({
  prompt: z.string(),
  history: z.array(z.object({
    role: z.string(),
    content: z.string()
  })).optional(),
  location: z.object({
    lat: z.number(),
    lng: z.number()
  }),
  currentWaypoints: z.array(z.object({
    latitude: z.number(),
    longitude: z.number()
  })).optional()
});
