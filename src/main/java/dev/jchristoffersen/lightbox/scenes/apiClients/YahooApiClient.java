package dev.jchristoffersen.lightbox.scenes.apiClients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;

import lombok.SneakyThrows;
import com.google.gson.Gson;

public class YahooApiClient {
    
    public YahooApiClient() {

    }

    record ChartData(Chart chart) {}
    record Chart(Result[] result) {}
    record Result(Meta meta, Integer[] timestamp, Indicators indicators) {}
    record Meta(String symbol, Double regularMarketPrice, Double previousClose) {}
    record Indicators(Quote[] quote) {}
    record Quote(Double[] close) {}

    public record IntradayData(int[] timestamps, double[] prices, double currentPrice, double previousClose) {}
    @SneakyThrows
    public IntradayData getIntradayData(String ticker) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + ticker + "?interval=1m&range=1d";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        ChartData intradayData = gson.fromJson(response.body(), ChartData.class);

        Result result = Arrays.stream(intradayData.chart().result())
            .filter(r -> r.meta().symbol().equals(ticker))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No result found"));

        System.out.println("Result found");

        int[] timestamps = Arrays.stream(result.timestamp()).mapToInt(Integer::intValue).toArray();

        // sometimes null values are present, assign them to the previous value (or open price if first value is null)
        Double[] prices = result.indicators().quote()[0].close();
        double[] pricesCleaned = new double[prices.length];
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] == null) {
                if (i == 0) {
                    pricesCleaned[i] = result.meta().previousClose();
                } else {
                    pricesCleaned[i] = pricesCleaned[i - 1];
                }
            } else {
                pricesCleaned[i] = prices[i];
            }
        }

        if (timestamps.length != prices.length) {
            throw new IllegalStateException("Timestamps and prices length mismatch");
        }

        return new IntradayData(timestamps, pricesCleaned, result.meta().regularMarketPrice(), result.meta().previousClose());
    }
}
