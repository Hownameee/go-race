import clubService from '../services/club.service.js';

const clubController = {
  getSuggestedClubs: async function (req, res, next) {
    try {
      const currentUserId = req.user.userId;
      const limit = 10;
      const result = await clubService.getSuggestClubs(currentUserId, limit);

      return res.ok(result, 'Suggested clubs fetched successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  getClubsBySearch: async function (req, res, next) {
    try {
      const currentUserId = req.user.userId;
      const search = req.query.search || '';
      const limit = 10;
      const result = await clubService.searchClubsByName(
        currentUserId,
        search,
        limit,
      );

      return res.ok({ clubs: result }, 'Search clubs successfully.');
    } catch (error) {
      if (error.status === 409) {
        console.error(error);
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  joinClub: async function (req, res, next) {
    try {
      const currentUserId = req.user.userId;
      const clubId = req.params.clubId;
      const result = await clubService.joinClub(currentUserId, clubId);

      return res.ok({ result }, result.message);
    } catch (error) {
      if (error.status === 409 || error.status === 404) {
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },

  leaveClub: async function (req, res, next) {
    try {
      const currentUserId = req.user.userId;
      const clubId = req.params.clubId;
      const result = await clubService.leaveClub(currentUserId, clubId);

      return res.ok({ result }, result.message);
    } catch (error) {
      if (error.status === 400 || error.status === 404) {
        return res.violate(null, error.message);
      }
      return next(error);
    }
  },
};

export default clubController;
