package dev.jchristoffersen.lightbox.render;

import com.sun.jna.Pointer;

import dev.jchristoffersen.lightbox.constants.Constants;

public class RaspberryPiLedMatrix implements LedMatrix {
    private final Pointer ledMatrixNative;
    private Pointer offscreenCanvas;


    public RaspberryPiLedMatrix() {
        ledMatrixNative = LedMatrixNative.INSTANCE.led_matrix_create(32, 2, 1); // chained 2 rows of 32 pixels each powers the 32x64 panel
        offscreenCanvas = LedMatrixNative.INSTANCE.led_matrix_get_canvas(ledMatrixNative);
    }

    public void present(FrameBuffer buffer) {
        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                LedMatrixNative.INSTANCE.led_canvas_set_pixel(
                    offscreenCanvas,
                    x,
                    y,
                    buffer.pixels[3 * (y * Constants.WIDTH + x)],
                    buffer.pixels[3 * (y * Constants.WIDTH + x) + 1],
                    buffer.pixels[3 * (y * Constants.WIDTH + x) + 2]
                );
            }
        }
    
        offscreenCanvas = LedMatrixNative.INSTANCE.led_matrix_swap_on_vsync(ledMatrixNative, offscreenCanvas);
    }
}
