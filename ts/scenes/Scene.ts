import { LedMatrixInstance } from "rpi-led-matrix";

class Scene {
  started: boolean
  constructor() {
    this.started = false;
  }

  nextFrame(matrix: LedMatrixInstance, dt: number, t: number): void {
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

export { Scene };
