import { ZodError } from 'zod';

export default function errorHandler(error, req, res) {
  console.error(error);
  if (error instanceof ZodError) {
    res.error(error.issues, 'Validation failed');
  } else if (error.code === 'SQLITE_CONSTRAINT_CHECK') {
    res.error(null, 'Invalid data');
  } else {
    res.error(error.issues);
  }
}
