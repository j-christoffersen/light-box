import { LedMatrix, LedMatrixInstance } from "rpi-led-matrix/dist/types";

class Scene {
  started: boolean
  constructor() {
    this.started = false;
  }

  nextFrame(matrix, dt, t) {
    return;
  }

  prepare(): boolean | Promise<boolean> {
    return true;
  }

  start(matrix: LedMatrixInstance): void {
    this.started = true;
    return;
  }

  stop(): void {
    return;
  }
}

export default Scene;
