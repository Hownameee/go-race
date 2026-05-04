import userRouteRepo from '../repo/user-route.repo.js';

const userRouteService = {
  async createRoute(userId, payload) {
    if (!payload.name) {
      const error = new Error('Route name is required');
      error.status = 400;
      throw error;
    }

    const routeId = await userRouteRepo.create(userId, payload);
    const created = await userRouteRepo.getById(routeId);
    return created;
  },

  async getRoutesByUser(userId) {
    return await userRouteRepo.getByUserId(userId);
  },

  async deleteRoute(userId, routeId) {
    await userRouteRepo.delete(userId, routeId);
    return { success: true };
  },

  async updateRoute(userId, routeId, payload) {
    if (!payload.name) {
      const error = new Error('Route name is required');
      error.status = 400;
      throw error;
    }
    await userRouteRepo.update(userId, routeId, payload);
    return { success: true };
  }
};

export default userRouteService;
