package dev.jchristoffersen.lightbox.scenes;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.StaticScene;

public class GameOfLifeScene extends Scene {
    private final boolean[][] grid;

    public GameOfLifeScene() {
        this.grid = new boolean[Constants.HEIGHT][Constants.WIDTH];
        int[] palette = {0x9955ff, 0x5599ff, 0x99ff55, 0xff5599};
        int fgColor = palette[ThreadLocalRandom.current().nextInt(palette.length)];
    }

    public FrameBuffer getNextFrame() {
        boolean[][] nextGrid = new boolean[Constants.HEIGHT][Constants.WIDTH];
        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                int neighbors = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dy == 0 && dx == 0) continue;
                        if (grid[y + dy][x + dx]) neighbors++;
                    }
                }

                if ((grid[y][x] && neighbors == 2) || neighbors == 3) {
                    nextGrid[y][x] = true;
                } else {
                    nextGrid[y][x] = false;
                }
            }
        }

        grid = nextGrid;
        frameBuffer = new FrameBuffer(Constants.WIDTH, Constants.HEIGHT);

        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                if (grid[y][x]) {
                    frameBuffer.setPixel(x, y, fgColor);
                }
            }
        }

        return SceneResult.updated(frameBuffer);
    }
}
