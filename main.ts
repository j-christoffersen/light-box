import { LedMatrix } from 'rpi-led-matrix';
import Manager from './manager';

const matrix = new LedMatrix(
  LedMatrix.defaultMatrixOptions(), // TODO
  LedMatrix.defaultRuntimeOptions() // TODO
);

const manager = new Manager({ matrix });

