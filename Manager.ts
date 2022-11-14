import { LedMatrixInstance } from "rpi-led-matrix";
import GameOfLifeScene from "./scenes/GameOfLifeScene";
import Scene from './scenes/Scene';

console.log('confused...');

class Manager {
  constructor({ matrix }: { matrix: LedMatrixInstance }) {
    const scenes = [GameOfLifeScene, Scene];
    let i = 0;
    let scene: Scene;
    const nextScene = () => {
      const SceneClass = scenes[i];
      i = (i + 1) % scenes.length;
      scene = new SceneClass();
    };
    nextScene();
    setInterval(nextScene, 20000);

    matrix.afterSync((matrixx, dt, t) => {
      if (scene) {
        scene.nextFrame(matrixx, dt, t);
      }
      setTimeout(() => matrixx.sync(), 0);
    });

    console.log('< first sync call');
    
    matrix.sync();
  }
}

export default Manager;
