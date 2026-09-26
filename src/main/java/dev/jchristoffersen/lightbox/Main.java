package dev.jchristoffersen.lightbox;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scenes.ClockScene;
import dev.jchristoffersen.lightbox.scenes.GameOfLifeScene;
import dev.jchristoffersen.lightbox.scenes.PlasmaScene;
import dev.jchristoffersen.lightbox.scenes.StockScene;
import dev.jchristoffersen.lightbox.scenes.transitions.BarsTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.CircleMaskTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.EmptyTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.FadeTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.WipeTransition;
import dev.jchristoffersen.lightbox.render.LedMatrix;
import dev.jchristoffersen.lightbox.render.RaspberryPiLedMatrix;
import dev.jchristoffersen.lightbox.core.RenderLoop;
import dev.jchristoffersen.lightbox.core.SceneManager;

public class Main {
    public static void main(String[] args) throws IOException {
        SceneManager sceneManager = new SceneManager(List.<Supplier<Scene>>of(
            () -> new PlasmaScene(),
            () -> new ClockScene(),
            () -> new GameOfLifeScene(),
            () -> new StockScene("PYPL")
        ), List.of(
            (oldScene, newScene) -> new WipeTransition(oldScene, newScene),
            (oldScene, newScene) -> new FadeTransition(oldScene, newScene),
            (oldScene, newScene) -> new BarsTransition(oldScene, newScene),
            (oldScene, newScene) -> new CircleMaskTransition(oldScene, newScene)
        ), 60 * 30); // 30 seconds per scene
        LedMatrix ledMatrix = new RaspberryPiLedMatrix();
        RenderLoop renderLoop = new RenderLoop(sceneManager, ledMatrix);
        renderLoop.start();
    }
}