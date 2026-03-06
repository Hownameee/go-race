export default function validation(schema, source = 'body') {
  return function (req, res, next) {
    try {
      schema.parse(req[source]);
    } catch (err) {
      return next(err);
    }
    next();
  };
}
