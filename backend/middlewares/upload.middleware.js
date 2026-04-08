import path from 'path';
import multer from 'multer';

const allowedMimeTypes = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
]);

const upload = multer({
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 5 * 1024 * 1024,
    files: 1,
  },
  fileFilter: (req, file, cb) => {
    if (!allowedMimeTypes.has(file.mimetype)) {
      const error = new Error('Only JPG, PNG, and WEBP images are allowed.');
      error.status = 400;
      return cb(error);
    }

    cb(null, true);
  },
}).single('avatar');

export function uploadAvatar(req, res, next) {
  upload(req, res, (error) => {
    if (error) {
      return next(error);
    }

    if (!req.file) {
      return res.badRequest(null, 'Avatar image is required.');
    }

    next();
  });
}
