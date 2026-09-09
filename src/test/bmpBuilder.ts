import { LedMatrixInstance } from "rpi-led-matrix";
import { DummyMatrix } from "../DummyMatrix";
import { parseBdf } from "../utils/parseBdf";
import { TextDrawer } from "../utils/drawText";

parseBdf(`${process.cwd()}/node_modules/rpi-led-matrix/fonts/4x6.bdf`).then(font => {
    const textDrawer = new TextDrawer({

        bdf: font,
        matrix: {} as LedMatrixInstance,
        color: 0xffffff,
    });
    


    console.log(textDrawer.drawText('Hello', 0, 0));
});