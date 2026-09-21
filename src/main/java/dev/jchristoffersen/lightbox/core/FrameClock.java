package dev.jchristoffersen.lightbox.core;

import java.util.concurrent.locks.LockSupport;

public class FrameClock {
    private final long frameNanos;
    private final Runnable onTick;
    private volatile boolean running = true;

    public FrameClock(int targetFps, Runnable onTick) {
        this.frameNanos = 1_000_000_000L / targetFps;
        this.onTick = onTick;
    }

    public void start() {
        long next = System.nanoTime();
        while (true) {
            onTick.run();

            next += frameNanos;
            long sleepNanos = next - System.nanoTime();
            if (sleepNanos > 0) {
                LockSupport.parkNanos(sleepNanos);
            } else {
                // A tick took longer than the frame budget (e.g. a scene did
                // something slow). Resync to now and keep going at
                // the target rate from here.
                next = System.nanoTime();
            }
        }
    }
}