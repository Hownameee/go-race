import transporter from '../config/mail.config.js';

const mailService = {
  sendEmail: async function (email, subject, content) {
    try {
      const info = await transporter.sendMail({
        from: process.env.MAIL_USER,
        to: email,
        subject: subject,
        html: content,
      });
      return info;
    } catch (error) {
      console.error('Mail send error:', error);
      throw new Error('Unable to send email. Please try again later.');
    }
  },
};

export default mailService;
