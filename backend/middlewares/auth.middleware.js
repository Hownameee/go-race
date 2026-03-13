import authService from "../services/auth.service";

const authMiddleware = {
  isAdmin: (req, res, next) => {
    const userRole = req.headers['x-user-role']; 

    if (userRole !== 'admin') {
      return res.status(403).json({
        success: false,
        data: null,
        message: "Access denied. Admin privileges required."
      });
    }
    
    next();
  }
};

export default authMiddleware;