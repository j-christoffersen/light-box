package dev.jchristoffersen.lightbox.render;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface LedMatrixNative extends Library {
    public static final LedMatrixNative INSTANCE = Native.load("/home/jackson/code/rpi-rgb-led-matrix/lib/librgbmatrix.so.1", LedMatrixNative.class); // TODO make dynamic

    public Pointer led_matrix_create(int rows, int chained, int parallel);
    public void led_matrix_delete(Pointer matrix);

    public Pointer led_matrix_get_canvas(Pointer matrix);
    public void led_canvas_set_pixel(Pointer canvas, int x, int y, byte r, byte g, byte b);
    public void led_canvas_clear(Pointer canvas);
    public Pointer led_matrix_swap_on_vsync(Pointer matrix, Pointer canvas);
}
