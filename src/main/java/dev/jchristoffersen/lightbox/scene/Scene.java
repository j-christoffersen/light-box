package dev.jchristoffersen.lightbox.scene;

import java.util.concurrent.CompletableFuture;

public interface Scene {
    default CompletableFuture<Void> prep() { return CompletableFuture.completedFuture(null); }

    SceneResult getNextFrame();
}
