package dev.jchristoffersen.lightbox.scenes.apiClients;

import java.io.IOException;
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
    record Result(Meta meta, int[] timestamp, Indicators indicators) {}
    record Meta(String symbol, double regularMarketPrice, double previousClose) {}
    record Indicators(Quote[] quote) {}
    record Quote(double[] close) {}

    public record IntradayData(int[] timestamps, double[] prices, double currentPrice, double previousClose) {}
    @SneakyThrows
    public IntradayData getIntradayData(String ticker) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + ticker + "?interval=1m&range=1d";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
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

        int[] timestamps = result.timestamp();
        double[] prices = result.indicators().quote()[0].close();

        if (timestamps.length != prices.length) {
            throw new IllegalStateException("Timestamps and prices length mismatch");
        }

        return new IntradayData(timestamps, prices, result.meta().regularMarketPrice(), result.meta().previousClose());
    }
}
