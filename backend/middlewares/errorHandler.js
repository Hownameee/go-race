import { ZodError } from 'zod';

export default function errorHandler(error, req, res, next) {
  console.error(error);

  if (error instanceof ZodError) {
    const firstIssueMessage = error.issues?.[0]?.message || 'Validation failed';
    return res.badRequest(error.issues, firstIssueMessage);
  }

  if (error?.code === 'SQLITE_CONSTRAINT_CHECK') {
    return res.badRequest(null, 'Invalid data');
  }

  if (error?.name === 'MulterError') {
    if (error.code === 'LIMIT_FILE_SIZE') {
      return res.badRequest(null, 'Avatar image must be 5MB or smaller.');
    }

    return res.badRequest(null, error.message);
  }

  if (error?.type === 'entity.too.large') {
    return res.badRequest(null, 'Request entity too large. Maximum allowed size is 50MB.');
  }

  if (error?.status === 400) {
    return res.badRequest(null, error.message);
  }

  return res.error(null, error?.message || 'Internal Server Error');
}
