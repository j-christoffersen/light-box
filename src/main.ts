import { LedMatrix } from 'rpi-led-matrix';
import SceneManager from './SceneManager';

/**
 * Main entrypoint for running the application on the raspberry pi.
 */

const matrix = new LedMatrix(
  {
    ...LedMatrix.defaultMatrixOptions(),
    cols: 64,
  },
  {
    ...LedMatrix.defaultRuntimeOptions(),
    gpioSlowdown: 2,
  },
);

const manager = new SceneManager({ matrix });
manager.start();
