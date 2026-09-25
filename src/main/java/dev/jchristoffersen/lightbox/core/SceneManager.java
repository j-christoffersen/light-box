package dev.jchristoffersen.lightbox.core;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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
public class SceneManager {
    private final Iterator<Supplier<Scene>> scenes;
    private final Iterator<BiFunction<Scene, Scene, Transition>> transitions;
    private final int ticksPerScene;

    private CompletableFuture<Void> prepFuture;
    private Scene currentScene;
    private Scene nextScene;
    private int ticks;

    public SceneManager(Iterable<Supplier<Scene>> scenes, Iterable<BiFunction<Scene, Scene, Transition>> transitions, int ticksPerScene) {
        this.scenes = Iterators.cycle(Objects.requireNonNull(scenes, "scenes"));
        this.transitions = Iterators.cycle(Objects.requireNonNull(transitions, "transitions"));
        this.ticksPerScene = ticksPerScene;
    }

    CompletableFuture<Void> start() {
        Supplier<Scene> sceneSupplier = scenes.next();
        currentScene = sceneSupplier.get();
        return currentScene.prep().exceptionally(e -> {
            System.err.println("Error prepping first scene");
            e.printStackTrace();
            return null;
        });
    }

    FrameBuffer getNextFrame() {
        if (ticks++ >= ticksPerScene && prepFuture == null) {
            Supplier<Scene> nextSceneSupplier = scenes.next();
            nextScene = nextSceneSupplier.get();

            prepFuture = nextScene.prep().exceptionally(e -> {
                System.err.println("Error prepping next scene");
                e.printStackTrace();
                return null;
            });
        }

        if (prepFuture != null && prepFuture.isDone()) {
            currentScene = transitions.next().apply(currentScene, nextScene);
            ticks = 0;
            prepFuture = null;
        }

        SceneResult sceneResult = currentScene.getNextFrame();;

        if (sceneResult instanceof SceneResult.Done done) {
            currentScene = done.nextScene();
            ticks = 0;
        }

        return sceneResult.frameBuffer();
    }
}