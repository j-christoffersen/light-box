package dev.jchristoffersen.lightbox.scenes.apiClients;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import dev.jchristoffersen.lightbox.scenes.apiClients.YahooApiClient.IntradayData;

class YahooApiClientTest {
    // Regular session for the fixtures' trading day: 9:30 to 16:00 America/New_York
    private static final int MARKET_OPEN = 1790343000;
    private static final int MARKET_CLOSE = 1790366400;

    @Test
    void parsesFullSessionAfterClose() throws IOException {
        IntradayData data = YahooApiClient.parseIntradayData(fixture("pypl-after-close.json"), "PYPL");

        // one point per minute from open to close, inclusive
        assertEquals(391, data.timestamps().length);
        assertEquals(391, data.prices().length);
        assertEquals(MARKET_OPEN, data.timestamps()[0]);
        assertEquals(MARKET_CLOSE, data.timestamps()[390]);

        assertEquals(52.665, data.prices()[0], 1e-9);
        assertEquals(55.04, data.prices()[390], 1e-9);
        assertEquals(55.04, data.currentPrice(), 1e-9);
        assertEquals(52.6, data.previousClose(), 1e-9);
    }

    @Test
    void fillsMissingPricesDuringTradingHours() throws IOException {
        // Mid-session responses contain nulls for minutes without a close, including the current minute.
        IntradayData data = YahooApiClient.parseIntradayData(fixture("pypl-during-trading.json"), "PYPL");

        assertArrayEquals(
            new int[] { MARKET_OPEN, MARKET_OPEN + 60, MARKET_OPEN + 120, MARKET_OPEN + 180, MARKET_OPEN + 240, MARKET_OPEN + 300 },
            data.timestamps());
        assertArrayEquals(
            new double[] {
                52.6,   // leading null -> previous close
                52.72,
                52.69,
                52.69,  // null -> carried forward
                52.54,
                52.54   // trailing null (current minute) -> carried forward
            },
            data.prices(),
            1e-9);
        assertEquals(52.54, data.currentPrice(), 1e-9);
        assertEquals(52.6, data.previousClose(), 1e-9);
    }

    private static String fixture(String name) throws IOException {
        try (InputStream in = YahooApiClientTest.class.getResourceAsStream("/yahoo/" + name)) {
            if (in == null) {
                throw new IOException("Missing test fixture: " + name);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
