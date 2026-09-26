package dev.jchristoffersen.lightbox;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import dev.jchristoffersen.lightbox.render.WebServerMatrix;
import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scenes.ClockScene;
import dev.jchristoffersen.lightbox.scenes.GameOfLifeScene;
import dev.jchristoffersen.lightbox.scenes.PlasmaScene;
import dev.jchristoffersen.lightbox.scenes.StockScene;
import dev.jchristoffersen.lightbox.scenes.SurfScene;
import dev.jchristoffersen.lightbox.scenes.transitions.BarsTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.CircleMaskTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.EmptyTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.WipeTransition;
import dev.jchristoffersen.lightbox.render.LedMatrix;
import dev.jchristoffersen.lightbox.core.RenderLoop;
import dev.jchristoffersen.lightbox.core.SceneManager;

public class Local {
    public static void main(String[] args) throws IOException {
        SceneManager sceneManager = new SceneManager(List.<Supplier<Scene>>of(
            () -> new GameOfLifeScene(),
            () -> new PlasmaScene()
            // () -> new SurfScene("El Porto"),
            // () -> new ClockScene(),
            // () -> new StockScene("PYPL")
        ), List.of(
            (oldScene, newScene) -> new CircleMaskTransition(oldScene, newScene),
            (oldScene, newScene) -> new BarsTransition(oldScene, newScene),
            (oldScene, newScene) -> new WipeTransition(oldScene, newScene)
        ), 60 * 8); // 15 seconds per scene
        LedMatrix ledMatrix = new WebServerMatrix();
        RenderLoop renderLoop = new RenderLoop(sceneManager, ledMatrix);
        renderLoop.start();
    }
}