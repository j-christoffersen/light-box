package dev.jchristoffersen.lightbox.scenes.transitions;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scene.SceneResult;
import dev.jchristoffersen.lightbox.scene.Transition;

public class WipeTransition extends Transition {
    public static final int DEFAULT_SLOWDOWN_FACTOR = 2;

    private int frameCount = 0;
    private int wipeProgress = 0;
    private FrameBuffer oldBuffer;
    private FrameBuffer newBuffer;
    public WipeTransition(Scene oldScene, Scene newScene) {
        super(oldScene, newScene);
    }

    public SceneResult getNextFrame() {
        boolean wipeUpdated = false;
        if (frameCount++ % DEFAULT_SLOWDOWN_FACTOR == 0) {
            wipeProgress++;
            wipeUpdated = true;    
        }

        SceneResult oldResult = oldScene.getNextFrame();
        if (oldResult instanceof SceneResult.Updated updated) {
            oldBuffer = updated.frameBuffer();
        } else if (oldResult instanceof SceneResult.Done done) {
            oldBuffer = done.frameBuffer();
        }

        SceneResult newResult = newScene.getNextFrame();
        if (newResult instanceof SceneResult.Updated updated) {
            newBuffer = updated.frameBuffer();
        } else if (newResult instanceof SceneResult.Done done) {
            newBuffer = done.frameBuffer();
        }

        // fully wiped, exit transition
        if (wipeProgress >= Constants.WIDTH) {
            return SceneResult.done(newBuffer, newScene);
        }

        if (!wipeUpdated && oldResult instanceof SceneResult.NoUpdate && newResult instanceof SceneResult.NoUpdate) {
            return SceneResult.noUpdate();
        }
        
        FrameBuffer frameBuffer = new FrameBuffer(Constants.WIDTH, Constants.HEIGHT);
        frameBuffer.copyFrom(oldBuffer);

        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < wipeProgress; x++) {
                frameBuffer.setPixel(x, y, newBuffer.getPixel(x, y));
            }
        }

        return SceneResult.updated(frameBuffer);
    }
}
