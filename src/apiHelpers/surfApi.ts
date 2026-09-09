import axios from 'axios';
import Bluebird from 'bluebird';
import _ from 'lodash';

(async () => {
  const endpoints = ['wave', 'rating', 'wind', 'tides'];
  console.log(_.keyBy(endpoints, v => v))
  const [wave, rating, wind, tides] = await Bluebird.map(endpoints, async (endpointString) => {
    const url = `https://services.surfline.com/kbyg/spots/forecasts/${endpointString}?spotId=5a25e409aa1aea001b27be39&days=1&intervalHours=1`;
    const { data } = await axios.get(url);
    return data;
  })

  // console.log({ wave rating,wind,tides})

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

  console.log('>>>>>>', {
    min,
    max,
    ratingKey,
    speedKts,
    directionType,
    tideHeight,
    nextTideHeight,
  });
})();
