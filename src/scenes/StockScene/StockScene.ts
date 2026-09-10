
import _ from 'lodash';
import {Scene} from '../Scene';
import { IntradayData, YahooApiClient } from './YahooApiClient';
import { parseBdf, ParsedBdf } from '../../utils/parseBdf';
import { TextDrawer } from '../../utils/TextDrawer';

const { AV_API_KEY } = process.env;

const colors = {
  white: 0xffffff,
  red: 0xff0000,
  green: 0x00ff00,
  lightRed: 0x330000,
  lightGreen: 0x003300,
};

const addTo930 = (minutesSince930) => {
  const totalMinutes = 30 + minutesSince930;
  const totalHours = 9 + Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  return `${totalHours < 10 ? '0' : ''}${totalHours}:${minutes < 10 ? '0' : ''}${minutes}:00`;
};

const timeToIntervalsSince930 = (s) => {
  const [hs, ms] = s.split(':');
  const hours = parseInt(hs);
  const minutes = parseInt(ms);
  return (hours - 9) * 12 + (minutes - 30) / 5;
}

class StockScene extends Scene {
  data?: IntradayData;
  font?: ParsedBdf;

  nextFrame(matrix, dt, t) {
    return;
  }

  async prepare(): Promise<boolean> {
    const yahooApiClient = new YahooApiClient();
    this.data = await yahooApiClient.getIntradayData('PYPL');
    this.font = await parseBdf(`${process.cwd()}/node_modules/rpi-led-matrix/fonts/4x6.bdf`);
    return true;
  }

  start(matrix): void {
    this.started = true;
    matrix.clear();

    // draw text
    const currentPrice = this.data!.price;
    const prevClose = this.data!.previousClose;
    const gain = currentPrice - prevClose;
    const gainPercent = gain / prevClose;
    const format = (s, d = 2) => {
      const [dollars, cents = '00'] = s.toString().split('.');
      let centsString = cents.substring(0, 2);
      return `${dollars}.${centsString}`;
    };
    const sign = gain < 0 ? '-' : '+';
    console.log(`PYPL ${format(currentPrice)} ${sign}$${format(Math.abs(gain))} (${sign}${format(gainPercent * 100)}%)`)

    const drawer = new TextDrawer({
      matrix,
      color: colors.white,
      bdf: this.font!,
    });

    drawer.drawText(`PYPL`, 1, 1);
    drawer.drawText(`$${format(currentPrice)}`, 1, 8);
    drawer.drawText(`${sign}$${format(Math.abs(gain))}`, 33, 1);
    drawer.drawText(`${sign}${format(gainPercent * 100)}%`, 33, 8);

    // 9:30 - 4:00 = 390 minutes
    // 390 minutes / 64 pixels = 6.09375 minutes per pixel
    // iterate and get price for each pixel
    const t0 = this.data!.data[0].timestamp;
    const pixelData: number[] = [];
    for (const { timestamp, price } of this.data!.data) {
      const minutesSince930 = (timestamp - t0) / 60;
      const pixelIndex = Math.floor(minutesSince930 / 6.09375);
      pixelData[pixelIndex] = price;
    }

    // draw the graph
    const high = _.max(pixelData);
    const low = _.min(pixelData);
    console.log('????', pixelData);
    for (let x = 0; x < 64; x++) {
      if (pixelData[x]) {
        console.log('drawing pixel', x, pixelData[x]);
        const v = 31 - Math.ceil((pixelData[x] - low) / (high - low) * 15);
        for (let y = 16; y < 32; y++) {
          if (y === v) {
            if (v < prevClose) {
              matrix.fgColor(colors.green).setPixel(x, y);
            } else {
              matrix.fgColor(colors.red).setPixel(x, y);
            }
          } else if (y >= prevClose && y < v) {
            matrix.fgColor(colors.lightRed).setPixel(x, y);
          } else if (y < prevClose && y > v) {
            matrix.fgColor(colors.lightGreen).setPixel(x, y);
          }
        }
      }
    }
  }

  stop(): void {
    return;
  }
}

export { StockScene };
