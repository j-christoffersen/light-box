import _ from 'lodash';
import { LedMatrixInstance } from "rpi-led-matrix";
import { ParsedBdf } from "./parseBdf";

type TextBitmap = { basisCharHeight: number; minYOffset: number; rows: { [key: number]: boolean[] } }

/**
 * Draws text using a bmp file. Implemented on an as-needed basis, might not work for all chars or fonts.
 */
class TextDrawer {
    bdf: ParsedBdf;
    matrix: LedMatrixInstance;
    color: number = 0xffffff;

    constructor({ bdf, matrix, color }: { bdf: ParsedBdf; matrix: LedMatrixInstance; color: number }) {
        this.bdf = bdf;
        this.matrix = matrix;
        this.color = color;
    }

    setColor(color: number) {
        this.color = color;
    }

    getTextBitmap(text: string): TextBitmap {
        const basisChar = this.bdf.getGlyph(text[0]);
        const basisCharHeight = parseInt(basisChar.BBX[1]);

        const rows = {}
        for (let i = 0; i < basisCharHeight; i++) {
            rows[i] = [];
        }

        let minYOffset = 0;
        for (const char of text) {
            const glyph = this.bdf.getGlyph(char);
            const glyphWidth = parseInt(glyph.BBX[0]);
            const glyphHeight = parseInt(glyph.BBX[1]);
            const glyphXOffset = parseInt(glyph.BBX[2]); // TODO handle
            const glyphYOffset = parseInt(glyph.BBX[3]);

            if (glyphYOffset < minYOffset) {
                // columns in new rows should be empty
                // note: Y corrdiantes reverse so e.g. a -1 offset with height 6 means a 7th row is needed
                for (let i = basisCharHeight - minYOffset; i < basisCharHeight - glyphYOffset; i++) {
                    rows[i] = Array(rows[0].length).fill(false);
                }
                minYOffset = glyphYOffset;
            }

            for (let i = 0; i < basisCharHeight - minYOffset; i++) {

                // copy bits into target row
                for (let j = 0; j < glyphWidth; j++) {
                    rows[i].push(parseInt(glyph.BITMAP[i + glyphYOffset] ?? 0x00, 16) & (1 << (7 - j)) ? true : false);
                }
            }
        }

        return { rows, minYOffset, basisCharHeight };
    }

    private printBitmap({ basisCharHeight, minYOffset, rows }: TextBitmap) {
        for (let i = 0; i < basisCharHeight - minYOffset; i++) {
            console.log(rows[i].map(cel => cel ? '#' : '.').join(''));
        }    
    }

    drawText(text: string, x: number, y: number, rekern = true) {
        let { basisCharHeight, minYOffset, rows } = this.getTextBitmap(text);

        // if specified, redo kerning to be even
        if (rekern) {
            const indexesToRemove: number[] = [];
            let seenSpace = true;
            const rowValues = Object.values(rows);
            for (let i = 0; i < rows[0].length; i++) {
                if (rowValues.every(row => row[i] === false)) {
                    if (seenSpace) {
                        indexesToRemove.push(i);
                    }
                    seenSpace = true;
                } else {
                    seenSpace = false;
                }
            }

            const lastIndex = rows[0].length - 1;
            if (rowValues.every(row => row[lastIndex] === false)) {
                indexesToRemove.push(lastIndex);
            }

            rows = _.mapValues(rows, (row) => row.filter((_, i) => !indexesToRemove.includes(i)));
        }

        this.matrix.fgColor(this.color);
        for (let i = 0; i < basisCharHeight - minYOffset; i++) {
            for (let j = 0; j < rows[i].length; j++) {
                if (rows[i][j]) {
                    this.matrix.setPixel(x + j, y + i);
                }
            }
        }
    }

    width(text: string) {
        const { rows, minYOffset, basisCharHeight } = this.getTextBitmap(text);
        return rows[0].length;
    }
}

export { TextDrawer };
