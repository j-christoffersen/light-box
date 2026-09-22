package dev.jchristoffersen.lightbox.core;

import java.util.Objects;

import dev.jchristoffersen.lightbox.render.LedMatrix;
import dev.jchristoffersen.lightbox.render.FrameBuffer;

public class RenderLoop {
    private final FrameClock frameClock;

    public RenderLoop(SceneManager sceneManager, LedMatrix ledMatrix) {
        SceneManager _sceneManager = Objects.requireNonNull(sceneManager, "sceneManager");
        LedMatrix _ledMatrix = Objects.requireNonNull(ledMatrix, "ledMatrix");
        this.frameClock = new FrameClock(60, () -> {
            FrameBuffer nextFrame = _sceneManager.getNextFrame();

            // If we get a new buffer, push to the screen. Otherwise, we're done.
            if (nextFrame != null) {
                _ledMatrix.present(nextFrame);
            }
        });
    }

    public void start() {
        frameClock.start();
    }
}