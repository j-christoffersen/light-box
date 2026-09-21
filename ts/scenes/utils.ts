import { readFile } from 'node:fs/promises';
import bmpJs from 'bmp-js';
import Bluebird from 'bluebird';

export const loadBmp = async (filePath) => {
  const fileBuffer = await readFile(filePath);
  return bmpJs.decode(fileBuffer);
}

export const readFiles = (pathsByKey) => {
  return Bluebird.props(pathsByKey, (path) => loadBmp(path));
}

export const drawBmp = (matrix, x0, y0, {width, height, data}) => {
  if (width * height !== data.length / 4) {
    throw new Error('darn');
  }
  for (let i = 0; i < data.length; i += 4) {
    const blue = data[i+1];
    const green = data[i+2];
    const red = data[i+3];
    const x = x0 + i % width
    const y = y0 + Math.floor(i / width);

    matrix.fgColor(red * 0x010000 + green * 0x000100 + blue * 0x000001).setPixel(x, y);
  }
};