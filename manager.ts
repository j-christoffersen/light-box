import { LedMatrixInstance } from "rpi-led-matrix";
import GameOfLifeScene from "./scenes/GameOfLifeScene";
import Scene from './scenes/Scene';

class Manager {
  constructor({ matrix }: { matrix: LedMatrixInstance }) {
    console.log('hmmmmm');
    const scenes = [Scene, GameOfLifeScene];
    let i = 0;
    let scene: Scene;
    const nextScene = () => {
      const SceneClass = scenes[i];
      i = (i + 1) % scenes.length;
      scene = new SceneClass();
    };
    nextScene();
    setInterval(nextScene, 20000);

    console.log('< first sync call');
    matrix.sync();
    matrix.afterSync((matrixx, dt, t) => {
      console.log('> after sync')
      if (scene) {
        console.log('>> nextframe')
        scene.nextFrame(matrixx, dt, t);
      }
      setTimeout(() => matrixx.sync(), 0);
    });
  }
}

export default Manager;
