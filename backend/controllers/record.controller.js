import recordService from '../services/record.service.js';

const recordController = {
  getList: async function (req, res) {
    const userId = req.user.userId;
    const data = await recordService.getList(userId);
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
