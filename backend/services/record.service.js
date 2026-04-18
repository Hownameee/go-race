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

function startOfYear(date) {
  return new Date(date.getFullYear(), 0, 1, 0, 0, 0, 0);
}

function normalizeAggregate(row) {
  return {
    total_activities: Number(row?.total_activities || 0),
    total_distance_km: Number(row?.total_distance_km || 0),
    total_duration_seconds: Number(row?.total_duration_seconds || 0),
    total_elevation_gain_m: Number(row?.total_elevation_gain_m || 0),
  };
}

function roundTo(value, digits = 1) {
  return Number(Number(value || 0).toFixed(digits));
}

function parseDateOnly(dateStr) {
  const [year, month, day] = dateStr.split('-').map(Number);
  return new Date(year, month - 1, day);
}

function diffDays(laterDate, earlierDate) {
  const oneDayMs = 24 * 60 * 60 * 1000;
  return Math.round((laterDate - earlierDate) / oneDayMs);
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

  getProfileStatistics(userId, activityType = null) {
    const now = new Date();
    const currentWeekStart = startOfWeek(now);
    const weeklyWindowStart = new Date(currentWeekStart);
    weeklyWindowStart.setDate(weeklyWindowStart.getDate() - 11 * 7);

    const weeklyTotals = normalizeAggregate(
      recordRepo.getAggregateStats(
        userId,
        activityType,
        formatDate(weeklyWindowStart),
        formatDate(endOfWeek(currentWeekStart)),
      ),
    );
    const yearToDate = normalizeAggregate(
      recordRepo.getAggregateStats(
        userId,
        activityType,
        formatDate(startOfYear(now)),
        formatDate(now),
      ),
    );
    const allTime = normalizeAggregate(recordRepo.getAggregateStats(userId, activityType, null, null));

    return {
      activity_type: activityType,
      weekly_average: {
        total_activities: roundTo(weeklyTotals.total_activities / 12, 1),
        total_distance_km: roundTo(weeklyTotals.total_distance_km / 12, 2),
        total_duration_seconds: Math.round(weeklyTotals.total_duration_seconds / 12),
      },
      year_to_date: yearToDate,
      all_time: {
        total_activities: allTime.total_activities,
        total_distance_km: allTime.total_distance_km,
      },
    };
  },

  getStreakByUserId(userId) {
    const rows = recordRepo.getActiveRecordDates(userId);

    if (!rows || rows.length === 0) {
      return {
        current_streak_days: 0,
        longest_streak_days: 0,
        total_active_days: 0,
        last_record_date: null,
        today_has_record: false,
      };
    }

    const dates = rows.map((row) => row.activity_date);
    const totalActiveDays = dates.length;
    const lastRecordDate = dates[0];

    const today = formatDate(new Date());
    const yesterdayDate = new Date();
    yesterdayDate.setDate(yesterdayDate.getDate() - 1);
    const yesterday = formatDate(yesterdayDate);

    const todayHasRecord = lastRecordDate === today;

    let longestStreak = 1;
    let runningStreak = 1;

    for (let index = 0; index < dates.length - 1; index += 1) {
      const current = parseDateOnly(dates[index]);
      const next = parseDateOnly(dates[index + 1]);

      if (diffDays(current, next) === 1) {
        runningStreak += 1;
        longestStreak = Math.max(longestStreak, runningStreak);
      } else {
        runningStreak = 1;
      }
    }

    let currentStreak = 0;

    if (lastRecordDate === today || lastRecordDate === yesterday) {
      currentStreak = 1;

      for (let index = 0; index < dates.length - 1; index += 1) {
        const current = parseDateOnly(dates[index]);
        const next = parseDateOnly(dates[index + 1]);

        if (diffDays(current, next) === 1) {
          currentStreak += 1;
        } else {
          break;
        }
      }
    }

    return {
      current_streak_days: currentStreak,
      longest_streak_days: longestStreak,
      total_active_days: totalActiveDays,
      last_record_date: lastRecordDate,
      today_has_record: todayHasRecord,
    };
  },
};

export default recordService;
