import {Scene} from "./Scene";

type Color = { r: number, g: number, b: number };

class PlasmaScene extends Scene {
    private phase: Array<number>
    private a: Array<number>
    private b: Array<number>

    constructor() {
        super();

        // Initialize scene with random coefficients and phase values
        this.phase = new Array(3).fill(0).map(() => Math.random() * 2 * Math.PI);
        this.a = new Array(4).fill(0).map(() => 0.05 + Math.random() * 0.1);
        this.b = new Array(4).fill(0).map(() => 0.05 + Math.random());
    }

    private getPlasmaPixel(x: number, y: number, timeMs: number): Color {
        const time = timeMs / 1000;
    
        // 1. Layer multiple moving sine/cosine waves to create complex interference patterns
        const v1 = Math.sin(x * this.a[0] + time * this.b[0]);
        const v2 = Math.cos(y * this.a[1] - time * this.b[1]);
        const v3 = Math.sin((x + y) * this.a[2] + time * this.b[2]);
    
        // Circular wave pattern
        const cx = x - 32; // Center matrix X
        const cy = y - 16; // Center matrix Y
        const v4 = Math.sin(Math.sqrt(cx * cx + cy * cy) * this.a[3] - time * this.b[3]);
    
        // 2. Combine all wave values together
        const total = v1 + v2 + v3 + v4;
    
        // 3. Map the total spectrum (-4.0 to +4.0 max) down to standard 0-255 RGB values
        const r = Math.floor(Math.sin(total * Math.PI + this.phase[0]) * 127 + 128);
        const g = Math.floor(Math.cos(total * Math.PI + this.phase[1]) * 127 + 128);
        const b = Math.floor(Math.sin(total * Math.PI + this.phase[2]) * 127 + 128);
    
        return { r, g, b };
    }

    nextFrame(matrix, dt, t) {
        for (let x = 0; x < matrix.width(); x++) {
        for (let y = 0; y < matrix.height(); y++) {
            const color = this.getPlasmaPixel(x, y, t);
            matrix.fgColor(color).setPixel(x, y);
        }
        }
    }
}

export { PlasmaScene };
