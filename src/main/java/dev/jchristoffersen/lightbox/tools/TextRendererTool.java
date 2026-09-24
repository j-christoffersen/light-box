package dev.jchristoffersen.lightbox.tools;

import dev.jchristoffersen.lightbox.text.ParsedBdf;
import dev.jchristoffersen.lightbox.text.TextRenderer;

/**
 * Render input to text to a bitmap and print to console
 */
public class TextRendererTool {
    public static void main(String[] args) {
        TextRenderer textRenderer = new TextRenderer(ParsedBdf.parse("/fonts/5x7.bdf"), 0xFFFFFF);
        textRenderer.renderTextToConsole("TWENTY");
        textRenderer.renderTextToConsole("PAST");
        textRenderer.renderTextToConsole("NINE");
    }
}
