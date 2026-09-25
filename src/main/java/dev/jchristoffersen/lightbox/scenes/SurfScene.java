package dev.jchristoffersen.lightbox.scenes;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    public SurfScene(String spotName) {
        this.spotName = spotName;
        this.surflineApiClient = new SurflineApiClient();
    }
    
    public CompletableFuture<Void> prep() {
        return surflineApiClient.getSurflineData(spotIdMap.get(spotName))
        .thenApply((SurflineApiClient.SurflineData data) -> {
            this.surflineData = data;
            return null;
        });
    }

    public FrameBuffer renderOnce() {
        
    }
}
