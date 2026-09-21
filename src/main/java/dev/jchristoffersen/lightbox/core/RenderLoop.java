package dev.jchristoffersen.lightbox.core;

public class RenderLoop {
    private final FrameClock frameClock;
    private final SceneManager sceneManager;
    private final LedMatrix ledMatrix;

    public RenderLoop(SceneManager sceneManager, LedMatrix ledMatrix) {
        this.sceneManager = Objects.requireNonNull(sceneManager, "sceneManager");
        this.ledMatrix = Objects.requireNonNull(ledMatrix, "ledMatrix");
        this.frameClock = new FrameClock(60, () -> {
            SceneResult result = sceneManager.getNextFrame();
            // SceneManager should never be done at this point, so throw an exception
            if (result instanceof SceneResult.Done done) {
                throw new IllegalStateException("SceneManager is done");
            }
            // If we get a new buffer, push to the screen. Otherwise, we're done.
            if (result instanceof SceneResult.Updated updated) {
                updated.buffer().render(ledMatrix);
            }
        });
    }

    public void start() {
        frameClock.start();
    }
}