package dev.jchristoffersen.lightbox.scenes.transitions;

import java.util.Arrays;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.scene.Scene;

public class BarsTransition extends MaskTransition {
    public static final int BAR_THICKNESS = 8;
    public BarsTransition(Scene oldScene, Scene newScene) {
        super(oldScene, newScene);
    }

    public MaskResult getMask() {
        boolean[][] mask = new boolean[Constants.HEIGHT][Constants.WIDTH];
        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                mask[y][x] = ((y / BAR_THICKNESS) % 2 == 0) ? x < progress : (x >= Constants.WIDTH - progress);
            }
        }

        return new MaskResult(mask, progress >= Constants.WIDTH);
    }
}
