import { LedMatrixInstance } from "rpi-led-matrix";
import GameOfLifeScene from "./scenes/GameOfLifeScene";
import Scene from './scenes/Scene';

class Manager {
  constructor({ matrix }: { matrix: LedMatrixInstance }) {
    const scenes = [Scene, GameOfLifeScene];
    let i = 0;
    let scene;
    const nextScene = () => {
      const SceneClass = scenes[i];
      i = (i + 1) % scenes.length;
      scene = new SceneClass();
    };
    nextScene();
    setInterval(nextScene, 20000);

    matrix.afterSync((matrixx, dt, t) => {
      scene.nextFrame(matrixx, dt, t);
      setTimeout(() => matrixx.sync(), 0);
    });
  }
}

export default Manager;
