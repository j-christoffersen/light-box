import { LedMatrix } from 'rpi-led-matrix';
import Manager from './Manager';

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

new Manager({ matrix });
