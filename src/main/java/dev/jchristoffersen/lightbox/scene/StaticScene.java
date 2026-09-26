package dev.jchristoffersen.lightbox.scene;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public abstract class StaticScene implements Scene {
    private boolean rendered = false;
    private FrameBuffer buffer;
    public SceneResult getNextFrame() {
        if (!rendered) {
            buffer = renderOnce();
            rendered = true;
            return SceneResult.updated(buffer);
        }

        return SceneResult.noUpdate(buffer);
    }

    protected abstract FrameBuffer renderOnce();
}