package dev.jchristoffersen.lightbox.scene;

import java.util.Objects;

public abstract class Transition implements Scene {
    protected final Scene oldScene;
    protected final Scene newScene;

    protected Transition(Scene oldScene, Scene newScene) {
        this.oldScene = Objects.requireNonNull(oldScene, "oldScene");
        this.newScene = Objects.requireNonNull(newScene, "newScene");
    }

    public SceneResult getNextFrame() {
        return SceneResult.noUpdate();
    }
}
