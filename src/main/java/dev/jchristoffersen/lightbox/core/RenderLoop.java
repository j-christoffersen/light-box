package dev.jchristoffersen.lightbox.core;

import java.util.Objects;

import dev.jchristoffersen.lightbox.render.MatrixOutput;
import dev.jchristoffersen.lightbox.render.FrameBuffer;

public class RenderLoop {
    private final FrameClock frameClock;

    public RenderLoop(SceneManager sceneManager, MatrixOutput matrixOutput) {
        SceneManager _sceneManager = Objects.requireNonNull(sceneManager, "sceneManager");
        MatrixOutput _matrixOutput = Objects.requireNonNull(matrixOutput, "matrixOutput");
        this.frameClock = new FrameClock(60, () -> {
            FrameBuffer nextFrame = _sceneManager.getNextFrame();

            // If we get a new buffer, push to the screen. Otherwise, we're done.
            if (nextFrame != null) {
                _matrixOutput.present(nextFrame);
            }
        });
    }

    public void start() {
        frameClock.start();
    }
}