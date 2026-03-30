import mailService from '../services/mail.service.js';

const mailController = {
  sendEmail: async function (req, res) {
    try {
      const { email, subject, content } = req.body;
      await mailService.sendEmail(email, subject, content);
      res.ok(content);
    } catch (error) {
      res.error(null, error.message);
    }
  },
};

export default mailController;
