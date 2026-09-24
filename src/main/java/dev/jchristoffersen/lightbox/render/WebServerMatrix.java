package dev.jchristoffersen.lightbox.render;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;

import com.sun.net.httpserver.HttpServer;
import com.socketio4j.socketio.Configuration;
import com.socketio4j.socketio.SocketIOServer;

public class WebServerMatrix implements LedMatrix {
    private final HttpServer httpServer;
    private final SocketIOServer socketServer;

    public WebServerMatrix() throws IOException {
        // Create the HTTP server
        httpServer = HttpServer.create(new InetSocketAddress(3000), 0);
        httpServer.createContext("/", exchange -> {
            try (InputStream responseStream = WebServerMatrix.class.getResourceAsStream("/index.html")) {
                if (responseStream != null) {
                    byte[] bytes = responseStream.readAllBytes();
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } else {
                    byte[] errorResponse = "404 - index.html file not found in resources folder.".getBytes();
                    exchange.sendResponseHeaders(404, errorResponse.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(errorResponse);
                    }
                }
            }
        });
        httpServer.start();
        System.out.println("HTTP server successfully running on http://localhost:3000");

        // Start the socket server
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
        config.setOrigin("http://localhost:3000");
        socketServer = new SocketIOServer(config);
        socketServer.start();
        System.out.println("Server successfully running on http://localhost:3000");

        openBrowser("http://localhost:3000");
    }

    // OS independent cross-platform utility to open browser windows
    private static void openBrowser(String url) {
        System.out.println("Opening browser to " + url);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                return;
            } catch (Exception e) {
                // Fallback to runtime execution if AWT Desktop fails
            }
        }
        
        // Manual OS command execution fallback
        Runtime runtime = Runtime.getRuntime();
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("mac")) {
                runtime.exec(new String[]{"open", url});
            } else if (os.contains("win")) {
                runtime.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else if (os.contains("nix") || os.contains("nux")) {
                runtime.exec(new String[]{"xdg-open", url});
            }
        } catch (IOException e) {
            System.out.println("Could not launch browser automatically: " + e.getMessage());
        }
    }

    public void present(FrameBuffer buffer) {
        System.out.println("Presenting frame");
        // System.out.println(Arrays.toString(buffer.pixels));
        String encoded = Base64.getEncoder().encodeToString(buffer.pixels);
        socketServer.getBroadcastOperations().sendEvent("frame", encoded);
    }
}
