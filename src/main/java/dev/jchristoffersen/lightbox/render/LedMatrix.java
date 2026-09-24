package dev.jchristoffersen.lightbox.render;

import java.util.concurrent.CompletableFuture;

public interface LedMatrix {

    void present(FrameBuffer buffer);

    default CompletableFuture<Void> ready() {
        return CompletableFuture.completedFuture(null);
    }
}
