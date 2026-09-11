
import _ from 'lodash';
import {Scene} from '../Scene';
import { IntradayData, YahooApiClient } from './YahooApiClient';
import { parseBdf, ParsedBdf } from '../../utils/parseBdf';
import { TextDrawer } from '../../utils/TextDrawer';

const colors = {
  white: 0xffffff,
  red: 0xff0000,
  green: 0x00ff00,
  lightRed: 0x330000,
  lightGreen: 0x003300,
  darkRed: 0x660000,
  darkGreen: 0x006600,
};

const StockScene = (symbol: string) => class StockSceneClass extends Scene {
  data?: IntradayData;
  font?: ParsedBdf;
  
  constructor() {
    super();
  }

  nextFrame(matrix, dt, t) {
    return;
  }

  async prepare(): Promise<boolean> {
    const yahooApiClient = new YahooApiClient();
    this.data = await yahooApiClient.getIntradayData(symbol);
    this.font = await parseBdf(`${process.cwd()}/node_modules/rpi-led-matrix/fonts/5x8.bdf`);
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

    const drawer = new TextDrawer({
      matrix,
      color: colors.white,
      bdf: this.font!,
    });

    drawer.drawText(symbol, 0, 0);
    drawer.drawText(`$${format(currentPrice)}`, 0, 8);
    drawer.setColor(gain < 0 ? colors.darkRed : gain > 0 ? colors.darkGreen : colors.white);
    drawer.drawText(`${sign}$${format(Math.abs(gain))}`, 32, 0);
    drawer.drawText(`${sign}${format(gainPercent * 100)}%`, 32, 8);

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

    for (let x = 0; x < 64; x++) {
      if (pixelData[x]) {
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
