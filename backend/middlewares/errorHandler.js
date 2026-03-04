import { ZodError } from "zod";

export default function errorHandler(error, req, res, next) {
  console.error(error);
  if (error instanceof ZodError) {
    res.status(500).json({
      message: "Validation failed",
      success: false,
      data: error.issues,
    });
  } else {
    res.status(500).json({
      success: false,
      data: error.issues,
      message: null,
    });
  }
}