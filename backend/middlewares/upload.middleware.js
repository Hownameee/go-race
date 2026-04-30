import path from 'path';
import multer from 'multer';

const allowedMimeTypes = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/heic',
  'image/heif',
]);

const upload = multer({
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 5 * 1024 * 1024,
    files: 1,
  },
  fileFilter: (req, file, cb) => {
    if (!allowedMimeTypes.has(file.mimetype)) {
      const error = new Error('Only JPG, PNG, WEBP, HEIC, and HEIF images are allowed.');
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

const postUpload = multer({
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 5 * 1024 * 1024, // 5MB
    files: 10, // max 10 photos
  },
  fileFilter: (req, file, cb) => {
    if (!allowedMimeTypes.has(file.mimetype)) {
      const error = new Error('Only JPG, PNG, WEBP, HEIC, and HEIF images are allowed.');
      error.status = 400;
      return cb(error);
    }
    cb(null, true);
  },
}).array('photos', 10);

export function uploadPhotos(req, res, next) {
  postUpload(req, res, (error) => {
    if (error) {
      if (error.code === 'LIMIT_FILE_COUNT') {
        error.message = 'You can upload up to 10 photos per post.';
        error.status = 400;
      }
      return next(error);
    }
    next();
  });
}
