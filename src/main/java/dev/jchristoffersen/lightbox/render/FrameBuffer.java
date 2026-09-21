package dev.jchristoffersen.lightbox.render;

import java.util.Arrays;

public final class FrameBuffer {

    public final int width;
    public final int height;
    public final byte[] pixels;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new byte[width * height * 3];
    }

    public void setPixel(int x, int y, int r, int g, int b) {
        int idx = (y * width + x) * 3;
        pixels[idx] = (byte) r;
        pixels[idx + 1] = (byte) g;
        pixels[idx + 2] = (byte) b;
    }

    public void setPixel(int x, int y, int rgb) {
        int idx = (y * width + x) * 3;
        pixels[idx] = (byte) (rgb >> 16);
        pixels[idx + 1] = (byte) (rgb >> 8);
        pixels[idx + 2] = (byte) rgb;
    }

    public void clear() {
        Arrays.fill(pixels, (byte) 0);
    }

    public void copyFrom(FrameBuffer other) {
        System.arraycopy(other.pixels, 0, pixels, 0, pixels.length);
    }
}
