import recordRepo from '../repo/record.repo.js';

const recordService = {
  getList: async function (userId, offset = 0, quantity = 8) {
    return await recordRepo.findRecordsByUserId(userId, offset, quantity);
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
