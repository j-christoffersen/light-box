package dev.jchristoffersen.lightbox.scenes.transitions;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scene.SceneResult;
import dev.jchristoffersen.lightbox.scene.Transition;

public class FadeTransition extends Transition {
    private FrameBuffer frameBuffer;
    private int progress = 0;

    public FadeTransition(Scene oldScene, Scene newScene) {
        super(oldScene, newScene);
    }

    public SceneResult getNextFrame() {
        FrameBuffer oldBuffer = oldScene.getNextFrame().frameBuffer();
        FrameBuffer newBuffer = newScene.getNextFrame().frameBuffer();

        progress++;

        if (progress > 60) {
            return SceneResult.done(newBuffer, newScene);
        }

        frameBuffer = new FrameBuffer(Constants.WIDTH, Constants.HEIGHT);
        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                frameBuffer.setPixel(x, y, blend(oldBuffer.getPixel(x, y), newBuffer.getPixel(x, y), progress / 90.0));
            }
        }


        return SceneResult.updated(frameBuffer);
    }

    private byte[] blend(byte[] oldPixel, byte[] newPixel, double progress) {
        return new byte[] {
            (byte) ((oldPixel[0] & 0xff) * (1 - progress) + (newPixel[0] & 0xff) * progress),
            (byte) ((oldPixel[1] & 0xff) * (1 - progress) + (newPixel[1] & 0xff) * progress),
            (byte) ((oldPixel[2] & 0xff) * (1 - progress) + (newPixel[2] & 0xff) * progress)
        };
    }
}
