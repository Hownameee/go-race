import recordRepo from '../repo/record.repo.js';
import routePointRepo from '../repo/route-point.repo.js';
import { getImageUrlS3 } from '../utils/s3/s3.js';

const recordService = {
  getList: async function (userId, offset = 0, quantity = 8) {
    const list = await recordRepo.findRecordsByUserId(userId, offset, quantity);
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

  getNewList: async function (userId, currentId) {
    const list = await recordRepo.findNewRecordsByCurrentId(userId, currentId);
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
    return await recordRepo.findRecordByRecordId(userId, recordId);
  },

  /**
   * Phase 6: Backend Storage.
   * Creates the definitive record and links the received route points to it.
   */
  createRecord: async function (userId, recordData) {
    const { distanceKm, durationSeconds, routePoints } = recordData;

    // Use frontend's speed or calculate it as a fallback
    if (distanceKm && durationSeconds && !recordData.speed) {
      recordData.speed = (distanceKm / (durationSeconds / 3600)).toFixed(2);
    }

    if (!recordData.endTime) {
      recordData.endTime = new Date().toISOString();
    }

    // Mapping frontend fields to backend repo expectations
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
};

export default recordService;
