package dev.jchristoffersen.lightbox.text;

import java.util.ArrayList;

public class Glyph {
    public String title;
    public int encoding;
    public int swidth;
    public int dwidth;
    public int bbxWidth;
    public int bbxHeight;
    public int bbxXOffset;
    public int bbxYOffset;
    public int[] bitmap;

    Glyph(Builder builder) {
        this.title = builder.title;
        this.encoding = builder.encoding;
        this.swidth = builder.swidth;
        this.dwidth = builder.dwidth;
        this.bbxWidth = builder.bbxWidth;
        this.bbxHeight = builder.bbxHeight;
        this.bbxXOffset = builder.bbxXOffset;
        this.bbxYOffset = builder.bbxYOffset;
        this.bitmap = builder.bitmap.stream().mapToInt(Integer::intValue).toArray();
    }

    public static final class Builder {
        public String title;
        public int encoding;
        public int swidth;
        public int dwidth;
        public int bbxWidth;
        public int bbxHeight;
        public int bbxXOffset;
        public int bbxYOffset;
        private ArrayList<Integer> bitmap;

        public Builder title(String title) {
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
        
        public Builder setBitmap(ArrayList<Integer> bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public ArrayList<Integer> getBitmap() {
            return bitmap;
        }

        public Glyph build() {
            return new Glyph(this);
        }
    }
}
