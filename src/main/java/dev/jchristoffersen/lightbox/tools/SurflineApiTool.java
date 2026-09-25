package dev.jchristoffersen.lightbox.tools;

import dev.jchristoffersen.lightbox.scenes.apiClients.SurflineApiClient;

// calls the surfline api and returns the data in a structured format
public class SurflineApiTool {
    public static void main(String[] args) {
        SurflineApiClient surflineApiClient = new SurflineApiClient();
        SurflineApiClient.SurflineData surfResponse = surflineApiClient.getSurflineData("1234567890");
        System.out.println(surfResponse);
    }
}
