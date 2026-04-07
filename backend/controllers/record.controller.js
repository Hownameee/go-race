import recordService from '../services/record.service.js';

const recordController = {
  getList: async function (req, res) {
    const userId = req.user.userId;
    const offset = parseInt(req.query.offset) || 0;
    const limit = parseInt(req.query.limit) || 10;
    const data = await recordService.getList(userId, offset, limit);
    res.ok({ records: data });
  },

  getRecord: async function (req, res) {
    const userId = req.user.userId;
    const recordId = req.params.recordId;
    const data = await recordService.getRecord(userId, recordId);
    res.ok({ records: [data] });
  },

  createRecord: async function (req, res) {
    const userId = req.user.userId;
    const recordData = req.body;
    const data = await recordService.createRecord(userId, recordData);
    res.ok(data);
  },

  updateRecord: async function (req, res) {
    const userId = req.user.userId;
    const { recordId } = req.params;
    const updateData = req.body;

    try {
      await recordService.update(userId, recordId, updateData);
      res.ok();
    } catch (error) {
      res.error(null, error.message);
    }
  },

  getMyWeeklySummary(req, res, next) {
    try {
      const userId = req.user.userId;
      const { activityType, weeks } = req.query;

      const summary = recordService.getWeeklySummary(userId, activityType, weeks);
      return res.ok(summary, 'Weekly record summary fetched successfully');
    } catch (error) {
      return next(error);
    }
  },
};

export default recordController;
