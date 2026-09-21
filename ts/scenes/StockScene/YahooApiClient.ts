import z from "zod";
import _ from "lodash";

export type IntradayData = { previousClose: number, price: number, data: Array<{ timestamp: number, price: number }> };

const YahooResponseSchema = z.object({
    chart: z.object({
        result: z.array(z.object({
            meta: z.object({
                symbol: z.string(),
                regularMarketPrice: z.number(),
                previousClose: z.number(),
            }),
            timestamp: z.array(z.number()),
            indicators: z.object({
                quote: z.array(z.object({
                    close: z.array(z.number()),
                })),
            }),
        })),
    }),
});

class YahooApiClient {
    constructor() {

    }

    async getIntradayData(symbol: string): Promise<IntradayData> {
        const response = await fetch(`https://query1.finance.yahoo.com/v8/finance/chart/${symbol}?interval=1m&range=1d`);
        const data = await response.json();
        const parsed = YahooResponseSchema.parse(data);

        const desiredResult = parsed.chart.result.find(result => result.meta.symbol === symbol);
        if (!desiredResult) {
            throw new Error(`No data found for symbol ${symbol}`);
        }

        const timestamps = desiredResult?.timestamp;
        const prices = desiredResult?.indicators.quote[0].close; // unsure what a 2nd quote object would mean

        return {
            previousClose: desiredResult?.meta.previousClose,
            price: desiredResult?.meta.regularMarketPrice,
            data: _.zipWith(timestamps, prices, (timestamp, price) => ({
            timestamp, price
            })),
        }
    }
}

export { YahooApiClient };
