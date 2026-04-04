import recordService from '../services/record.service.js';

const recordController = {
  getList: async function (req, res) {
    const userId = req.user.userId;
    const offset = parseInt(req.query.offset) || 0;
    const limit = parseInt(req.query.limit) || 10;
    console.log(userId, offset, limit);
    const data = await recordService.getList(userId, offset, limit);
    console.log(data);
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
};

export default recordController;
