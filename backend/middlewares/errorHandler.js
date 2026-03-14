import { ZodError } from "zod";

export default function errorHandler(error, req, res, next) {
  console.log(error.message);
  if (error instanceof ZodError) {
    return res.badRequest(error.issues, "Validation failed");
  }
  
  return res.error(error.issues, error.message || "Internal Server Error");
}