package dev.jchristoffersen.lightbox.render;

public interface MatrixOutput {

    int width();

    int height();

    /** Push buffer to the physical display. Called only when a Scene/Transition reports UPDATED or DONE. */
    void present(FrameBuffer buffer);

    void close();
}
