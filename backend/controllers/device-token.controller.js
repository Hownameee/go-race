import deviceTokenService from '../services/device-token.service.js';

const deviceTokenController = {
  register: async function (req, res) {
    try {
      const { token, platform } = req.body;
      await deviceTokenService.register({
        userId: req.user.userId,
        token,
        platform: platform || 'android',
      });
      res.ok({ success: true }, 'Token registered');
    } catch (err) {
      console.error(err);
      res.error(null, err.message);
    }
  },

  unregister: async function (req, res) {
    try {
      const { token } = req.body;
      if (!token) {
        return res.badRequest(null, 'Token is required');
      }
      await deviceTokenService.unregister(req.user.userId, token);
      res.ok({ success: true }, 'Token unregistered');
    } catch (err) {
      console.error(err);
      res.error(null, err.message);
    }
  },
};

export default deviceTokenController;
