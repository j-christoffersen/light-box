package dev.jchristoffersen.lightbox.bmp;

import java.awt.image.BufferedImage;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public class BitmapRenderer {
    public static void render(BufferedImage image, int x, int y, FrameBuffer frameBuffer) {
        if (image == null) {
            throw new IllegalArgumentException("Null image provided");
        }
    
        for (int dy = 0; dy < image.getHeight(); dy++) {
            for (int dx = 0; dx < image.getWidth(); dx++) {
                int rgb = image.getRGB(dx, dy); // packed 0xAARRGGBB
                byte r = (byte) ((rgb >> 16) & 0xFF);
                byte g = (byte) ((rgb >> 8) & 0xFF);
                byte b = (byte) (rgb & 0xFF);
    
                frameBuffer.setPixel(dx + x, dy + y, r, g, b);
            }
        }
    }
}
