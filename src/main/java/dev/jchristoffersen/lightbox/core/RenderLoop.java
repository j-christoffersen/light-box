package dev.jchristoffersen.lightbox.core;

import java.util.Objects;

import dev.jchristoffersen.lightbox.render.LedMatrix;
import dev.jchristoffersen.lightbox.render.FrameBuffer;

public class RenderLoop {
    private final FrameClock frameClock;
    private final SceneManager sceneManager;
    private final LedMatrix ledMatrix;

    public RenderLoop(SceneManager _sceneManager, LedMatrix _ledMatrix) {
        sceneManager = Objects.requireNonNull(_sceneManager, "sceneManager");
        ledMatrix = Objects.requireNonNull(_ledMatrix, "ledMatrix");

        this.frameClock = new FrameClock(60, () -> {
            try {
                FrameBuffer nextFrame = sceneManager.getNextFrame();

                // If we get a new buffer, push to the screen. Otherwise, we're done.
                if (nextFrame != null) {
                    ledMatrix.present(nextFrame);
                }
            } catch (Exception e) {
                System.err.println("Error in frame clock tick");
                e.printStackTrace();
                // stop process
                System.exit(1);
            }
        });
    }

    public void start() {
        ledMatrix.ready().thenCompose((_void) -> {
            return sceneManager.start().thenRun(() -> {
                System.out.println("sceneManager.start finished");
            });
        }).thenRun(() -> {
            frameClock.start();
            System.out.println("frameClock.start finished");
        });
    }
}