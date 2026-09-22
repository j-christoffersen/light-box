package dev.jchristoffersen.lightbox.scenes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import dev.jchristoffersen.lightbox.constants.Constants;
import dev.jchristoffersen.lightbox.render.FrameBuffer;
import dev.jchristoffersen.lightbox.scene.StaticScene;
import dev.jchristoffersen.lightbox.text.ParsedBdf;
import dev.jchristoffersen.lightbox.text.TextRenderer;

public class ClockScene extends StaticScene {
    private static final Map<Integer, String> hoursWords = Map.ofEntries(
        Map.entry(0, "MIDNIGHT"),
        Map.entry(1, "ONE"),
        Map.entry(2, "TWO"),
        Map.entry(3, "THREE"),
        Map.entry(4, "FOUR"),
        Map.entry(5, "FIVE"),
        Map.entry(6, "SIX"),
        Map.entry(7, "SEVEN"),
        Map.entry(8, "EIGHT"),
        Map.entry(9, "NINE"),
        Map.entry(10, "TEN"),
        Map.entry(11, "ELEVEN"),
        Map.entry(12, "NOON"),
        Map.entry(13, "ONE"),
        Map.entry(14, "TWO"),
        Map.entry(15, "THREE"),
        Map.entry(16, "FOUR"),
        Map.entry(17, "FIVE"),
        Map.entry(18, "SIX"),
        Map.entry(19, "SEVEN"),
        Map.entry(20, "EIGHT"),
        Map.entry(21, "NINE"),
        Map.entry(22, "TEN"),
        Map.entry(23, "ELEVEN")
    );

    private static final Map<Integer, String> minutesWords = Map.ofEntries(
        Map.entry(5, "FIVE PAST"),
        Map.entry(10, "TEN PAST"),
        Map.entry(15, "QUARTER PAST"),
        Map.entry(20, "TWENTY PAST"),
        Map.entry(25, "TWENTY-FIVE PAST"),
        Map.entry(30, "HALF PAST"),
        Map.entry(35, "TWENTY-FIVE TILL"),
        Map.entry(40, "TWENTY TILL"),
        Map.entry(45, "QUARTER TILL"),
        Map.entry(50, "TEN TILL"),
        Map.entry(55, "FIVE TILL")
    );

    private ParsedBdf parsedBdf;

    public CompletableFuture<Void> prep() {
        return CompletableFuture.runAsync(() -> {
            this.parsedBdf = ParsedBdf.parse("TODO");
        });
    }

    public FrameBuffer renderOnce() {
        FrameBuffer frameBuffer = new FrameBuffer(Constants.WIDTH, Constants.HEIGHT);

        int hours = LocalDateTime.now().getHour();
        int minutes = LocalDateTime.now().getMinute();
        int minutesRounded = ((int) Math.round((double) minutes / 5) * 5) % 60;

        // render up to three words, each in their own row
        // by default render the hour on the middle line
        String one = "";
        String two = hoursWords.get(hours);
        String three = "";

        // if there are minutes, render the minutes words as well
        if (minutesRounded > 0) {
            String[] minutesWordsArray = minutesWords.get(minutesRounded).split("\s");
            one = minutesWordsArray[0];
            two = minutesWordsArray[1];
            if (minutesRounded <= 30) {
                three = hoursWords.get(hours);
            } else {
                three = hoursWords.get(hours + 1);
            }
        }

        TextRenderer textRenderer = new TextRenderer(parsedBdf, 0xffffff);
        textRenderer.renderTextToFrameBuffer(one, 0, -1, frameBuffer);
        textRenderer.renderTextToFrameBuffer(two, 0, 7, frameBuffer);
        textRenderer.renderTextToFrameBuffer(three, 0, 14, frameBuffer);

        return frameBuffer;
    }
}
