import dns from 'node:dns';
dns.setDefaultResultOrder('ipv4first');
import mapConfig from '../../config/map.config.js';

/**
 * Encode a single signed integer using the Google Polyline Algorithm.
 * Uses BigInt-safe arithmetic to avoid 32-bit overflow for large lat/lng values.
 */
function encodeSignedInteger(num) {
  // Left-shift by 1 (multiply by 2), then invert if negative
  let sgnNum = num < 0 ? ~(num * 2) : num * 2;

  const chars = [];

  while (sgnNum >= 0x20) {
    chars.push(String.fromCharCode((0x20 | (sgnNum & 0x1f)) + 63));
    sgnNum = Math.floor(sgnNum / 32); // safe unsigned right-shift by 5
  }

  chars.push(String.fromCharCode(sgnNum + 63));

  return chars.join('');
}

/**
 * Encode an array of {latitude, longitude} points into a Google Polyline string.
 * Uses array + join instead of string concatenation to avoid O(n²) allocations.
 */
function encoded(points) {
  const parts = [];
  let prevLat = 0;
  let prevLng = 0;

  for (const point of points) {
    const lat = Math.round(point.latitude * 1e5);
    const lng = Math.round(point.longitude * 1e5);

    parts.push(encodeSignedInteger(lat - prevLat));
    parts.push(encodeSignedInteger(lng - prevLng));

    prevLat = lat;
    prevLng = lng;
  }

  return parts.join('');
}

export default async function getImageFromRoutePoints(points) {
  const mapStyle = 'mapbox/dark-v10';
  const dimensions = '400x430';
  const token = mapConfig.accessToken;

  let overlay;
  let viewport;

  // Single point: render a pin marker centered on the coordinate.
  if (points.length === 1) {
    const { latitude: lat, longitude: lng } = points[0];
    overlay = `pin-s+ff4444(${lng},${lat})`;
    viewport = `${lng},${lat},14`;
  }
  // Multiple points: render the full route as a polyline path.
  else {
    const encodedPoints = encoded(points);
    const safePolyline = encodeURIComponent(encodedPoints);
    const strokeWidth = 5;
    const strokeColor = 'ff4444';
    const strokeOpacity = 0.8;
    overlay = `path-${strokeWidth}+${strokeColor}-${strokeOpacity}(${safePolyline})`;
    viewport = 'auto';
  }

  const url = `https://api.mapbox.com/styles/v1/${mapStyle}/static/${overlay}/${viewport}/${dimensions}?access_token=${token}&padding=120`;

  try {
    const response = await fetch(url);

    if (!response.ok) {
      const body = await response.text().catch(() => '');
      throw new Error(
        `Mapbox API error: ${response.status} ${response.statusText}${body ? ` — ${body}` : ''}`,
      );
    }

    const arrayBuffer = await response.arrayBuffer();
    return Buffer.from(arrayBuffer);
  } catch (error) {
    console.error('Failed to fetch route image:', error);
    throw error;
  }
}
