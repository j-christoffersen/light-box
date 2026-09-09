import { LedMatrixInstance } from "rpi-led-matrix";
import { ParsedBdf } from "./parseBdf";

/**
 * Draws text using a bmp file. Implemented on an as-needed basis, might not work for all chars or fonts.
 */

class TextDrawer {
    bdf: ParsedBdf;
    matrix: LedMatrixInstance;
    color: number = 0xffffff;
    kerning: number = 1;

    constructor({ bdf, matrix, color }: { bdf: ParsedBdf; matrix: LedMatrixInstance; color: number }) {
        this.bdf = bdf;
        this.matrix = matrix;
        this.color = color;
    }

    drawText(text: string, x: number, y: number) {
        const basisChar = this.bdf.glyphs[text[0]];
        const basisCharHeight = parseInt(basisChar.BBX[1]);

        const rows = {}
        for (let i = 0; i < basisCharHeight; i++) {
            rows[i] = [];
        }

        let minYOffset = 0;
        for (const char of text) {
            const glyph = this.bdf.glyphs[char];
            const glyphWidth = parseInt(glyph.BBX[0]);
            const glyphHeight = parseInt(glyph.BBX[1]);
            const glyphXOffset = parseInt(glyph.BBX[2]); // TODO handle
            const glyphYOffset = parseInt(glyph.BBX[3]);

            if (glyphYOffset < minYOffset) {
                minYOffset = glyphYOffset;
                // todo add new rows as needed
            }

            for (let i = 0; i < glyph.BITMAP.length; i++) {
                const targetRow = (rows[i + glyphYOffset] ??= []);

                console.log("hex is", glyph.BITMAP[i]);

                // copy bits into target row
                for (let j = 0; j < glyphWidth; j++) {
                    targetRow.push(parseInt(glyph.BITMAP[i], 16) & (1 << (7 - j)) ? true : false);
                }

                console.log("row is now",targetRow);
            }
        }

        return rows;
    }
}

// TODO new row needs to be filled with false values
// TODO any rows not in the for loop need to be filled with false values

export { TextDrawer };
