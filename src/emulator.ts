import http from 'http';
import path from 'path';

import express from 'express';
import { Server } from 'socket.io';
import open from 'open';

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

type RGB = [number, number, number];

// Generate your 2D array data here
function generateMatrixData() {
    let matrix: RGB[][] = [];
    const time = Date.now() * 0.002;
    
    for (let y = 0; y < HEIGHT; y++) {
        let row: RGB[] = [];
        for (let x = 0; x < WIDTH; x++) {
            // Example animation math: moving sine wave color pattern
            let r = Math.floor(Math.sin(x * 0.1 + time) * 127 + 128);
            let g = Math.floor(Math.cos(y * 0.1 + time) * 127 + 128);
            let b = Math.floor(Math.sin((x + y) * 0.1 + time) * 127 + 128);
            
            row.push([r, g, b]); // Each pixel is an [R, G, B] array
        }
        matrix.push(row);
    }
    return matrix;
}

// Stream data at ~30 frames per second
setInterval(() => {
    const frameData = generateMatrixData();
    io.emit('frame', frameData);
}, 33);

server.listen(3000, () => {
    console.log('Dummy LED Matrix server running at http://localhost:3000');
    open('http://localhost:3000');
});
