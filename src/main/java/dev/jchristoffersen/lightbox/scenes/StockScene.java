package dev.jchristoffersen.lightbox.scenes;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.StaticScene;
import dev.jchristoffersen.lightbox.scenes.apiClients.YahooApiClient;
import dev.jchristoffersen.lightbox.text.ParsedBdf;
import dev.jchristoffersen.lightbox.text.TextRenderer;

public class StockScene extends StaticScene {
    private static final class Colors {
        public static final int white = 0xFFFFFF;
        public static final int grey = 0x808080;
        public static final int red = 0xFF0000;
        public static final int green = 0x00FF00;
        public static final int lightRed = 0x330000;
        public static final int lightGreen = 0x003300;
        public static final int darkRed = 0x660000;
        public static final int darkGreen = 0x006600;
    }
    private String ticker;
    private YahooApiClient.IntradayData intradayData;
    private ParsedBdf parsedBdf;

    public StockScene(String ticker) {
        this.ticker = ticker;
    }
    
    public CompletableFuture<Void> prep() {
        return CompletableFuture.runAsync(() -> {
            // fetch stock info
            YahooApiClient yahooApiClient = new YahooApiClient();
            YahooApiClient.IntradayData intradayData = yahooApiClient.getIntradayData(ticker);
            this.intradayData = intradayData;
            this.parsedBdf = ParsedBdf.parse("/fonts/5x8.bdf");
        })
        .exceptionally(e -> {
            System.err.println("Error prepping StockScene: " + e.getMessage());
            e.printStackTrace();
            return null;
        });
    }

    public FrameBuffer renderOnce() {
        if (intradayData == null) {
            throw new IllegalStateException("Intraday data has not been fetched");
        }

        double gain = intradayData.currentPrice() - intradayData.previousClose();

        FrameBuffer frameBuffer = new FrameBuffer(Constants.WIDTH, Constants.HEIGHT);
    
        TextRenderer textRenderer = new TextRenderer(parsedBdf, 0xFFFFFF);
        textRenderer.renderTextToFrameBuffer(ticker, 0, -1, frameBuffer);
        textRenderer.renderTextToFrameBuffer(String.format("$%.2f", intradayData.currentPrice()), 0, 7, frameBuffer);
        textRenderer.setColor(gain < 0 ? Colors.darkRed : gain > 0 ? Colors.darkGreen : Colors.white);

        String sign = gain < 0 ? "-" : "+";
        textRenderer.renderTextToFrameBuffer(String.format("%s$%.2f", sign, Math.abs(gain)), 31, -1, frameBuffer);
        textRenderer.renderTextToFrameBuffer(String.format("%s%.0f%%", sign, Math.abs(gain / intradayData.previousClose() * 100)), 31, 7, frameBuffer);

        // 9:30 - 4:00 = 390 minutes
        // 390 minutes / 64 pixels = 6.09375 minutes per pixel
        // iterate and get price for each pixel
        int[] timestamps = intradayData.timestamps();
        int t0 = timestamps[0];
        Double[] pixelData = new Double[64];
        for (int i = 0; i < timestamps.length; i++) {
            double minutesSince930 = (double) (timestamps[i] - t0) / 60;
            int pixelIndex = (int) (minutesSince930 / 6.09375);
            pixelData[Math.min(pixelIndex, 63)] = intradayData.prices()[i];
        }

        double high = Arrays.stream(pixelData).filter(Objects::nonNull).mapToDouble(Double::doubleValue).max().getAsDouble();
        double low = Arrays.stream(pixelData).filter(Objects::nonNull).mapToDouble(Double::doubleValue).min().getAsDouble();
        System.out.println("intradayData.prices: " + Arrays.toString(intradayData.prices()));
        System.out.println("pixelData: " + Arrays.toString(pixelData));
        System.out.println("high: " + high);
        System.out.println("low: " + low);
        for (int x = 0; x < 64; x++) {
            if (pixelData[x] != null) {
                int v = 31 - (int) Math.ceil((pixelData[x] - low) / (high - low) * 15);
                System.out.println("pixelData[x]: " + pixelData[x]);
                System.out.println("v: " + v);
                for (int y = 16; y < 32; y++) {
                    if (y == v) {
                        frameBuffer.setPixel(x, y, gain < 0 ? Colors.red : gain > 0 ? Colors.green : Colors.white);
                    } else if (y >= intradayData.previousClose() && y < v) {
                        frameBuffer.setPixel(x, y, gain < 0 ? Colors.lightRed : gain > 0 ? Colors.lightGreen : Colors.grey);
                    } else if (y <= intradayData.previousClose() && y > v) {
                        frameBuffer.setPixel(x, y, gain < 0 ? Colors.lightRed : gain > 0 ? Colors.lightGreen : Colors.grey);
                    }
                }
            }
        }

        return frameBuffer;
    }
}
