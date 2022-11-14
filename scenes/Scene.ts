import { LedMatrix } from "rpi-led-matrix/dist/types";

class Scene {
  started: boolean
  cosntructor() {
    this.started = false;
  }

  nextFrame(matrix, dt, t) {
    console.log('filling....')
    Math.floor(t) % 2 === 0 ? matrix.fgColor(0xf00).fill() : matrix.fgColor(0x00f).fill();
  }

  prepare(): boolean | Promise<boolean> {
    return true;
  }

  start(matrix: LedMatrix): void {
    this.started = true;
    return;
  }

  stop(): void {
    return;
  }
}

export default Scene;
