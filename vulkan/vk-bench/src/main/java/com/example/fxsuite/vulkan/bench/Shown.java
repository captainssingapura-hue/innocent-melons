package com.example.fxsuite.vulkan.bench;

import javafx.application.Platform;

/**
 * A walk's JavaFX window for as long as the walk lasts. A walk opened by itself begins JavaFX to show it and ends
 * JavaFX when it closes. A walk opened beside a bench whose JavaFX is already running shows its window on that, and
 * closing only takes its own window away, the bench's being left as they are.
 */
public final class Shown implements AutoCloseable {

    private final Guide guide;
    private final boolean beside;

    /** {@code guide} shown with its top left corner {@code x} across the screen and {@code y} down. */
    /** {@code guide} shown where {@code arranged} puts a console, beside a JavaFX already running or not. */
    public Shown(final Guide guide, final boolean beside, final Arranged arranged) {
        this.guide = guide;
        this.beside = beside;
        if (beside) {
            Platform.runLater(() -> guide.shown(arranged));
        } else {
            Platform.startup(() -> guide.shown(arranged));
        }
    }

    public Shown(final Guide guide, final boolean beside, final double x, final double y) {
        this.guide = guide;
        this.beside = beside;
        if (beside) {
            Platform.runLater(() -> guide.shown(x, y));
        } else {
            Platform.startup(() -> guide.shown(x, y));
        }
    }

    @Override
    public void close() {
        if (beside) {
            Platform.runLater(guide::hidden);
        } else {
            Platform.exit();
        }
    }
}
