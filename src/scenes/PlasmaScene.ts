import {Scene} from "./Scene";

type Color = { r: number, g: number, b: number };

const getPlasmaPixel = (x: number, y: number, timeMs: number): Color => {
    const time = timeMs / 1000;

    // 1. Layer multiple moving sine/cosine waves to create complex interference patterns
    const v1 = Math.sin(x * 0.1 + time);
    const v2 = Math.cos(y * 0.12 - time * 0.8);
    const v3 = Math.sin((x + y) * 0.08 + time * 1.2);

    // Circular wave pattern
    const cx = x - 32; // Center matrix X
    const cy = y - 16; // Center matrix Y
    const v4 = Math.sin(Math.sqrt(cx * cx + cy * cy) * 0.15 - time);

    // 2. Combine all wave values together
    const total = v1 + v2 + v3 + v4;

    // 3. Map the total spectrum (-4.0 to +4.0 max) down to standard 0-255 RGB values
    const r = Math.floor(Math.sin(total * Math.PI) * 127 + 128);
    const g = Math.floor(Math.cos(total * Math.PI + (2 * Math.PI) / 3) * 127 + 128);
    const b = Math.floor(Math.sin(total * Math.PI + (4 * Math.PI) / 3) * 127 + 128);

    return { r, g, b };
}

class PlasmaScene extends Scene {
  nextFrame(matrix, dt, t) {
    for (let x = 0; x < matrix.width(); x++) {
      for (let y = 0; y < matrix.height(); y++) {
        const color = getPlasmaPixel(x, y, t);
        matrix.fgColor(color).setPixel(x, y);
      }
    }
  }
}

export { PlasmaScene };
