package dev.jchristoffersen.lightbox.scene;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public abstract class SlowScene implements Scene {
    private final int rate;
    private int count = 0;
    private FrameBuffer buffer;

    public SlowScene(int rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("rate must be greater than 0");
        }
        this.rate = rate;
    }

    public final SceneResult getNextFrame() {
        if (count++ % rate == 0) {
            SceneResult result = this.computeNextFrame();
            buffer = result.frameBuffer();
            return result;
        }

        return SceneResult.noUpdate(buffer);
    }

    protected abstract SceneResult computeNextFrame();
}
