import { LedMatrix } from 'rpi-led-matrix';
import Manager from './Manager';

console.log('what');
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
console.log('the heck');

const manager = new Manager({ matrix });
console.log('huh');
