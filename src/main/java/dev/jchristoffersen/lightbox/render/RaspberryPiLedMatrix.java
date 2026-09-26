package dev.jchristoffersen.lightbox.render;

import com.sun.jna.Pointer;

import dev.jchristoffersen.lightbox.constants.Constants;

public class RaspberryPiLedMatrix implements LedMatrix {
    private final Pointer ledMatrixNative;
    private Pointer offscreenCanvas;


    public RaspberryPiLedMatrix() {
        RGBLedMatrixOptions options = new RGBLedMatrixOptions();
        options.rows = 32;
        options.cols = 64;

        RGBLedRuntimeOptions rtOptions = new RGBLedRuntimeOptions();
        rtOptions.gpio_slowdown = 2;

        ledMatrixNative = LedMatrixNative.INSTANCE.led_matrix_create_from_options_and_rt_options(options, rtOptions);
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
