package dev.jchristoffersen.lightbox.text;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.jchristoffersen.lightbox.render.FrameBuffer;

public class TextRenderer {
    
    private ParsedBdf bdf;
    private byte[] color;
    
    public TextRenderer(ParsedBdf bdf, int color) {
        this.bdf = Objects.requireNonNull(bdf);
        this.color = new byte[] { (byte) (color >> 16), (byte) (color >> 8), (byte) color };
    }

    public void setColor(int color) {
        this.color = new byte[] { (byte) (color >> 16), (byte) (color >> 8), (byte) color };
    }
    
    record TextBitmap(int basisCharHeight, int minYOffset, long[] bitmap) {}
    private TextBitmap getTextBitmap(String text) {
        System.out.println("Getting text bitmap for: " + text);
        System.out.println("Text code points: " + Arrays.toString(text.codePoints().toArray()));
        System.out.println("A glyph" + this.bdf.getGlyph('T'));
        List<Glyph> glyphs = text.codePoints().mapToObj(this.bdf::getGlyph).collect(Collectors.toList());
        Glyph basisChar = glyphs.stream().findFirst().orElseThrow();
        int basisCharHeight = basisChar.bbxHeight;
        int minYOffset = glyphs.stream().mapToInt(glyph -> glyph.bbxYOffset).min().orElse(0);

        // a long is 64 bits, conviniently the width of out LED screen
        long[] bitmapBuilder = new long[basisCharHeight - minYOffset];

        int x = 0;
        for (int codePoint : text.codePoints().toArray()) {
            Glyph glyph = this.bdf.getGlyph(codePoint);
            int glyphWidth = glyph.bbxWidth;
            // TODO handle X offset
            
            // copy bits into target rows
            for (int y = 0; y < bitmapBuilder.length; y++) {
                if (y >= glyph.bitmap.length) {
                    continue;
                }
                
                // 63 -> all the way to the left, 7 -> width of the glyph bitmap - 1
                bitmapBuilder[y] |= (long) glyph.bitmap[y] << (63 - 7 - x);
            }
            
            x += glyphWidth;
        }

        return new TextBitmap(basisCharHeight, minYOffset, bitmapBuilder);
    }

    private long[] rekernBitmap(long[] bitmap) {
        long[] rekerned = new long[bitmap.length];
        int xOut = 0;
        boolean hasAddedSpace = true;
        for (int xIn = 0; xIn < 64; xIn++) {
            long mask = 1L << (63 - xIn);
            if(Arrays.stream(bitmap).noneMatch(row -> (mask & row) != 0L)) {
                if(hasAddedSpace) {
                    continue;
                }
                hasAddedSpace = true;
            } else {
                hasAddedSpace = false;
            }

            for (int y = 0; y < bitmap.length; y++) {
                if ((mask & bitmap[y]) != 0L) {
                    rekerned[y] |= 1L << (63 - xOut);
                }
            }
            xOut++;
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
        for (int dy = Math.max(0, -y); dy < textBitmap.basisCharHeight - textBitmap.minYOffset; dy++) { // TODO fix edge case y goes out fo bounds
            for (int dx = Math.max(0, -x); dx < Math.min(64, 64 - x); dx++) {
                if (((1L << (63 - dx)) & bitmap[dy]) != 0L) {
                    frameBuffer.setPixel(x + dx, y + dy, color);
                    System.out.println("Setting pixel at " + (x + dx) + ", " + (y + dy) + " to " + Arrays.toString(color));
                }
            }
        }
    }

    public void renderTextToConsole(String text) {
        renderTextToConsole(text, true);
    }
    public void renderTextToConsole(String text, boolean rekern) {
        TextBitmap textBitmap = getTextBitmap(text);
        final long[] bitmap = rekern ? rekernBitmap(textBitmap.bitmap) : textBitmap.bitmap;

        System.out.println(Long.toBinaryString(bitmap[3]));

        for (int y = 0; y < textBitmap.basisCharHeight - textBitmap.minYOffset; y++) {

            StringBuilder line = new StringBuilder();
            for (int x = 0; x < 64; x++) {
                line.append((bitmap[y] & (1L << (63 - x))) != 0L ? "█" : ".");
            }
            System.out.println(line);
            }
        System.out.println();
    }
}

