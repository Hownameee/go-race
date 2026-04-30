import clubService from '../services/club.service.js';
import postService from '../services/post.service.js';

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

  getClubPosts: async (req, res, next) => {
    try {
      const userId = req.user.userId;
      const clubId = req.params.clubId;
      const { cursor, limit } = req.query;
      const result = await postService.getClubPosts(
        clubId,
        userId,
        cursor,
        limit,
      );
      res.ok(result, 'Club posts retrieved successfully.');
    } catch (error) {
      if (error.status === 403 || error.status === 404) {
        return res.violate(null, error.message);
      }
      next(error);
    }
  },

  joinClub: async (req, res) => {
    try {
      const userId = req.user.userId;
      const clubId = req.params.clubId;
      const result = await clubService.joinClub(clubId, userId);
      res.ok({
        result: result.status === 'approved' ? 'Joined' : 'Request sent',
      });
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
        return res.badRequest(null, 'Club name is required');
      }

      const result = await clubService.createClub(
        name,
        description,
        privacy_type,
        userId,
      );
      res.ok(result);
    } catch (error) {
      res.error(null, error.message);
    }
  },

  getAdmins: async (req, res) => {
    try {
      const clubId = parseInt(req.params.clubId);
      const admins = await clubService.getClubAdmins(clubId);
      res.ok(admins);
    } catch (error) {
      res.error(null, error.message);
    }
  },

  checkIsLeader: async (req, res) => {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const isLeader = await clubService.isLeader(clubId, userId);
      res.ok({ is_leader: isLeader });
    } catch (error) {
      res.error(null, error.message);
    }
  },

  checkIsAdmin: async (req, res) => {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const isAdmin = await clubService.isAdminOrLeader(clubId, userId);
      res.ok({ is_admin: isAdmin });
    } catch (error) {
      res.error(null, error.message);
    }
  },

  updateClub: async (req, res) => {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const { name, description, image_base64, image_content_type } = req.body;
      await clubService.updateClub(userId, clubId, {
        name,
        description,
        imageBase64: image_base64,
        imageContentType: image_content_type,
      });
      res.ok(null, 'Club updated successfully.');
    } catch (error) {
      if (error.status === 403) return res.violate(null, error.message);
      if (error.status === 400) return res.badRequest(null, error.message);
      res.error(null, error.message);
    }
  },

  getClubStats: async (req, res) => {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const stats = await clubService.getClubStats(clubId, userId);
      res.ok(stats, 'Club stats retrieved successfully.');
    } catch (error) {
      res.error(null, error.message);
    }
  },

  getMembers: async (req, res) => {
    try {
      const userId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const members = await clubService.getMembers(clubId, userId);
      res.ok(members, 'Club members retrieved successfully.');
    } catch (error) {
      res.error(null, error.message);
    }
  },

  updateMemberStatus: async (req, res) => {
    try {
      const requesterId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const targetUserId = parseInt(req.params.userId);
      const { status } = req.body;
      const result = await clubService.updateMemberStatus(
        clubId,
        requesterId,
        targetUserId,
        status,
      );
      res.ok(result);
    } catch (error) {
      const status = error.status || 500;
      if (status === 403) return res.violate(null, error.message);
      if (status === 400) return res.badRequest(null, error.message);
      res.error(null, error.message);
    }
  },

  updateMemberRole: async (req, res) => {
    try {
      const requesterId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const targetUserId = parseInt(req.params.userId);
      const { role } = req.body;
      const result = await clubService.updateMemberRole(
        clubId,
        requesterId,
        targetUserId,
        role,
      );
      res.ok(result);
    } catch (error) {
      const status = error.status || 500;
      if (status === 403) return res.violate(null, error.message);
      if (status === 400) return res.badRequest(null, error.message);
      res.error(null, error.message);
    }
  },

  transferLeadership: async (req, res) => {
    try {
      const currentLeaderId = req.user.userId;
      const clubId = parseInt(req.params.clubId);
      const { new_leader_id } = req.body;
      const result = await clubService.transferLeadership(
        clubId,
        currentLeaderId,
        parseInt(new_leader_id),
      );
      res.ok(result);
    } catch (error) {
      const status = error.status || 500;
      if (status === 403) return res.violate(null, error.message);
      if (status === 400) return res.badRequest(null, error.message);
      if (status === 404) return res.notFound(null, error.message);
      res.error(null, error.message);
    }
  },

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
};

export default clubController;
