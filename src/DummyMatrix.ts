// @ts-nocheck
import { LedMatrixInstance, MatrixOptions } from "rpi-led-matrix";

/**
 * LED matrix emulation that renders in terminal. For local testing.
 */


class DummyMatrix implements LedMatrixInstance {
  private matrix: any;

  constructor({
    cols,
    rows,
  }: MatrixOptions) {
    this.matrix = new Array(cols).fill(0).map(() => new Array(rows).fill(0));
  }

  afterSync(callback: (matrix: any) => void) {
    callback(this.matrix);
  }

  bgColor(color: number) {
    return this;
  }

  brightness(brightness: number) {
    return this;
  }

  clear() {
    return this;
  }

  fgColor(color: number) {
    return this;
  }

  fill() {
    return this;
  }

  drawCircle(x: number, y: number, radius: number) {
    return this;
  }

  drawRect(x: number, y: number, width: number, height: number) {
    return this;
  }

  drawLine(x1: number, y1: number, x2: number, y2: number) {
    return this;
  }

  drawText(x: number, y: number, text: string) {
    return this;
  }
}

export { DummyMatrix };
