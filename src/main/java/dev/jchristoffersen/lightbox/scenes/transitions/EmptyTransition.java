package dev.jchristoffersen.lightbox.scenes.transitions;

import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scene.SceneResult;
import dev.jchristoffersen.lightbox.scene.Transition;

public class EmptyTransition extends Transition {
    public EmptyTransition(Scene oldScene, Scene newScene) {
        super(oldScene, newScene);
    }

    public SceneResult getNextFrame() {
        return SceneResult.done(newScene.getNextFrame().frameBuffer(), newScene);
    }
}
