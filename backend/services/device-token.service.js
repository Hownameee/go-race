import deviceTokenRepository from '../repo/device-token.repo.js';

const deviceTokenService = {
  register: async function ({ userId, token, platform = 'android' }) {
    if (!userId || !token) {
      throw new Error('userId and token are required');
    }
    await deviceTokenRepository.upsert({ userId, token, platform });
  },

  getTokensByUserId: async function (userId) {
    const rows = await deviceTokenRepository.findByUserId(userId);
    return rows.map((item) => item.token).filter(Boolean);
  },

  getAllTokens: async function () {
    const rows = await deviceTokenRepository.findAllTokens();
    return rows.map((item) => item.token).filter(Boolean);
  },

  removeInvalidTokens: async function (invalidTokens) {
    if (!Array.isArray(invalidTokens) || invalidTokens.length === 0) return;
    await deviceTokenRepository.deleteByTokens(invalidTokens);
  },
};

export default deviceTokenService;
