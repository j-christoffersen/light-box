package dev.jchristoffersen.lightbox.text;

import java.util.HashMap;

public class Glyph {
    private string title;
    private int encoding;
    private int swidth;
    private int dwidth;
    private int bbxWidth;
    private int bbxHeight;
    private int bbxXOffset;
    private int bbxYOffset;
    private int[] bitmap;

    Glyph(Builder builder) {
        this.title = builder.title;
        this.encoding = builder.encoding;
        this.swidth = builder.swidth;
        this.dwidth = builder.dwidth;
        this.bbxWidth = builder.bbxWidth;
        this.bbxHeight = builder.bbxHeight;
        this.bbxXOffset = builder.bbxXOffset;
        this.bbxYOffset = builder.bbxYOffset;
        this.bitmap = builder.bitmap;
    }

    public static final class Builder {
        private string title;
        private int encoding;
        private int swidth;
        private int dwidth;
        private int bbxWidth;
        private int bbxHeight;
        private int bbxXOffset;
        private int bbxYOffset;
        private int[] bitmap;

        public Builder title(string title) {
            this.title = title;
            return this;
        }

        public Builder encoding(int encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder swidth(int swidth) {
            this.swidth = swidth;
            return this;
        }
        
        public Builder dwidth(int dwidth) {
            this.dwidth = dwidth;
            return this;
        }

        public Builder bbxWidth(int bbxWidth) {
            this.bbxWidth = bbxWidth;
            return this;
        }

        public Builder bbxHeight(int bbxHeight) {
            this.bbxHeight = bbxHeight;
            return this;
        }
        
        public Builder bbxXOffset(int bbxXOffset) {
            this.bbxXOffset = bbxXOffset;
            return this;
        }

        public Builder bbxYOffset(int bbxYOffset) {
            this.bbxYOffset = bbxYOffset;
            return this;
        }
        
        public Builder bitmap(int[] bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public Glyph build() {
            return new Glyph(this);
        }
    }
}

public class ParsedBdf {
    private HashMap<Integer, Glyph> glyphs;

    private ParsedBdf(HashMap<Integer, Glyph> glyphs) {
        this.glyphs = glyphs;
    }

    public Glyph getGlyph(int encoding) {
        Glyph glyph = this.glyphs.get(encoding);
        if (glyph == null) {
            throw new IllegalArgumentException(`Glyph with encoding ${encoding} not found`);
        }
        return glyph;
    }

    public static CompletableFuture<ParsedBdf> parse(string filePath) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<Integer, Glyph> glyphs = new HashMap<>();
            HashMap<String, String> meta = new HashMap<>();
            Builder currentGlyph = null;
            boolean inBitmap = false;

            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(line -> {
                    String[] words = line.split("\\s+");
                    if (currentGlyph != null) {
                        if (words[0].equals("ENDCHAR")) {
                            glyphs.put(currentGlyph.getEncoding(), currentGlyph.build());
                            currentGlyph = null;
                            inBitmap = false;
                        } else if (words[0].equals("BITMAP")) {
                            inBitmap = true;
                            currentGlyph.setBitmap(new ArrayList<>());
                        } else if (inBitmap) {
                            currentGlyph.getBitmap().add(words[0]);
                        } else {
                            switch (words[0]) {
                                case "ENCODING":
                                    currentGlyph.setEncoding(Integer.parseInt(words[1]));
                                    break;
                                case "SWIDTH":
                                    currentGlyph.setSwidth(Integer.parseInt(words[1]));
                                    break;
                                case "DWIDTH":
                                    currentGlyph.setDwidth(Integer.parseInt(words[1]));
                                    break;
                                case "BBX":
                                    currentGlyph.setBbxWidth(Integer.parseInt(words[1]));
                                    currentGlyph.setBbxHeight(Integer.parseInt(words[2]));
                                    currentGlyph.setBbxXOffset(Integer.parseInt(words[3]));
                                    currentGlyph.setBbxYOffset(Integer.parseInt(words[4]));
                                    break;
                            }
                        }
                    } else {
                        if (words[0].equals("STARTCHAR")) {
                            currentGlyph = new Glyph.Builder();
                            currentGlyph.setTitle(words[1]);
                        } else {
                            meta.put(words[0], words[1]);
                        }
                    }
                });
            }
        })
    }
}
