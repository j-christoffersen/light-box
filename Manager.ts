import { LedMatrixInstance } from "rpi-led-matrix";
import GameOfLifeScene from "./scenes/GameOfLifeScene";
import Scene from './scenes/Scene';
import StockScene from "./scenes/StockScene";

console.log('confused...');

class Manager {
  constructor({ matrix }: { matrix: LedMatrixInstance }) {
    const scenes = [StockScene, GameOfLifeScene, Scene];
    let i = 0;
    let scene: Scene;
    const nextScene = async () => {
      const SceneClass = scenes[i];
      const newScene = new SceneClass();
      await newScene.prepare();
      i = (i + 1) % scenes.length;
      scene = newScene;
    };
    nextScene();
    setInterval(nextScene, 20000);

    matrix.afterSync((matrixx, dt, t) => {
      if (scene) {
        if (!scene.started) {
          scene.start(matrixx);
        }
        scene.nextFrame(matrixx, dt, t);
      }
      setTimeout(() => matrixx.sync(), 0);
    });

    console.log('< first sync call');
    
    matrix.sync();
  }
}

export default Manager;
