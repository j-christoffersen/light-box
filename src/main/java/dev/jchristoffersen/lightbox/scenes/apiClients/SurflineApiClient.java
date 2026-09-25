package dev.jchristoffersen.lightbox.scenes.apiClients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

public class SurflineApiClient {
    public SurflineApiClient() {
        
    }

    record SurflineSurfResponse(SurflineSurfData data) {}
    record SurflineSurfData(SurfEntry[] surf) {}
    record SurfEntry(int timestamp, Surf surf) implements SurflineEntry {}
    record Surf(int min, int max) {}

    record SurflineRatingResponse(SurflineRatingData data) {}
    record SurflineRatingData(RatingEntry[] rating) {}
    record RatingEntry(int timestamp, Rating rating) implements SurflineEntry {}
    record Rating(String key, int value) {}

    record SurflineWindResponse(SurflineWindData data) {}
    record SurflineWindData(WindEntry[] wind) {}
    record WindEntry(int timestamp, double speed, double direction, int optimalScore) implements SurflineEntry {}

    record SurflineTidesResponse(SurflineTidesData data) {}
    record SurflineTidesData(TidesEntry[] tides) {}
    record TidesEntry(int timestamp, double height) implements SurflineEntry {}

    record Tides(double height, double minOrMax, boolean isMax) {}
    public record SurflineData(SurfEntry currentSurf, Rating currentRating, WindEntry currentWind, Tides currentTides) {}
    public CompletableFuture<SurflineData> getSurflineData(String spotId) {
        HttpClient client = HttpClient.newHttpClient();

        CompletableFuture<HttpResponse<String>> surfReqFuture = client.sendAsync(
            HttpRequest.newBuilder(URI.create("https://services.surfline.com/kbyg/spots/forecasts/surf?spotId=" + spotId + "&days=1&intervalHours=1&units[waveHeight]=FT")).build(),
            HttpResponse.BodyHandlers.ofString());
    
        CompletableFuture<HttpResponse<String>> ratingReqFuture = client.sendAsync(
            HttpRequest.newBuilder(URI.create("https://services.surfline.com/kbyg/spots/forecasts/rating?spotId=" + spotId + "&days=1&intervalHours=1")).build(),
            HttpResponse.BodyHandlers.ofString());
        
        CompletableFuture<HttpResponse<String>> windReqFuture = client.sendAsync(
            HttpRequest.newBuilder(URI.create("https://services.surfline.com/kbyg/spots/forecasts/wind?spotId=" + spotId + "&days=1&intervalHours=1")).build(),
            HttpResponse.BodyHandlers.ofString());
        
        CompletableFuture<HttpResponse<String>> tideReqFuture = client.sendAsync(
            HttpRequest.newBuilder(URI.create("https://services.surfline.com/kbyg/spots/forecasts/tides?spotId=" + spotId + "&days=1&intervalHours=1")).build(),
            HttpResponse.BodyHandlers.ofString());

        return CompletableFuture.allOf(surfReqFuture, ratingReqFuture, windReqFuture, tideReqFuture).thenApply((v) -> {
            Gson gson = new Gson();
            SurflineSurfResponse surfResponse = gson.fromJson(surfReqFuture.join().body(), SurflineSurfResponse.class);
            SurflineRatingResponse ratingResponse = gson.fromJson(ratingReqFuture.join().body(), SurflineRatingResponse.class);
            SurflineWindResponse windResponse = gson.fromJson(windReqFuture.join().body(), SurflineWindResponse.class);
            SurflineTidesResponse tideResponse = gson.fromJson(tideReqFuture.join().body(), SurflineTidesResponse.class);

            TidesEntry currentTides = getLatestEntry(tideResponse.data().tides());
            TidesEntry[] tides = tideResponse.data().tides();

            double latestTideHeight = 0;
            double currentTideHeight = 0;
            Boolean isIncreasing = null;
            for (int i = 0; i < tides.length; i++) {
                if (i == 0 || tides[i].timestamp() < System.currentTimeMillis() / 1000) {
                    // iterate until we find current tide height
                    currentTideHeight = tides[i].height();
                } else if (isIncreasing == null) {
                    // on the first entry after the current tide height, determine if the tide is increasing or decreasing
                    isIncreasing = tides[i].height() > currentTideHeight;
                    latestTideHeight = tides[i].height();
                } else if (isIncreasing) {
                    // once we see a decrease, we found the local maximum
                    if (tides[i].height() < latestTideHeight) {
                        break;
                    }
                } else {
                    // once we see an increase, we found the local min
                    if (tides[i].height() > latestTideHeight) {
                        break;
                    }
                }
            }



            return new SurflineData(
                getLatestEntry(surfResponse.data().surf()),
                getLatestEntry(ratingResponse.data().rating()).rating(),
                getLatestEntry(windResponse.data().wind()),
                new Tides(currentTides.height(), latestTideHeight, isIncreasing)
            );
        });



        
    }

    interface SurflineEntry {
        int timestamp();
    }
    private <T extends SurflineEntry> T getLatestEntry(T[] entries) {
        return Arrays.stream(entries).filter(entry -> entry.timestamp() < System.currentTimeMillis() / 1000)
        .max(Comparator.comparingInt(SurflineEntry::timestamp))
        .orElseThrow();
    }
}
