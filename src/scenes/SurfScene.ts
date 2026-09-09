import axios from 'axios';
import Bluebird from 'bluebird';
import _ from 'lodash';
import { Font } from 'rpi-led-matrix';
import Scene from "./Scene";
import { drawBmp, readFiles } from './utils';

const format = (s, d = 2) => {
  const [dollars, cents = '00'] = s.toString().split('.');
  let centsString = cents.substring(0, d);
  return `${dollars}.${centsString}`;
};


class SurfScene extends Scene {
  data: any
  bmps: any
  frame: number
  ratingKey: string

  constructor() {
    super();
    this.frame = 0;
    this.ratingKey = '';
  }

  async prepare() {
    const endpoints = ['wave', 'rating', 'wind', 'tides'];
    const [wave, rating, wind, tides] = await Bluebird.map(endpoints, async (endpointString) => {
      const url = `https://services.surfline.com/kbyg/spots/forecasts/${endpointString}?spotId=5a25e409aa1aea001b27be39&days=1&intervalHours=1`;
      const { data } = await axios.get(url);
      return data;
    })

    const {
      data: {
        wave: [{
          surf: { min, max },
        }],
      },
    } = wave;
    const {
      data: {
        rating: [{
          rating: { key: ratingKey, },
        }]
      }
    } = rating;
    const {
      data: {
        wind: [{
          speed: speedKts, directionType,
        }]
      }
    } = wind;
    const  {
      data: {
        tides: [{
          height: tideHeight,
        }, { height: nextTideHeight }]
      }
    } = tides;

    this.data = {
      min,
      max,
      ratingKey,
      speedKts,
      directionType,
      tideHeight,
      nextTideHeight,
    };

    return true;
  }

  start(matrix) {
    this.started = true;

    const {
      min,
      max,
      ratingKey,
      speedKts,
      directionType,
      tideHeight,
      nextTideHeight,
    } = this.data;

    const speedMph = speedKts * 1.15078
    const tideDirection = nextTideHeight - tideHeight > 0 ? 'UP' : 'DOWN';
  
    const font = new Font('4x6', `${process.cwd()}/node_modules/rpi-led-matrix/fonts/4x6.bdf`);
    matrix.font(font).fgColor(0xffffff);
    matrix.drawText(`${min}-${max} FT`, 32, 1); // Bigger font
    matrix.drawText(`${Math.round(speedMph)} MPH ${directionType}`, 32, 8);
    matrix.drawText(`${format(tideHeight, 1)} FT ${tideDirection}`, 32, 15);

    this.bmps = readFiles({
      wave: `${process.cwd()}/assets/wave.bmp`,
      '1_poor': `${process.cwd()}/assets/1_poor.bmp`,
      '2_0_poor': `${process.cwd()}/assets/2_0_poor.bmp`,
      '2_1_fair': `${process.cwd()}/assets/2_1_fair.bmp`,
      '3_fair': `${process.cwd()}/assets/3_fair.bmp`,
      '4_0_fair': `${process.cwd()}/assets/4_0_fair.bmp`,
      '4_1_good': `${process.cwd()}/assets/4_1_good.bmp`,
      '5_good': `${process.cwd()}/assets/5_good.bmp`,
      // TODO fallback
    });

    drawBmp(matrix, 0, 0, this.bmps.wave);

    const ratingInitialBmps = {
      'POOR': '1_poor',
      'POOR_FAIR': '2_0_poor',
      'FAIR': '3_fair',
      'FAIR_GOOD': '4_0_fair',
      'GOOD': '5_poor',
    };

    drawBmp(matrix, 32, 16, this.bmps[ratingInitialBmps[ratingKey]]);
    this.frame= 0;
    this.ratingKey = ratingKey;
  }

  nextFrame(matrix, dt, t) {
    if (['POOR_FAIR', 'FAIR_GOOD'].includes(this.ratingKey)) {
      if (t % 2000 >= 1000 && this.frame === 0) {
        // frame should be 1
      } else if (t % 2000 < 1000 && this.frame === 1) {
        // frame should be 0
      }
    }
  }
}

export default SurfScene;
