import { LedMatrixInstance } from "rpi-led-matrix";
import Scene from './scene';

class Manager {
  constructor({ matrix, scene }: { matrix: LedMatrixInstance, scene: Scene }) {
    matrix.afterSync((matrixx, dt, t) => {
      scene.nextFrame(matrixx, dt, t);

      setTimeout(() => matrixx.sync(), 0);
    });
  }
}

export default Manager;
