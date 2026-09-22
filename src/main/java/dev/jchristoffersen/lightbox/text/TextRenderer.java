package dev.jchristoffersen.lightbox.text;

import java.util.Arrays;
import java.util.stream.Stream;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public class TextRenderer {
    
    private ParsedBdf bdf;
    private byte[] color;
    
    public TextRenderer(ParsedBdf bdf, int color) {
        this.bdf = bdf;
        this.color = new byte[] { (byte) (color >> 16), (byte) (color >> 8), (byte) color };
    }
    
    record TextBitmap(int basisCharHeight, int minYOffset, long[] bitmap) {}
    private TextBitmap getTextBitmap(String text) {
        
        Stream<Glyph> glyphs = text.codePoints().mapToObj(this.bdf::getGlyph);
        Glyph basisChar = glyphs.findFirst().orElseThrow();
        int basisCharHeight = basisChar.bbxHeight;
        int minYOffset = glyphs.mapToInt(glyph -> glyph.bbxYOffset).min().orElse(0);

        // a long is 64 bits, conviniently the width of out LED screen
        long[] bitmapBuilder = new long[basisCharHeight - minYOffset];

        int x = 0;
        for (int codePoint : text.codePoints().toArray()) {
            Glyph glyph = this.bdf.getGlyph(codePoint);
            int glyphWidth = glyph.bbxWidth;
            // TODO handle X offset
            
            // copy bits into target rows
            for (int y = 0; y < bitmapBuilder.length; y++) {
                bitmapBuilder[y] |= glyph.bitmap[y] << (63 - x);
            }

            x += glyphWidth;
        }

        return new TextBitmap(basisCharHeight, minYOffset, bitmapBuilder);
    }

    private long[] rekernBitmap(long[] bitmap) {
        long[] rekerned = new long[bitmap.length];
        int xOut = 0;
        for (int xIn = 0; xIn < 64; xIn++) {
            long mask = 1L << (63 - xIn);
            if(Arrays.stream(bitmap).anyMatch(row -> (mask & row) != 0L)) {
                for (int y = 0; y < bitmap.length; y++) {
                    if ((mask & bitmap[y]) != 0L) {
                        rekerned[y] |= 1L << (63 - xOut);
                    }
                }
                xOut++;
            }
        }

        return rekerned;
    }

    public void renderTextToFrameBuffer(String text, int x, int y, FrameBuffer frameBuffer) {
        renderTextToFrameBuffer(text, x, y, frameBuffer, true);
    }

    public void renderTextToFrameBuffer(String text, int x, int y, FrameBuffer frameBuffer, boolean rekern) {
        TextBitmap textBitmap = getTextBitmap(text);
        final long[] bitmap = rekern ? rekernBitmap(textBitmap.bitmap) : textBitmap.bitmap;

        // render from top left corner of text
        for (int dy = 0; dy < textBitmap.basisCharHeight - textBitmap.minYOffset; dy++) {
            for (int dx = 0; dx < 64; dx++) {
                if (((1L << (63 - dx)) & bitmap[dy]) != 0L) {
                    frameBuffer.setPixel(x + dx, y + dy, color);
                }
            }
        }
    }
}
