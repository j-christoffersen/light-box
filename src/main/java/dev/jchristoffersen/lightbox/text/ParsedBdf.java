package dev.jchristoffersen.lightbox.text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParsedBdf {
    private HashMap<Integer, Glyph> glyphs;

    private ParsedBdf(HashMap<Integer, Glyph> glyphs) {
        this.glyphs = glyphs;
    }

    public Glyph getGlyph(int encoding) {
        Glyph glyph = this.glyphs.get(encoding);
        if (glyph == null) {
            throw new IllegalArgumentException("Glyph with encoding " + encoding + " not found");
        }
        return glyph;
    }

    public static ParsedBdf parse(String filePath) {
        HashMap<Integer, Glyph> glyphs = new HashMap<>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get(filePath));
            HashMap<String, String> meta = new HashMap<>();
            Glyph.Builder glyphBuilder = null;
            boolean inBitmap = false;

            for (String line : allLines) {
                String[] words = line.split("\\s+");
                if (glyphBuilder != null) {
                    if (words[0].equals("ENDCHAR")) {
                        glyphs.put(glyphBuilder.encoding, glyphBuilder.build());
                        glyphBuilder = null;
                        inBitmap = false;
                    } else if (words[0].equals("BITMAP")) {
                        inBitmap = true;
                        glyphBuilder.setBitmap(new ArrayList<>());
                    } else if (inBitmap) {
                        glyphBuilder.getBitmap().add(Integer.parseInt(words[0], 16));
                    } else {
                        switch (words[0]) {
                            case "ENCODING":
                                glyphBuilder.encoding = Integer.parseInt(words[1]);
                                break;
                            case "SWIDTH":
                                glyphBuilder.swidth = Integer.parseInt(words[1]);
                                break;
                            case "DWIDTH":
                                glyphBuilder.dwidth = Integer.parseInt(words[1]);
                                break;
                            case "BBX":
                                glyphBuilder.bbxWidth = Integer.parseInt(words[1]);
                                glyphBuilder.bbxHeight = Integer.parseInt(words[2]);
                                glyphBuilder.bbxXOffset = Integer.parseInt(words[3]);
                                glyphBuilder.bbxYOffset = Integer.parseInt(words[4]);
                                break;
                        }
                    }
                } else {
                    if (words[0].equals("STARTCHAR")) {
                        glyphBuilder = new Glyph.Builder();
                        glyphBuilder.title = words[1];
                    } else {
                        meta.put(words[0], words[1]);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file " + filePath, e);
        }

        return new ParsedBdf(glyphs);
    }
}
