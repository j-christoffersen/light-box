package dev.jchristoffersen.lightbox.scenes;

import java.util.Random;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scene.SceneResult;

public class PlasmaScene implements Scene {
    private double[] phase;
    private double[] alpha;
    private double[] beta;
    private int t;

    PlasmaScene() {
        // Initialize scene with random coefficients and phase values
        this.phase = new double[3];
        this.alpha = new double[4];
        this.beta = new double[4];

        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            this.phase[i] = random.nextDouble() * 2 * Math.PI;
        }
        for (int i = 0; i < 4; i++) {
            this.alpha[i] = 0.05 + random.nextDouble() * 0.1;
            this.beta[i] = 0.8 + random.nextDouble();
        }
    }

    private int[] getPlasmaPixel(int x, int y, double t) {
        double v1 = Math.sin(x * this.alpha[0] + t * this.beta[0]);
        double v2 = Math.cos(y * this.alpha[1] - t * this.beta[1]);
        double v3 = Math.sin((x + y) * this.alpha[2] + t * this.beta[2]);
        double v4 = Math.sin(Math.sqrt(Math.pow(x - Constants.WIDTH / 2, 2) + Math.pow(y - Constants.HEIGHT / 2, 2)) * this.alpha[3] - t * this.beta[3]);

        double total = v1 + v2 + v3 + v4;

        double r = Math.floor(Math.sin(total * Math.PI + this.phase[0]) * 127 + 128);
        double g = Math.floor(Math.cos(total * Math.PI + this.phase[1]) * 127 + 128);
        double b = Math.floor(Math.sin(total * Math.PI + this.phase[2]) * 127 + 128);
        return new int[] { (int) r, (int) g, (int) b };
    }

    public SceneResult getNextFrame() {
        FrameBuffer frameBuffer = new FrameBuffer(Constants.WIDTH, Constants.HEIGHT);
        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                int[] pixel = getPlasmaPixel(x, y, t);
                frameBuffer.setPixel(x, y, pixel[0], pixel[1], pixel[2]);
            }
        }

        t++;
        return SceneResult.updated(frameBuffer);
    }
}