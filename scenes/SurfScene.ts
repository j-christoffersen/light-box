import axios from 'axios';
import Bluebird from 'bluebird';
import _ from 'lodash';
import { Font } from 'rpi-led-matrix';
import Scene from "./Scene";

const format = (s, d = 2) => {
  const [dollars, cents = '00'] = s.toString().split('.');
  let centsString = cents.substring(0, d);
  return `${dollars}.${centsString}`;
};


class SurfScene extends Scene {
  data: any

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

    
  }
}

export default SurfScene;
