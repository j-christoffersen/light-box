import http from 'http';
import path from 'path';

import express from 'express';
import { Server } from 'socket.io';
import open from 'open';
import { LedMatrix } from 'rpi-led-matrix';

import { DummyMatrix } from './DummyMatrix';
import SceneManager from './SceneManager';

const app = express();
// Serve the UI static file
app.get('/', (req, res) => {
    res.sendFile(path.join(process.cwd(), "public/index.html"));
});

const server = http.createServer(app);
const io = new Server(server);

// Dimensions
const WIDTH = 64;
const HEIGHT = 32;

const emitFrameData = (frameData: number[][]) => {
    io.emit('frame', frameData);
}

const matrix = new DummyMatrix({ cols: WIDTH, rows: HEIGHT}, emitFrameData);

const manager = new SceneManager({ matrix });
manager.start();

server.listen(3000, () => {
    console.log('Dummy LED Matrix server running at http://localhost:3000');
    open('http://localhost:3000');
});
