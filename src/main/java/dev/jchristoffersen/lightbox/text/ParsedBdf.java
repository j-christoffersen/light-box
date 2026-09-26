package dev.jchristoffersen.lightbox.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
            List<String> allLines;
            InputStream is = ParsedBdf.class.getResourceAsStream(filePath);
            System.out.println("input stream: " + (is != null));
            System.out.println("filePath: " + filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            allLines = reader.lines().collect(Collectors.toList());


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
                        // a bit hacky but we don't need these meta values really
                        meta.put(words[0], words.length > 1 ? words[1] : "");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading file " + filePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return new ParsedBdf(glyphs);
    }
}
