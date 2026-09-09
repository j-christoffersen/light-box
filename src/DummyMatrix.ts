import fs from 'fs';
import { Color, FontInstance, LedMatrixInstance, MatrixOptions } from "rpi-led-matrix";

/**
 * LED matrix emulation that renders in terminal. For local testing.
 */

// note that the actual matrix doesn't have a defined FPS, ignoring dt in scenes may lead to unpredictable timing.
const FPS = 60;

class DummyMatrix implements LedMatrixInstance {
    private matrix: Color[][];
    private afterSyncCb?: (matrix: any, dt: number, t: number) => void;
    private tStart?: number;
    private tLast?: number;
    private frame = 0;
    private _bgColor: Color = { r: 0, g: 0, b: 0 };
    private _brightness: number = 100;
    private _fgColor: Color = { r: 0, g: 0, b: 0 };
    private emitFrameData: (matrix: number[][]) => void;

    constructor({
        cols,
        rows,
    }: Pick<MatrixOptions, 'cols' | 'rows'>, emitFrameData: (matrix: number[][]) => void) {
        this.matrix = new Array(cols).fill(0).map(() => new Array(rows).fill(0));
        this.emitFrameData = emitFrameData;
    }

    private static colorFromNumber(color: number): Color {
        return { r: (color >> 16) & 0xff, g: (color >> 8) & 0xff, b: color & 0xff };
    }

    afterSync(callback: (matrix: LedMatrixInstance, dt: number, t: number) => void) {
        this.afterSyncCb = callback;
        return this;
    }

    sync(): void {
        if (this.tStart === undefined) {
            this.tStart = Date.now();
        }

        // push data via websocket - convert Color to hex value
        this.emitFrameData(this.matrix.map(row => row.map(pixel => pixel.r << 16 | pixel.g << 8 | pixel.b)));
        
        // set up next sync call
        this.frame++;
        const tTarget = this.tStart + this.frame * 1000 / FPS;
        const dt = tTarget - (this.tLast ?? 0);
        this.tLast = tTarget;

        setTimeout(() => {
            this.afterSyncCb?.(this, dt, Math.floor(Date.now() - this.tStart!));
        }, dt);
    }

    bgColor(color: Color | number): this;
    bgColor(): Color;
    bgColor(color?: Color | number): any {
        if (color) {
        if (typeof color === 'number') {
            color = DummyMatrix.colorFromNumber(color);
        }
        this._bgColor = color;
        return this;
        }
        return this._bgColor;
    }

    brightness(brightness: number): this;
    brightness(): number;
    brightness(brightness?: number): any {
        if (brightness) {
        this._brightness = brightness;
        return this;
        }
        return this._brightness;
    }

    clear() {
        return this;
    }

    drawBuffer(buffer: Buffer | Uint8Array, w?: number, h?: number): this {
        return this;
    }

    drawCircle(x: number, y: number, r: number): this {
        return this;
    }

    drawLine(x0: number, y0: number, x1: number, y1: number): this {
        return this;
    }

    drawRect(x0: number, y0: number, width: number, height: number): this {
        return this;
    }
    drawText(text: string, x: number, y: number, kerning?: number): this {
        return this;
    }

    fgColor(color: Color | number): this;
    fgColor(): Color;
    fgColor(color?: Color | number): any {
        if (color !== undefined) {
            if (typeof color === 'number') {
                color = DummyMatrix.colorFromNumber(color);
            }
            this._fgColor = color;
            return this;
        }
        return this._fgColor;
    }

    fill(): this;
    fill(x0: number, y0: number, x1: number, y1: number): this;
    fill() {
        return this
    }

    font(font: FontInstance): this;
    font(): string;
    font(font?: FontInstance): any {
        return this;
    }

    getAvailablePixelMappers(): string[] {
        return [];
    }

    height(): number {
        return this.matrix.length;
    }

    luminanceCorrect(correct: boolean): this;
    luminanceCorrect(): boolean;
    luminanceCorrect(correct?: boolean): any {
        return this;
    }

    map(cb: (coords: [number, number, number], t: number) => number): this {
        return this;
    }

    pwmBits(pwmBits: number): this;
    pwmBits(): number;
    pwmBits(pwmBits?: number): any {
        return this;
    }

    setPixel(x: number, y: number): this {
        this.matrix[y][x] = this._fgColor;
        return this;
    }

    width(): number {
        return this.matrix[0].length;
    }
}

export { DummyMatrix };
