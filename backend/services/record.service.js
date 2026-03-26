import recordRepo from '../repo/record.repo.js';
import { getImageUrlS3 } from '../utils/s3/s3.js';

const recordService = {
  getList: async function (userId, offset = 0, quantity = 8) {
    const list = await recordRepo.findRecordsByUserId(userId, offset, quantity);

    const result = await Promise.all(
      list.map(async (item) => {
        if (item.s3_key) {
          const image_url = await getImageUrlS3(item.s3_key);
          return {
            ...item,
            image_url,
          };
        }
        return item;
      }),
    );

    return result;
  },

  getRecord: async function (userId, recordId) {
    return await recordRepo.findRecordByRecordId(userId, recordId);
  },

  createRecord: async function (userId, recordData) {
    const { distance, duration } = recordData;
    if (distance && duration && !recordData.speed) {
      recordData.speed = (distance / (duration / 3600)).toFixed(2);
    }
    if (!recordData.endTime) {
      recordData.endTime = new Date()
        .toISOString()
        .replace('T', ' ')
        .substring(0, 19);
    }
    return await recordRepo.create(userId, recordData);
  },
};

export default recordService;
