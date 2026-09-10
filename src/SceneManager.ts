import { LedMatrixInstance } from "rpi-led-matrix";
import {GameOfLifeScene} from "./scenes/GameOfLifeScene";
import {Scene} from './scenes/Scene';
import {StockScene} from "./scenes/StockScene/StockScene";
import {ClockScene} from "./scenes/ClockScene";
import { PlasmaScene } from "./scenes/PlasmaScene";

const SCENE_LENGTH_MS = 20000;
const scenes = [
  PlasmaScene,
  GameOfLifeScene, 
  StockScene, 
  ClockScene,
];

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
  }

  start() {
    this.matrix.sync();
  }
}

export default SceneManager;
