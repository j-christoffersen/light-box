package dev.jchristoffersen.lightbox.render;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FrameBufferTest {
    private static final int WIDTH = 4;
    private static final int HEIGHT = 3;

    private FrameBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new FrameBuffer(WIDTH, HEIGHT);
    }

    @Test
    void newBufferIsBlack() {
        assertEquals(WIDTH * HEIGHT * 3, buffer.pixels.length);
        for (byte b : buffer.pixels) {
            assertEquals(0, b);
        }
    }

    @Test
    void setPixelWithComponentsWritesRgbBytes() {
        buffer.setPixel(1, 2, 10, 20, 30);

        assertArrayEquals(new byte[] { 10, 20, 30 }, buffer.getPixel(1, 2));
    }

    @Test
    void setPixelWithByteArrayWritesRgbBytes() {
        buffer.setPixel(3, 0, new byte[] { 1, 2, 3 });

        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.getPixel(3, 0));
    }

    @Test
    void setPixelWithPackedIntUnpacksChannels() {
        buffer.setPixel(0, 0, 0x123456);

        assertArrayEquals(new byte[] { 0x12, 0x34, 0x56 }, buffer.getPixel(0, 0));
    }

    @Test
    void setPixelWithPackedIntIgnoresAlphaByte() {
        buffer.setPixel(0, 0, 0xFF123456);

        assertArrayEquals(new byte[] { 0x12, 0x34, 0x56 }, buffer.getPixel(0, 0));
    }

    @Test
    void pixelsAreStoredRowMajor() {
        buffer.setPixel(2, 1, 7, 8, 9);

        int idx = (1 * WIDTH + 2) * 3;
        assertEquals(7, buffer.pixels[idx]);
        assertEquals(8, buffer.pixels[idx + 1]);
        assertEquals(9, buffer.pixels[idx + 2]);
    }

    @Test
    void setPixelOnlyAffectsTargetPixel() {
        buffer.setPixel(1, 1, 0xFFFFFF);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (x != 1 || y != 1) {
                    assertArrayEquals(new byte[] { 0, 0, 0 }, buffer.getPixel(x, y), "pixel " + x + "," + y);
                }
            }
        }
    }

    @ParameterizedTest(name = "0x{0}")
    @ValueSource(strings = { "102030", "FF0000", "00FF00", "0000FF", "808080", "FFFFFF", "10FF10" })
    void getPixelInt(String hex) {
        int rgb = Integer.parseInt(hex, 16);
        buffer.setPixel(0, 0, rgb);
        int actual = buffer.getPixelInt(0, 0);
        assertEquals(rgb, actual,
            () -> String.format("expected 0x%06X but was 0x%08X", rgb, actual));
    }

    @Test
    void clearResetsAllPixelsToBlack() {
        buffer.setPixel(0, 0, 0xFFFFFF);
        buffer.setPixel(3, 2, 0x123456);

        buffer.clear();

        assertArrayEquals(new byte[WIDTH * HEIGHT * 3], buffer.pixels);
    }

    @Test
    void copyFromCopiesPixelsWithoutSharingStorage() {
        FrameBuffer source = new FrameBuffer(WIDTH, HEIGHT);
        source.setPixel(2, 2, 0xABCDEF);

        buffer.copyFrom(source);
        source.setPixel(2, 2, 0x000000);

        assertArrayEquals(new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }, buffer.getPixel(2, 2));
    }
}
