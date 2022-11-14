import Scene from './Scene';

class GameOfLifeScene extends Scene {
  vals: boolean[];
  fgColor: number;

  constructor() {
    super();
    this.vals = (new Array(32 * 64)).map(() => Math.random() > 0.4 ? true : false);
    this.fgColor = 0x9955ff;
  }

  valueAt(x, y) {
    return this.vals[64 * (y % 32) + (x % 64)];
  }

  nextFrame(matrix: any, dt: any, t: any): void {
    let newVals = (new Array(32 * 64)).fill(false);
    for (let x = 0; x < 64; x++) {
      for (let y = 0; y < 32; y++) {
        let neighbors = 0;
        for (let xx of [-1, 0, 1]) {
          for (let yy of [-1, 0, 1]) {
            if (xx === 0 && yy === 0) {
              continue;
            }

            neighbors += this.valueAt(x+xx, y+yy) ? 1 : 0;
          }
        }
        if ((this.valueAt(x, y) && neighbors === 2) || neighbors === 3) {
          newVals[64 * y + x] = true;
        }
      }
    }
    this.vals = newVals;
    console.log('??', newVals, newVals.some(x => x));

    for (let x = 0; x < 64; x++) {
      for (let y = 0; y < 32; y++) {
        matrix.fgColor(this.valueAt(x, y) ? this.fgColor : 0x000).setPixel(x, y);
      }
    }
  }
}

export default GameOfLifeScene;
