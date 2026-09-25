package dev.jchristoffersen.lightbox.scenes.transitions;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.scene.Scene;

public class CircleMaskTransition extends MaskTransition {
    public static final int DEFAULT_SLOWDOWN_FACTOR = 2;
    public CircleMaskTransition(Scene oldScene, Scene newScene) {
        super(oldScene, newScene);
    }

    public MaskResult getMask() {
        boolean[][] mask = new boolean[Constants.HEIGHT][Constants.WIDTH];

        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                mask[y][x] = Math.sqrt(Math.pow(x - Constants.WIDTH / 2, 2) + Math.pow(y - Constants.HEIGHT / 2, 2)) <= progress;
            }
        }

        // we are done when a corner of the screen is masked
        return new MaskResult(mask, mask[0][0]);
    }
}
