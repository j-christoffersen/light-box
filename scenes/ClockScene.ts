import { Font } from 'rpi-led-matrix';
import Scene from "./Scene";

class ClockScene extends Scene {
  start(matrix) {
    const timeString = (new Date()).toTimeString();
    const [hoursString, minutesString] = timeString.split(':');
    const hoursInt = parseInt(hoursString);
    const minutesInt = parseInt(minutesString);
    const minutesRounded = Math.round(minutesInt / 5) * 5;
    const hours = (hoursInt + (minutesRounded >= 60 ? 1 : 0)) % 24;
    const minutes = minutesRounded % 60;

    const hoursWords = {
      0: 'MIDNIGHT',
      1: 'ONE',
      2: 'TWO',
      3: 'THREE',
      4: 'FOUR',
      5: 'FIVE',
      6: 'SIX',
      7: 'SEVEN',
      8: 'EIGHT',
      9: 'NINE',
      10: 'TEN',
      11: 'ELEVEN',
      12: 'NOON',
      13: 'ONE',
      14: 'TWO',
      15: 'THREE',
      16: 'FOUR',
      17: 'FIVE',
      18: 'SIX',
      19: 'SEVEN',
      20: 'EIGHT',
      21: 'NINE',
      22: 'TEN',
      23: 'ELEVEN',
      24: 'MIDNIGHT',
    };

    const minutesWords = {
      5: 'FIVE PAST',
      10: 'TEN PAST',
      15: 'QUARTER PAST',
      20: 'TWENTY PAST',
      25: 'TWENTY-FIVE PAST',
      30: 'HALF PAST',
      35: 'TWENTY-FIVE TIL',
      40: 'TWENTY TIL',
      45: 'QUARTER TIL',
      50: 'TEN TIL',
      55: 'FIVE TIL',
    }

    let one = '';
    let two = hoursWords[hours];
    let three = '';
    
    if (minutes) {
      [one, two] = minutesWords[minutes].split(' ');
      if (minutes <= 30) {
        three = hoursWords[hours]
      } else {
        three = hoursWords[hours + 1];
      }
    }

    console.log([one, two, three].join(' '));

    const font = new Font('4x6', `${process.cwd()}/node_modules/rpi-led-matrix/fonts/4x6.bdf`);
    matrix.font(font).fgColor(0xffffff);
    matrix.drawText(one, 5, 5);
    matrix.drawText(two, 10, 14);
    matrix.drawText(three, 15, 23);
  }
}
