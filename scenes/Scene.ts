
class Scene {
  nextFrame(matrix, dt, t) {
    Math.floor(t) % 2 === 0 ? matrix.fill(0xf00) : matrix.fill(0x00f);
  }

  prepare(): boolean | Promise<boolean> {
    return true;
  }

  start(): void {
    return;
  }

  stop(): void {
    return;
  }
}

export default Scene;
