
import _ from 'lodash';
import axios from 'axios';
import Scene from './Scene';
import { Font } from 'rpi-led-matrix';

const { AV_API_KEY } = process.env;

const colors = {
  white: 0xffffff,
  red: 0xff0000,
  green: 0x00ff00,
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
  data: any;

  nextFrame(matrix, dt, t) {
    return;
  }

  async prepare(): Promise<boolean> {
    const url = `https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=PYPL&interval=5min&apikey=${AV_API_KEY}`;
    const { data } = await axios.get(url);
    console.log('>>>>', data);
    this.data = data;
    return true;
  }

  start(matrix): void {
    const interval = '5min';
    const { ['Meta Data']: { ['3. Last Refreshed']: lastRefreshed } } = this.data;
    const timeSeries = this.data[`Time Series (${interval})`];
    const currentPrice = timeSeries[lastRefreshed]['4. close'];
    const font = new Font('helvR12', `${process.cwd()}/node_modules/rpi-led-matrix/fonts/5x8.bdf`);
    matrix.font(font).fgColor(colors.white);
    matrix.drawText(`PYPL ${currentPrice.toLocaleString('en-US', { minimumFractionDigits: 2 })}`, 1, 1);
    // matrix.drawText(`$${currentPrice}`, 1, 8);
    console.log('DBG:', currentPrice, !!timeSeries);

    // fill in gaps in time series data
    const date = lastRefreshed.substring(0, 10);
    const fullTimeSeries = [] as any[];
    let mostRecent = Object.values(timeSeries)[Object.values(timeSeries).length]; // start with first TODO closest to open
    const intervalsSince930 = Math.min(timeToIntervalsSince930(lastRefreshed.substring(11)), timeToIntervalsSince930('16:00'));
    for (let i = 0; i < intervalsSince930; i++) { // TODO last refreshed
      const hi = timeSeries[`${date} ${addTo930(i * 5)}`];
      if (hi) {
        mostRecent = hi;
      }
      fullTimeSeries.push(mostRecent);
    }
    const everything = fullTimeSeries.map(x => x['4. close']);
    console.log('>>>>>', everything);
    const open = everything[0];
    const high = _.max(everything);
    const low = _.min(everything);

    const p_low = 31;
    const p_high = 10;
    const getPValue = (v) => Math.round(p_low + (p_high - p_low) / (high - low) * (v - low));
    const p_open = getPValue(open);

    console.log('PPPP', { p_high, p_low, p_open, high, low, open });
    
    const getThingIndex = (x) => {
        return Math.round(x * 64 / intervalsSince930);
      }

    // draw the thingy
    for (let x = 0; x < 64; x++) {
      const v = getPValue(everything[getThingIndex(x)]);
      console.log('vvvv',getThingIndex(x), everything[getThingIndex(x)], v);
      for (let y = 16; y < 32; y++) {
        if (y === p_open) {
          matrix.fgColor(colors.white).setPixel(x, y);
        } else if (y === v) {
          if (v > open) {
            matrix.fgColor(colors.green).setPixel(x, y);
          } else {
            matrix.fgColor(colors.red).setPixel(x, y);
          }
        } // TODO light colors
      }
    }
  }

  stop(): void {
    return;
  }
}

export default StockScene;