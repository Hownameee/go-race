import deviceTokenService from '../services/device-token.service.js';

const deviceTokenController = {
  register: async function (req, res) {
    try {
      const { user_id, token, platform } = req.body;
      await deviceTokenService.register({
        userId: user_id,
        token,
        platform: platform || 'android',
      });
      res.ok({ success: true }, 'Token registered');
    } catch (err) {
      console.error(err);
      res.error(null, err.message);
    }
  },
};

export default deviceTokenController;
