// for some reason we can access surfline's APIs using axios but not using Java's HttpClient

import axios from 'axios';

const [,, spotId, endpoint] = process.argv;

axios.get(`https://services.surfline.com/kbyg/spots/forecasts/${endpoint}`, {
    params: { spotId, days: 1, intervalHours: 1 },
    headers: {
        'User-Agent': 'PostmanRuntime/7.53.0',
        'Accept': '*/*'
    },
    timeout: 10000
})
.then(response => {
    console.log(JSON.stringify(response.data));
})
.catch(err => {
    console.error(err.message);
    process.exit(1);
});
