package dev.jchristoffersen.lightbox.scene;

import java.util.Objects;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public sealed interface SceneResult {
    FrameBuffer frameBuffer();
    SceneResult NO_UPDATE = new NoUpdate();
    static SceneResult noUpdate() { return NO_UPDATE; }
    static SceneResult updated(FrameBuffer buffer) { return new Updated(buffer); }
    static SceneResult done(FrameBuffer buffer, Scene nextScene) { return new Done(buffer, nextScene); }

    record NoUpdate() implements SceneResult {
        public FrameBuffer frameBuffer() {
            return null;
        }
    }
    record Updated(FrameBuffer buffer) implements SceneResult {
        public Updated {
            Objects.requireNonNull(buffer, "buffer");
        }

        public FrameBuffer frameBuffer() {
            return buffer;
        }
    }
    record Done(FrameBuffer buffer, Scene nextScene) implements SceneResult {
        public Done {
            Objects.requireNonNull(buffer, "buffer");
        }

        public FrameBuffer frameBuffer() {
            return buffer;
        }
    }
}