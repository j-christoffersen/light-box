package dev.jchristoffersen.lightbox.tools;

import dev.jchristoffersen.lightbox.text.ParsedBdf;

/**
 * Parse a BDF and inspect the glyphs
 */
public class ParseBdf {
    public static void main(String[] args) {
        ParsedBdf parsedBdf = ParsedBdf.parse("/fonts/5x7.bdf");
        System.out.println(parsedBdf.getGlyph((int) 'a'));
    }
}
