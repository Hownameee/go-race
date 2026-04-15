import clubService from '../services/club.service.js';

const clubController = {
    getClubs: async (req, res) => {
        const userId = req.user.userId;
        const offset = parseInt(req.query.offset) || 0;
        const limit = parseInt(req.query.limit) || 10;
        const { clubs, type } = await clubService.getClubs(userId, offset, limit);
        res.ok({ clubs, type });
    },

    getClubById: async (req, res) => {
        const userId = req.user.userId;
        const clubId = req.params.clubId;
        const club = await clubService.getClubByIdAndUserId(userId, clubId);
        res.ok({ clubs: [club] });
    },

    joinClub: async (req, res) => {
        try {
            const userId = req.user.userId;
            const clubId = req.params.clubId;
            const result = await clubService.joinClub(clubId, userId);
            res.ok({ result: result.status === 'approved' ? 'Joined' : 'Request sent' });
        } catch (error) {
            if (error.message === 'Club not found') {
                return res.notFound();
            }
            if (error.message === 'Already a member or request pending') {
                return res.badRequest(null, error.message);
            }
            res.error(null, error.message);
        }
    },

    leaveClub: async (req, res) => {
        try {
            const userId = req.user.userId;
            const clubId = req.params.clubId;
            const result = await clubService.leaveClub(clubId, userId);
            res.ok({ result: result.message });
        } catch (error) {
            res.badRequest(null, error.message);
        }
    },

    createClub: async (req, res) => {
        try {
            const userId = req.user.userId;
            const { name, description, privacy_type } = req.body;

            if (!name) {
                return res.badRequest(null, "Club name is required");
            }

            const result = await clubService.createClub(name, description, privacy_type, userId);
            res.ok(result);
        } catch (error) {
            res.error(null, error.message);
        }
    },
};

export default clubController;