import { promises as fs } from 'fs';

export class ParsedBdf {
    constructor(public meta: Record<string, string>, private glyphs: Record<string, Record<string, string[]>>) {}

    getGlyph(glyph: string): Record<string, string[]> {
        const codePoint = glyph.codePointAt(0);
        if (!codePoint) {
            throw new Error(`Code point for glyph ${glyph} not found`);
        }
        
        const glyphData = this.glyphs[codePoint];
        if (!glyphData) {
            throw new Error(`Glyph ${glyph} not found`);
        }

        return glyphData;
    }
}

type GlyphData = any

export const parseBdf = async (path: string): Promise<ParsedBdf> => {
    const file = await fs.readFile(path, 'utf8');

    const meta = {};
    const glyphs = {};

    let currentGlyph: GlyphData | null = null;
    let inBitmap = false;
    for (const line of file.split('\n')) {
        if (currentGlyph) {
            if (line.startsWith('ENDCHAR')) {
                currentGlyph = null;
                inBitmap = false;
            }  else {
                const [keyOrBmpVal, ...values] = line.split(' ');
                if (keyOrBmpVal === 'BITMAP') {
                    inBitmap = true;
                    currentGlyph.BITMAP = [];
                } else if (inBitmap) {
                    currentGlyph.BITMAP.push(keyOrBmpVal);
                } else {
                    currentGlyph[keyOrBmpVal] = values;
                    if (keyOrBmpVal === 'ENCODING') {
                        glyphs[values[0]] = currentGlyph;
                    }
                }
            }
        } else {
            const [key, value] = line.split(' ');
            if (key === 'STARTCHAR') {
                currentGlyph = {};
            } else {
                meta[key] = value;
            }
        }
    }

    return new ParsedBdf(meta, glyphs);
}