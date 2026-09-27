package dev.jchristoffersen.lightbox.render;

import java.util.Objects;

import com.sun.jna.Pointer;

public class RaspberryPiLedMatrix implements LedMatrix {
    public record Config(int rows, int cols, int gpioSlowdown, String dropPrivUser, String dropPrivGroup) {}

    private final LedMatrixNative ledMatrixNative;
    private final Config config;
    private final Pointer matrix;
    private Pointer offscreenCanvas;

    public RaspberryPiLedMatrix(LedMatrixNative ledMatrixNative, Config config) {
        this.ledMatrixNative = Objects.requireNonNull(ledMatrixNative, "ledMatrixNative");
        this.config = Objects.requireNonNull(config, "config");

        RGBLedMatrixOptions options = new RGBLedMatrixOptions();
        options.rows = config.rows();
        options.cols = config.cols();
        // options.brightness = 50;
        // options.disable_hardware_pulsing = true; // allows you to run without sudo, but makes image worse

        RGBLedRuntimeOptions rtOptions = new RGBLedRuntimeOptions();
        rtOptions.gpio_slowdown = config.gpioSlowdown();
        rtOptions.drop_priv_user = config.dropPrivUser();
        rtOptions.drop_priv_group = config.dropPrivGroup();

        matrix = ledMatrixNative.led_matrix_create_from_options_and_rt_options(options, rtOptions);
        offscreenCanvas = ledMatrixNative.led_matrix_get_canvas(matrix);
    }

    public void present(FrameBuffer buffer) {
        for (int y = 0; y < config.rows(); y++) {
            for (int x = 0; x < config.cols(); x++) {
                ledMatrixNative.led_canvas_set_pixel(
                    offscreenCanvas,
                    x,
                    y,
                    buffer.pixels[3 * (y * config.cols() + x)],
                    buffer.pixels[3 * (y * config.cols() + x) + 1],
                    buffer.pixels[3 * (y * config.cols() + x) + 2]
                );
            }
        }
    
        offscreenCanvas = ledMatrixNative.led_matrix_swap_on_vsync(matrix, offscreenCanvas);
    }
}
