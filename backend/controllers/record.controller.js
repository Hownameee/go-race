import recordService from '../services/record.service.js';

const recordController = {
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
