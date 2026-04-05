import fs from 'fs';
import path from 'path';
import multer from 'multer';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const avatarUploadDir = path.resolve(__dirname, '../uploads/avatars');

fs.mkdirSync(avatarUploadDir, { recursive: true });

const allowedMimeTypes = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
]);

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, avatarUploadDir);
  },
  filename: (req, file, cb) => {
    const extension = path.extname(file.originalname) || '.jpg';
    const userId = req.user?.userId ?? 'unknown';
    cb(null, `avatar-${userId}-${Date.now()}${extension}`);
  },
});

const upload = multer({
  storage,
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
