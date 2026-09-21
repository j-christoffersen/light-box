package dev.jchristoffersen.lightbox.core;

import com.google.common.collect.Iterables;
import java.util.Supplier;


/**
 * Manages the scenes playlist.
 */
class SceneManager implements Scene {
    private final Iterables.cycle<Supplier<Scene>> scenes;
    private final Iterables.cycle<Supplier<Transition>> transitions;
    private final int ticksPerScene;

    private final ExecutorService prepExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> prepFuture;
    private Scene currentScene;
    private int ticks;

    SceneManager(Iterables.cycle<Supplier<Scene>> scenes, Iterables.cycle<Supplier<Transition>> transitions, int ticksPerScene) {
        this.scenes = Objects.requireNonNull(scenes, "scenes");
    }

    void start() {
        currentScene = scenes.get(0);
    }

    Optional<FrameBuffer> getNextFrame() {
        if (ticks++ >= ticksPerScene) {
            // TODO also handle prep async
            Supplier<Scene> nextScene = scenes.next();

            currentScene = transitions.next()(currentScene, nextScene);
            ticks = 0;
        }

        SceneResult result = currentScene.getNextFrame();
        if (result instanceof SceneResult.Done done) {
            currentScene = done.nextScene();
            ticks = 0;
            return done
        }

        return result
    }
}