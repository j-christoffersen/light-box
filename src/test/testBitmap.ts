import { parseBdf } from "../utils/parseBdf";

parseBdf(`${process.cwd()}/node_modules/rpi-led-matrix/fonts/4x6.bdf`).then(font => {
    console.log(font.glyphs['a']);
});
