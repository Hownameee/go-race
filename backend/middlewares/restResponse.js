export default function restResponse(req, res, next) {
  res.unauthorized = () => {
    res.status(401).json({
      success: false,
      data: null,
      message: "Unathorized",
    });
  };

  res.notFound = () => {
    res.status(404).json({
      success: false,
      data: null,
      message: "Not found",
    });
  };

  res.created = (data = null, message = "Created") => {
    res.status(201).json({
      success: true,
      data: data,
      message: message,
    });
  };

  res.ok = (data, message = null) => {
    res.status(200).json({
      success: true,
      data: data,
      message: message,
    });
  };

  res.violate = (data = null, message = null) => {
    res.status(409).json({
      success: false,
      data: data,
      message: message,
    });
  };

  res.error = (data = null, message = null) => {
    res.status(500).json({
      success: false,
      data: data,
      message: message,
    });
  };

  next();
}