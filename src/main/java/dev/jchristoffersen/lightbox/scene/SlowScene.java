package dev.jchristoffersen.lightbox.scene;
public abstract class SlowScene implements Scene {
    private final int rate;
    private int count = 0;

    public SlowScene(int rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("rate must be greater than 0");
        }
        this.rate = rate;
    }

    public final SceneResult getNextFrame() {
        if (count++ % rate == 0) {
            return this.computeNextFrame();
        }

        return SceneResult.noUpdate();
    }

    protected abstract SceneResult computeNextFrame();
}
