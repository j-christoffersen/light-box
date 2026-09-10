import { LedMatrixInstance } from "rpi-led-matrix";
import { DummyMatrix } from "../DummyMatrix";
import { parseBdf } from "../utils/parseBdf";
import { TextDrawer } from "../utils/TextDrawer";

parseBdf(`${process.cwd()}/node_modules/rpi-led-matrix/fonts/4x6.bdf`).then(font => {
    const textDrawer = new TextDrawer({

        bdf: font,
        matrix: {} as LedMatrixInstance,
        color: 0xffffff,
    });
    


    const { rows, minYOffset, basisCharHeight } = textDrawer.getTextBitmap('Hello')
    console.log(rows);
    for (let i = 0; i < basisCharHeight - minYOffset; i++) {
        console.log(rows[i].map(cel => cel ? '#' : '.').join(''));
    }


});