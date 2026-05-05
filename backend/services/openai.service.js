import OpenAI from 'openai';
import openaiConfig from '../config/openai.config.js';
import { RouteResponseSchema } from '../utils/schemas/ai.schema.js';

const openai = new OpenAI({
  apiKey: openaiConfig.apiKey,
});

export const OpenAIService = {
  async generateRoute(prompt, history, location, currentWaypoints = []) {
    const currentRouteContext = currentWaypoints.length > 0 
      ? `The user has already drawn a route with the following waypoints: ${JSON.stringify(currentWaypoints)}. 
         If the user asks to "fix", "change", or "improve" the route, use these as a base.`
      : "The user has not drawn a route yet.";

    const systemPrompt = `
      You are an expert route planner for GoRace. 
      The user's current location is latitude: ${location.lat}, longitude: ${location.lng}.
      CRITICAL INSTRUCTION: The route MUST start at or very close to the user's current location (${location.lat}, ${location.lng}) and the entire route MUST be in the user's local vicinity, unless they explicitly name a different city or starting address. Do not generate routes in distant locations.
      CRITICAL INSTRUCTION: The route MUST be suitable and safe for walking or running (prefer parks, pedestrian paths, and scenic routes; avoid highways and major roads without sidewalks). Keep waypoints relatively close to each other to form a cohesive local path.
      
      ${currentRouteContext}
      
      Your goal is to plan a walking or running route based on the user's natural language description.
      Output ONLY a JSON object that matches this schema:
      {
        "explanation": "Briefly explain the route to the user, highlighting why it is good for walking/running.",
        "waypoints": [
          {"name": "Start Location", "latitude": ${location.lat}, "longitude": ${location.lng}, "description": "Starting point"},
          {"name": "Next Point", "latitude": 0.0, "longitude": 0.0, "description": "optional info"}
        ]
      }
    `;

    const messages = [
      { role: 'system', content: systemPrompt },
      ...history.map(msg => ({
        role: msg.role === 'user' ? 'user' : 'assistant',
        content: msg.content
      })),
      { role: 'user', content: prompt }
    ];

    const response = await openai.chat.completions.create({
      model: openaiConfig.model,
      messages: messages,
      response_format: { type: 'json_object' }
    });

    const content = JSON.parse(response.choices[0].message.content);
    const result = RouteResponseSchema.parse(content);

    // Safety: If it's a new route (no current waypoints) and the AI didn't include 
    // the start point, or if the list is empty, prepend the user's location.
    if (currentWaypoints.length === 0) {
      const firstWp = result.waypoints[0];
      const isStartNearUser = firstWp && 
        Math.abs(firstWp.latitude - location.lat) < 0.001 && 
        Math.abs(firstWp.longitude - location.lng) < 0.001;

      if (!isStartNearUser) {
        result.waypoints.unshift({
          name: "Start Location",
          latitude: location.lat,
          longitude: location.lng,
          description: "Your current location"
        });
      }
    }

    return result;
  }
};

export default OpenAIService;
