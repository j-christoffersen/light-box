package dev.jchristoffersen.lightbox.core;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import com.google.common.collect.Iterators;

import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scene.SceneResult;
import dev.jchristoffersen.lightbox.scene.Transition;

/**
 * Manages the scenes playlist.
 */
class SceneManager {
    private final Iterator<Supplier<Scene>> scenes;
    private final Iterator<BiFunction<Scene, Scene, Transition>> transitions;
    private final int ticksPerScene;

    private final ExecutorService prepExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> prepFuture;
    private Scene currentScene;
    private int ticks;

    SceneManager(Iterable<Supplier<Scene>> scenes, Iterable<BiFunction<Scene, Scene, Transition>> transitions, int ticksPerScene) {
        this.scenes = Iterators.cycle(Objects.requireNonNull(scenes, "scenes"));
        this.transitions = Iterators.cycle(Objects.requireNonNull(transitions, "transitions"));
        this.ticksPerScene = ticksPerScene;
    }

    void start() {
        Supplier<Scene> sceneSupplier = scenes.next();
        currentScene = sceneSupplier.get();
        // TODO call prep
    }

    FrameBuffer getNextFrame() {
        if (ticks++ >= ticksPerScene) {
            // TODO also handle prep async
            Supplier<Scene> nextScene = scenes.next();

            currentScene = transitions.next().apply(currentScene, nextScene.get());
            ticks = 0;
        }

        SceneResult sceneResult = currentScene.getNextFrame();
        if (sceneResult instanceof SceneResult.Done done) {
            currentScene = done.nextScene();
            ticks = 0;
        }

        return sceneResult.frameBuffer();
    }
}