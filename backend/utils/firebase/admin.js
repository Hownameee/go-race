import fs from 'fs';
import { createRequire } from 'module';
import config from '../../config/config.js';

const require = createRequire(import.meta.url);

let admin = null;
try {
  admin = require('firebase-admin');
} catch (e) {
  // ignore; FCM will be disabled
  console.warn('FCM disabled: firebase-admin could not be loaded.', e.message);
}

let initialized = false;

export function initFirebaseAdmin() {
  if (initialized) return;

  const serviceAccountPath = config.FIREBASE_SERVICE_ACCOUNT_PATH;

  if (!serviceAccountPath) {
    console.warn(
      '[firebase-admin] FIREBASE_SERVICE_ACCOUNT_PATH not set; FCM disabled',
    );
    return;
  }

  if (!admin) {
    console.warn(
      '[firebase-admin] firebase-admin package not installed; FCM disabled',
    );
    return;
  }

  try {
    const raw = fs.readFileSync(serviceAccountPath, 'utf8');
    const serviceAccount = JSON.parse(raw);

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
    });

    initialized = true;
  } catch (e) {
    console.warn(
      '[firebase-admin] init failed; FCM disabled:',
      e?.message || e,
    );
  }
}

export function getFirebaseAdmin() {
  return initialized ? admin : null;
}
