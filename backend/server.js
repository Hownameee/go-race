import http from 'http';
import app from './app.js';
import { initFirebaseAdmin } from './utils/firebase/admin.js';

const PORT = 5000;

// Tạo server HTTP
const server = http.createServer(app);

// Init Firebase Admin (optional; only if env configured)
initFirebaseAdmin();

// Bắt đầu server
server.listen(PORT, () => {
  console.log(`Backend server listening on port ${PORT}`);
});
