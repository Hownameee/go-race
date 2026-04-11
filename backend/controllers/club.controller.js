import clubService from '../services/club.service.js';

const clubController = {
    getClubs: async (req, res) => {
        const userId = req.user.userId;
        const offset = parseInt(req.query.offset) || 0;
        const limit = parseInt(req.query.limit) || 10;
        const { clubs, type } = await clubService.getClubs(userId, offset, limit);
        res.ok({ clubs, type });
    },
};

export default clubController;