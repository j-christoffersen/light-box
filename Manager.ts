import { LedMatrixInstance } from "rpi-led-matrix";
import GameOfLifeScene from "./scenes/GameOfLifeScene";
import Scene from './scenes/Scene';

console.log('confused...');

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

    matrix.afterSync((matrixx, dt, t) => {
      console.log('> after sync')
      if (scene) {
        console.log('>> nextframe')
        scene.nextFrame(matrixx, dt, t);
      }
      setTimeout(() => matrixx.sync(), 0);
    });

    console.log('< first sync call');
    
    matrix
    .clear() // clear the display
    .brightness(100) // set the panel brightness to 100%
    .fgColor(0x0000ff) // set the active color to blue
    .fill() // color the entire diplay blue
    .fgColor(0xffff00) // set the active color to yellow
    // draw a yellow circle around the display
    .drawCircle(matrix.width() / 2, matrix.height() / 2, matrix.width() / 2 - 1)
    // draw a yellow rectangle
    .drawRect(
      matrix.width() / 4,
      matrix.height() / 4,
      matrix.width() / 2,
      matrix.height() / 2,
    )
    // sets the active color to red
    .fgColor({ r: 255, g: 0, b: 0 })
    // draw two diagonal red lines connecting the corners
    .drawLine(0, 0, matrix.width(), matrix.height())
    .drawLine(matrix.width() - 1, 0, 0, matrix.height() - 1)
    .sync();
  }
}

export default Manager;
