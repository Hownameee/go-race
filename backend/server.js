import http from 'http';
import app from './app.js';
import { Server } from 'socket.io';
import { initFirebaseAdmin } from './utils/firebase/admin.js';

const PORT = 5000;

// Tạo server HTTP
const server = http.createServer(app);

// Init Firebase Admin (optional; only if env configured)
initFirebaseAdmin();

// Init Socket.IO
const io = new Server(server, {
  cors: { origin: '*', methods: ['GET', 'POST'] },
});

function setupSocketHandlers(io) {
  io.on('connection', (socket) => {

    socket.on('join', (data) => {
        const userId = data.userId; 
        console.log('Client connected, userId =', userId);

        // Gắn socket vào room
        socket.join(`user_${userId}`);
    });


    socket.on('message', (data) => handleMessage(socket, data));
    socket.on('disconnect', () => handleDisconnect(socket));

  });
}

function handleMessage(socket, data) {
  console.log('Message from client:', data);
  socket.emit('message', `Server nhận: ${data}`);
}

function handleDisconnect(socket) {
  console.log('Client disconnected:', socket.id);
}

// Thiết lập socket
setupSocketHandlers(io);

// Lưu io vào app
app.set('io', io);

// Bắt đầu server
server.listen(PORT, () => {
  console.log(`Backend server listening on port ${PORT}`);
});