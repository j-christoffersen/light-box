import { LedMatrixInstance } from "rpi-led-matrix";
import GameOfLifeScene from "./scenes/GameOfLifeScene";
import Scene from './scenes/Scene';
import StockScene from "./scenes/StockScene";

const SCENE_LENGTH_MS = 20000;
const scenes = [StockScene, GameOfLifeScene, Scene];

/**
 * Manages various scenes and syncs with the matrix UI.
 */

class SceneManager {
  private matrix: LedMatrixInstance;

  constructor({ matrix }: { matrix: LedMatrixInstance }) {
    this.matrix = matrix;

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
    setInterval(nextScene, SCENE_LENGTH_MS);

    matrix.afterSync((updatedMatrix, dt, t) => {
      if (scene) {
        if (!scene.started) {
          scene.start(updatedMatrix);
        }
        scene.nextFrame(updatedMatrix, dt, t);
      }
      setTimeout(() => updatedMatrix.sync(), 0);
    });

    console.log('< first sync call');
    
  }

  start() {
    this.matrix.sync();
  }
}

export default SceneManager;
