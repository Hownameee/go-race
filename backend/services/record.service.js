import recordRepo from '../repo/record.repo.js';
import routePointRepo from '../repo/route-point.repo.js';
import getImageFromRoutePoints from '../utils/map/map.js';
import { getImageUrlS3, uploadImageS3 } from '../utils/s3/s3.js';

function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function startOfWeek(date) {
  const normalized = new Date(date);
  normalized.setHours(0, 0, 0, 0);
  const day = normalized.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  normalized.setDate(normalized.getDate() + diff);
  return normalized;
}

function endOfWeek(date) {
  const end = new Date(date);
  end.setDate(end.getDate() + 6);
  end.setHours(23, 59, 59, 999);
  return end;
}

const recordService = {
  getList: async function (userId, offset, limit) {
    const list = await recordRepo.findRecordsByUserId(userId, offset, limit);
    const result = await Promise.all(
      list.map(async (item) => {
        const { s3_key, ...itemWithoutS3Key } = item;
        if (s3_key) {
          const image_url = await getImageUrlS3(s3_key);
          return {
            ...itemWithoutS3Key,
            image_url,
          };
        }
        return itemWithoutS3Key;
      }),
    );

    return result;
  },

  getRecord: async function (userId, recordId) {
    const item = await recordRepo.findRecordByRecordId(userId, recordId);
    if (!item) return null;
    const { s3_key, ...itemWithoutS3Key } = item;
    if (s3_key) {
      const image_url = await getImageUrlS3(s3_key);
      return { ...itemWithoutS3Key, image_url };
    }
    return itemWithoutS3Key;
  },

  createRecord: async function (userId, recordData) {
    const { distanceKm, durationSeconds, routePoints } = recordData;

    if (distanceKm && durationSeconds && !recordData.speed) {
      recordData.speed = (distanceKm / (durationSeconds / 3600)).toFixed(2);
    }

    if (!recordData.endTime) {
      recordData.endTime = new Date().toISOString();
    }

    const dbRecordData = {
      activityType: recordData.activityType,
      title: recordData.title,
      startTime: recordData.startTime,
      endTime: recordData.endTime,
      duration: recordData.durationSeconds,
      distance: recordData.distanceKm,
      calories: recordData.caloriesBurned,
      heartRate: recordData.heartRateAvg,
      speed: recordData.speed ? parseFloat(recordData.speed) : null,
    };

    const recordId = await recordRepo.create(userId, dbRecordData);

    if (routePoints && routePoints.length > 0) {
      getImageFromRoutePoints(routePoints)
        .then((imageBuffer) => {
          const key = `records-image/${recordId + Date.now() + crypto.randomUUID()}.png`;
          return uploadImageS3(imageBuffer, key);
        })
        .then(({ key }) => {
          return recordRepo.update(userId, recordId, { s3_key: key });
        })
        .catch((err) => {
          console.error(
            `[record.service] Failed to generate/upload image for record ${recordId}:`,
            err,
          );
        });

      await routePointRepo.createMany(recordId, routePoints);
    }

    return {
      record_id: recordId,
      owner_id: userId,
      activity_type: dbRecordData.activityType,
      title: dbRecordData.title,
      start_time: dbRecordData.startTime,
      end_time: dbRecordData.endTime,
      duration_seconds: dbRecordData.duration,
      distance_km: dbRecordData.distance,
      calories_burned: dbRecordData.calories,
      heart_rate_avg: dbRecordData.heartRate,
      speed: dbRecordData.speed,
    };
  },

  update: async function (userId, recordId, updateData) {
    const dbUpdateData = { ...updateData };
    return await recordRepo.update(userId, recordId, dbUpdateData);
  },

  getWeeklySummary(userId, activityType, weeks = 12) {
    const totalWeeks = Math.min(Math.max(Number(weeks) || 12, 1), 12);
    const currentWeekStart = startOfWeek(new Date());
    const firstWeekStart = new Date(currentWeekStart);
    firstWeekStart.setDate(firstWeekStart.getDate() - (totalWeeks - 1) * 7);

    const rows = recordRepo.getWeeklySummaryRows(
      userId,
      activityType,
      formatDate(firstWeekStart),
      formatDate(endOfWeek(currentWeekStart)),
    );

    const rowMap = new Map(rows.map((row) => [row.week_start, row]));
    const points = [];

    for (let index = 0; index < totalWeeks; index += 1) {
      const weekStart = new Date(firstWeekStart);
      weekStart.setDate(firstWeekStart.getDate() + index * 7);
      const weekEnd = endOfWeek(weekStart);
      const weekKey = formatDate(weekStart);
      const row = rowMap.get(weekKey);

      points.push({
        week_start: weekKey,
        week_end: formatDate(weekEnd),
        total_distance_km: row ? Number(row.total_distance_km) : 0,
        total_duration_seconds: row ? Number(row.total_duration_seconds) : 0,
        total_elevation_gain_m: row ? Number(row.total_elevation_gain_m) : 0,
      });
    }

    return {
      activity_type: activityType,
      weeks: totalWeeks,
      points,
    };
  },
};

export default recordService;
