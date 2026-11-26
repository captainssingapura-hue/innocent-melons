package com.example.fxsuite.vulkan.bench;

/**
 * Where one of a bench's windows goes: which of the desk's screens it is on, whether it is as large as that screen
 * lets it be, and otherwise how far its corner is from the corner of that screen's part for windows and how big it
 * is, all as the screen measures its own windows. A size of nothing is the window's own: whatever it would be made.
 *
 * @param on        which screen it is on
 * @param maximized whether it is as large as its screen lets it be
 * @param x         how far across its screen its corner is
 * @param y         how far down its screen its corner is
 * @param width     how wide it is; nothing, and it is as wide as it would be made
 * @param height    how high it is; nothing, and it is as high as it would be made
 */
public record Seat(Which on, boolean maximized, int x, int y, int width, int height) {

    /** Which of the desk's screens a window is on. */
    public enum Which {

        /** The desk's first screen. */
        FIRST,
        /** Another screen, the next after the first; the first, on a desk with one. */
        OTHER
    }

    /** A seat on a screen at so far across and down it, the window as big as it would be made. */
    public Seat(final Which on, final int x, final int y) {
        this(on, false, x, y, 0, 0);
    }

    /** It, as a line of a kept spread: {@code on=first maximized=true x=0 y=0 width=0 height=0}. */
    public String written() {
        return "on=" + on.name().toLowerCase(java.util.Locale.ROOT) + " maximized=" + maximized + " x=" + x + " y="
                + y + " width=" + width + " height=" + height;
    }
}
