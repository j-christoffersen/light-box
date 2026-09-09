import { promises as fs } from 'fs';

export type ParsedBdf = {
    meta: Record<string, string>;
    glyphs: Record<string, Record<string, string[]>>;
};

export const parseBdf = async (path: string): Promise<ParsedBdf> => {
    const file = await fs.readFile(path, 'utf8');

    const font = {
        meta: {},
        glyphs: {},
    };

    let currentGlyph: string | null = null;
    let inBitmap = false;
    for (const line of file.split('\n')) {
        if (currentGlyph) {
            if (line.startsWith('ENDCHAR')) {
                currentGlyph = null;
                inBitmap = false;
            } else {
                const [keyOrBmpVal, ...values] = line.split(' ');
                if (keyOrBmpVal === 'BITMAP') {
                    inBitmap = true;
                    font.glyphs[currentGlyph].BITMAP = [];
                } else if (inBitmap) {
                    font.glyphs[currentGlyph].BITMAP.push(keyOrBmpVal);
                } else {
                    font.glyphs[currentGlyph][keyOrBmpVal] = values;
                }
            }
        } else {
            const [key, value] = line.split(' ');
            if (key === 'STARTCHAR') {
                currentGlyph = value;
                font.glyphs[currentGlyph] = {};
            } else {
                font.meta[key] = value;
            }
        }
    }

    return font;
}