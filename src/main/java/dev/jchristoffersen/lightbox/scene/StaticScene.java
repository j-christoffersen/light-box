package dev.jchristoffersen.lightbox.scene;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public abstract class StaticScene implements Scene {
    private boolean rendered = false;
    public SceneResult getNextFrame() {
        if (!rendered) {
            FrameBuffer buffer = renderOnce();
            rendered = true;
            return SceneResult.updated(buffer);
        }

        return SceneResult.noUpdate();
    }

    protected abstract FrameBuffer renderOnce();
}