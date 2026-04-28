export default function validation(schema, source = 'body') {
  return function (req, res, next) {
    try {
      const rawData =
        source === 'body' && req[source] === undefined ? {} : req[source];
      const parsedData = schema.parse(rawData);
      const currentData = req[source];

      if (
        currentData &&
        typeof currentData === 'object' &&
        parsedData &&
        typeof parsedData === 'object'
      ) {
        Object.assign(currentData, parsedData);
      } else if (source === 'body') {
        req.body = parsedData;
      } else {
        req.validated = req.validated || {};
        req.validated[source] = parsedData;
      }
    } catch (err) {
      return next(err);
    }
    next();
  };
}
