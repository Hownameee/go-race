import OpenAI from 'openai';
import openaiConfig from '../config/openai.config.js';
import { RouteResponseSchema } from '../utils/schemas/ai.schema.js';

const openai = new OpenAI({
  apiKey: openaiConfig.apiKey,
});

async function geocodePlaceName(name) {
  const genericNames = [
    'start location', 'your current location', 'next point', 
    'optional info', 'waypoint', 'turn left', 'turn right',
    'destination', 'finish', 'start', 'end'
  ];
  
  const cleaned = name.trim().toLowerCase();
  if (cleaned.length < 3) return null;
  if (genericNames.includes(cleaned)) return null;
  if (cleaned.startsWith('point ') || /^\d+$/.test(cleaned)) return null;

  try {
    const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(name)}&format=json&limit=1`;
    const response = await fetch(url, {
      headers: {
        'User-Agent': 'GoRaceRoutePlanner/1.0'
      }
    });
    if (!response.ok) return null;
    const data = await response.json();
    if (data && data.length > 0) {
      return {
        lat: parseFloat(data[0].lat),
        lng: parseFloat(data[0].lon)
      };
    }
  } catch (error) {
    console.error(`Geocoding failed for name: ${name}`, error);
  }
  return null;
}

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
      CRITICAL INSTRUCTION: Before returning any coordinates for a named landmark, city, park, lake, street, or address specified by the user (e.g., "Hoan Kiem Lake", "Central Park", "Ben Thanh Market"), you MUST mentally simulate an exact Google Search and Google Maps lookup to retrieve the precise, real-world latitude and longitude coordinates of those landmarks. DO NOT hallucinate or guess coordinates; they must be geographically accurate so that they plot correctly on real-world maps.
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

    // Refine waypoint coordinates using Nominatim geocoding if applicable
    for (const wp of result.waypoints) {
      const geo = await geocodePlaceName(wp.name);
      if (geo) {
        // Verify that the geocoded point is close to the AI's guessed location (within ~20km / 0.2 degrees)
        // to prevent wrong landmark matches in other countries/cities.
        const isCloseToAiGuess = 
          Math.abs(geo.lat - wp.latitude) < 0.2 && 
          Math.abs(geo.lng - wp.longitude) < 0.2;
          
        if (isCloseToAiGuess) {
          wp.latitude = geo.lat;
          wp.longitude = geo.lng;
        }
      }
    }

    // Safety: If it's a new route (no current waypoints) and the AI didn't include 
    // the start point, or if the list is empty, prepend the user's location.
    // However, if the first waypoint is far from the user's device location, it means 
    // the user explicitly asked for a route in another city or landmark, so do NOT prepend the device location.
    if (currentWaypoints.length === 0 && result.waypoints.length > 0) {
      const firstWp = result.waypoints[0];
      const isStartNearUser = firstWp && 
        Math.abs(firstWp.latitude - location.lat) < 0.005 && 
        Math.abs(firstWp.longitude - location.lng) < 0.005;

      const isStartFarFromUser = firstWp && (
        Math.abs(firstWp.latitude - location.lat) > 0.05 || 
        Math.abs(firstWp.longitude - location.lng) > 0.05
      );

      if (!isStartNearUser && !isStartFarFromUser) {
        result.waypoints.unshift({
          name: "Start Location",
          latitude: location.lat,
          longitude: location.lng,
          description: "Your current location"
        });
      }
    } else if (result.waypoints.length === 0) {
      result.waypoints.push({
        name: "Start Location",
        latitude: location.lat,
        longitude: location.lng,
        description: "Your current location"
      });
    }

    return result;
  }
};

export default OpenAIService;
