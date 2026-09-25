package dev.jchristoffersen.lightbox.scenes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import dev.jchristoffersen.lightbox.bmp.BitmapRenderer;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.StaticScene;
import dev.jchristoffersen.lightbox.scenes.apiClients.SurflineApiClient;

public class SurfScene extends StaticScene {
    private static Map<String, String> spotIdMap = Map.of(
        "El Porto", "5a25e409aa1aea001b27be39"
    );

    private String spotName;
    private SurflineApiClient surflineApiClient;
    private SurflineApiClient.SurflineData surflineData;
    private BufferedImage waveBmp;

    public SurfScene(String spotName) {
        this.spotName = spotName;
        this.surflineApiClient = new SurflineApiClient();
    }
    
    public CompletableFuture<Void> prep() {
        return CompletableFuture.supplyAsync(() -> surflineApiClient.getSurflineData(spotIdMap.get(spotName)))
        .thenApply((SurflineApiClient.SurflineData data) -> {
            this.surflineData = data;

            try {
                InputStream is = SurfScene.class.getResourceAsStream("/wave.bmp");
                if (is == null) {
                    throw new IOException("BMP resource not found: /assets/wave.bmp");
                }

                BufferedImage image = ImageIO.read(is);
                if (image == null) {
                    throw new IOException("Could not decode BMP: /assets/wave.bmp");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public FrameBuffer renderOnce() {
        FrameBuffer frameBuffer = new FrameBuffer(64, 32);

        BitmapRenderer.render(waveBmp, 32, 64, frameBuffer);
        return frameBuffer;
    }
}
