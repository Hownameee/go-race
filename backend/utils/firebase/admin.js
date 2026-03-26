import fs from 'fs';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);

let admin = null;
try {
  // Only needed when Firebase Admin is installed/configured.
  // If not installed yet, we keep the backend running without FCM.
  // eslint-disable-next-line import/no-extraneous-dependencies
  admin = require('firebase-admin');
} catch (e) {
  // ignore; FCM will be disabled
}

let initialized = false;

export function initFirebaseAdmin() {
  if (initialized) return;

  // Provide service account json path via env:
  // FIREBASE_SERVICE_ACCOUNT_PATH=/absolute/path/serviceAccount.json
  const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT_PATH;
  if (!serviceAccountPath) {
    console.warn('[firebase-admin] FIREBASE_SERVICE_ACCOUNT_PATH not set; FCM disabled');
    return;
  }

  if (!admin) {
    console.warn('[firebase-admin] firebase-admin package not installed; FCM disabled');
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
    console.warn('[firebase-admin] init failed; FCM disabled:', e?.message || e);
  }
}

export function getFirebaseAdmin() {
  return initialized ? admin : null;
}
