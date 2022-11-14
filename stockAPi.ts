import axios from 'axios';

const main = async () => {
  const url = `https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=PYPL&interval=5min&apikey=${AV_API_KEY}`;
    const { data } = await axios.get(url);
    console.log('>>>>', data);
}

main();
