import recordService from '../services/record.service.js';

const recordController = {
  getList: async function (req, res) {
    const userId = req.user.userId;
    const offset = req.query.offset;
    const data = await recordService.getList(userId, offset);
    res.ok({ records: data });
  },

  getNewList: async function (req, res) {
    const userId = req.user.userId;
    const currentId = req.query.recordId;
    const data = await recordService.getNewList(userId, currentId);
    res.ok({ records: data });
  },

  getRecord: async function (req, res) {
    const userId = req.user.userId;
    const recordId = req.params.recordId;
    const data = await recordService.getRecord(userId, recordId);
    res.ok(data);
  },

  createRecord: async function (req, res) {
    const userId = req.user.userId;
    const recordData = req.body;
    await recordService.createRecord(userId, recordData);
    res.created();
  },
};

export default recordController;
