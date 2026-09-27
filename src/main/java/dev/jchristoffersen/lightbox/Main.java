package dev.jchristoffersen.lightbox;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import dev.jchristoffersen.lightbox.scene.Scene;
import dev.jchristoffersen.lightbox.scenes.ClockScene;
import dev.jchristoffersen.lightbox.scenes.GameOfLifeScene;
import dev.jchristoffersen.lightbox.scenes.PlasmaScene;
import dev.jchristoffersen.lightbox.scenes.StockScene;
import dev.jchristoffersen.lightbox.scenes.apiClients.YahooApiClient;
import dev.jchristoffersen.lightbox.scenes.transitions.BarsTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.CircleMaskTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.FadeTransition;
import dev.jchristoffersen.lightbox.scenes.transitions.WipeTransition;
import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.LedMatrix;
import dev.jchristoffersen.lightbox.render.LedMatrixNative;
import dev.jchristoffersen.lightbox.render.RaspberryPiLedMatrix;
import dev.jchristoffersen.lightbox.core.RenderLoop;
import dev.jchristoffersen.lightbox.core.SceneManager;

public class Main {
    private static final String RGB_MATRIX_LIBRARY_PATH = "/home/jackson/code/rpi-rgb-led-matrix/lib/librgbmatrix.so.1";

    public static void main(String[] args) throws IOException {
        Clock clock = Clock.systemDefaultZone();
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        YahooApiClient yahooApiClient = new YahooApiClient(httpClient);

        SceneManager sceneManager = new SceneManager(List.<Supplier<Scene>>of(
            () -> new PlasmaScene(),
            () -> new StockScene("PYPL", yahooApiClient),
            () -> new GameOfLifeScene(),
            () -> new ClockScene(clock)
        ), List.of(
            (oldScene, newScene) -> new WipeTransition(oldScene, newScene),
            (oldScene, newScene) -> new FadeTransition(oldScene, newScene),
            (oldScene, newScene) -> new BarsTransition(oldScene, newScene),
            (oldScene, newScene) -> new CircleMaskTransition(oldScene, newScene)
        ), 60 * 10);
        LedMatrix ledMatrix = new RaspberryPiLedMatrix(
            LedMatrixNative.load(RGB_MATRIX_LIBRARY_PATH),
            new RaspberryPiLedMatrix.Config(Constants.HEIGHT, Constants.WIDTH, 4, "jackson", "jackson"));
        RenderLoop renderLoop = new RenderLoop(sceneManager, ledMatrix);
        renderLoop.start();
    }
}