package dev.jchristoffersen.lightbox.scene;

import java.util.Objects;

public class SlowScene implements Scene {
    private final Scene parent;
    private final int rate;
    private int count = 0;

    SlowScene(Scene parent, int rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("rate must be greater than 0");
        }
        this.parent = Objects.requireNonNull(parent, "parent");
        this.rate = rate;
    }

    prep() {
        parent.prep();
    }

    getNextFrame() {
        if (count % rate == 0) {
            return parent.getNextFrame();
        }

        return SceneResult.noUpdate();
    }
}
