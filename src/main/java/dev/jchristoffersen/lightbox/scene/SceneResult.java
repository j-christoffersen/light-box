package dev.jchristoffersen.lightbox.scene;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public sealed interface SceneResult {
    SceneResult NO_UPDATE = new NoUpdate();
    static SceneResult updated(FrameBuffer buffer) { return new Updated(buffer); }
    static SceneResult done(FrameBuffer buffer, Scene nextScene) { return new Done(buffer, nextScene); }

    record NoUpdate() implements SceneResult {}
    record Updated(FrameBuffer buffer) implements SceneResult {
        public Updated {
            Objects.requireNonNull(buffer, "buffer");
        }
    }
    record Done(FrameBuffer buffer, Scene nextScene) implements SceneResult {
        public Done {
            Objects.requireNonNull(buffer, "buffer");
        }
    }
}