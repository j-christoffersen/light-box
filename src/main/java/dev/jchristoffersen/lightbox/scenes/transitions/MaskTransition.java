package dev.jchristoffersen.lightbox.scenes.transitions;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scene.SceneResult;
import dev.jchristoffersen.lightbox.scene.Transition;

/**
 * 
 * MaskTransition is a transition that uses an animated mask to transition between two scenes.
 */
public abstract class MaskTransition extends Transition {
    private int frameCount = 0;
    protected int progress = 0;
    private FrameBuffer oldBuffer;
    private FrameBuffer newBuffer;
    protected final int slowdownFactor = 2;
    private FrameBuffer frameBuffer;

    public MaskTransition(Scene oldScene, Scene newScene) {
        super(oldScene, newScene);
    }

    protected record MaskResult(boolean[][] mask, boolean done) {}
    protected abstract MaskResult getMask();

    public SceneResult getNextFrame() {
        boolean maskNeedsUpdate = false;

        if (frameCount++ % slowdownFactor == 0) {
            progress++;
            maskNeedsUpdate = true;    
        }

        SceneResult oldResult = oldScene.getNextFrame();
        oldBuffer = oldResult.frameBuffer();

        SceneResult newResult = newScene.getNextFrame();
        newBuffer = newResult.frameBuffer();

        if (!maskNeedsUpdate && oldResult instanceof SceneResult.NoUpdate && newResult instanceof SceneResult.NoUpdate) {
            return SceneResult.noUpdate(frameBuffer);
        }

        MaskResult maskResult = getMask();        
        frameBuffer = new FrameBuffer(Constants.WIDTH, Constants.HEIGHT);
        frameBuffer.copyFrom(oldBuffer);

        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                if (maskResult.mask[y][x]) {
                    frameBuffer.setPixel(x, y, newBuffer.getPixel(x, y));
                }
            }
        }

        if (maskResult.done) {
            return SceneResult.done(frameBuffer, newScene);
        }

        return SceneResult.updated(frameBuffer);
    }
    
}
