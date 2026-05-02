import userRouteService from '../services/user-route.service.js';

const userRouteController = {
  createRoute: async (req, res, next) => {
    try {
      const userId = req.user.userId;
      const result = await userRouteService.createRoute(userId, req.body);
      res.ok(result);
    } catch (e) {
      next(e);
    }
  },

  getRoutes: async (req, res, next) => {
    try {
      const userId = req.user.userId;
      const result = await userRouteService.getRoutesByUser(userId);
      res.ok(result);
    } catch (e) {
      next(e);
    }
  },

  deleteRoute: async (req, res, next) => {
    try {
      const userId = req.user.userId;
      const routeId = req.params.id;
      const result = await userRouteService.deleteRoute(userId, routeId);
      res.ok(result);
    } catch (e) {
      next(e);
    }
  },

  updateRoute: async (req, res, next) => {
    try {
      const userId = req.user.userId;
      const routeId = req.params.id;
      const result = await userRouteService.updateRoute(userId, routeId, req.body);
      res.ok(result);
    } catch (e) {
      next(e);
    }
  }
};

export default userRouteController;
