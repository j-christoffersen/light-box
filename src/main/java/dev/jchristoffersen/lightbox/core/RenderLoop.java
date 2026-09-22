package dev.jchristoffersen.lightbox.core;

import java.util.Objects;

import dev.jchristoffersen.lightbox.render.LedMatrix;
import dev.jchristoffersen.lightbox.render.FrameBuffer;

public class RenderLoop {
    private final FrameClock frameClock;
    private final SceneManager sceneManager;

    public RenderLoop(SceneManager _sceneManager, LedMatrix _ledMatrix) {
        sceneManager = Objects.requireNonNull(_sceneManager, "sceneManager");
        LedMatrix ledMatrix = Objects.requireNonNull(_ledMatrix, "ledMatrix");

        this.frameClock = new FrameClock(60, () -> {
            FrameBuffer nextFrame = sceneManager.getNextFrame();

            // If we get a new buffer, push to the screen. Otherwise, we're done.
            if (nextFrame != null) {
                ledMatrix.present(nextFrame);
            }
        });
    }

    public void start() {
        sceneManager.start().thenRun(() -> {
            frameClock.start();
        });
    }
}