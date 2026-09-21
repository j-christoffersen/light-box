package dev.jchristoffersen.lightbox.scene;

public interface Scene {
    default void prep() {}

    SceneResult getNextFrame() {}
}
