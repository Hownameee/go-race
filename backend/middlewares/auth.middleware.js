import jwt from 'jsonwebtoken';
import config from '../config/config.js';

export const auth = (req, res, next) => {
  try {
    // get header
    const header = req.headers.authorization;
    if (!header) {
      return res.status(401).json({ message: 'No token provided' });
    }

    // get token
    const token = header.split(' ')[1];

    // verify token
    jwt.verify(token, config.JWT_SECRET, async (err, decoded) => {
      if (err) {
        return res.status(403).json({ message: 'Expired or invalid token' });
      }

      req.user = decoded;
      next();
    });
  } catch (error) {
    console.log(error);
    return res
      .status(500)
      .json({ message: 'System error when JWT authenticating' });
  }
};
