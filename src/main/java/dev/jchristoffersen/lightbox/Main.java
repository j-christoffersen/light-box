package dev.jchristoffersen.lightbox;

import dev.jchristoffersen.lightbox.led.LedMatrix;
import dev.jchristoffersen.lightbox.led.RaspberryPiLedMatrix;
import dev.jchristoffersen.lightbox.render.RenderLoop;
import dev.jchristoffersen.lightbox.scene.SceneManager;

public class Main {
    public static void main(String[] args) {
        SceneManager sceneManager = new SceneManager(scenes, transitions, 60);
        LedMatrix ledMatrix = new RaspberryPiLedMatrix();
        RenderLoop renderLoop = new RenderLoop(sceneManager, ledMatrix);
        renderLoop.start();
    }
}
