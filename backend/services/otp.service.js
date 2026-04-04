const OTP_TTL_MS = 5 * 60 * 1000;
const otpStore = new Map();

function generateOtpCode() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

function buildOtpKey(userId, purpose, target = '') {
  return `${userId}:${purpose}:${target}`;
}

const otpService = {
  createOtp(userId, purpose, target = '') {
    const otpCode = generateOtpCode();
    const key = buildOtpKey(userId, purpose, target);
    otpStore.set(key, {
      otpCode,
      expiresAt: Date.now() + OTP_TTL_MS,
    });
    return otpCode;
  },

  verifyOtp(userId, purpose, otpCode, target = '', consume = true) {
    const key = buildOtpKey(userId, purpose, target);
    const storedOtp = otpStore.get(key);

    if (!storedOtp) {
      return false;
    }

    const isExpired = Date.now() > storedOtp.expiresAt;
    const isMatch = storedOtp.otpCode === otpCode;

    if (isExpired || !isMatch) {
      if (isExpired) otpStore.delete(key);
      return false;
    }

    if (consume) {
      otpStore.delete(key);
    }
    return true;
  },
};

export default otpService;
