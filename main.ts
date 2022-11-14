import { LedMatrix } from 'rpi-led-matrix';
import Manager from './Manager';

console.log('what');
const matrix = new LedMatrix(
  LedMatrix.defaultMatrixOptions(), // TODO
  LedMatrix.defaultRuntimeOptions() // TODO
);
console.log('the heck');

const manager = new Manager({ matrix });

