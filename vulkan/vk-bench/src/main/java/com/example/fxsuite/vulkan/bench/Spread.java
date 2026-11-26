package com.example.fxsuite.vulkan.bench;

import java.util.List;

/**
 * How a bench's two windows are spread over the desk's screens: where the picture goes, the card's window, and
 * where the console goes, JavaFX's. It is what is kept as a profile and read back the next time any bench opens.
 *
 * <p>With nothing kept, the usual: on a desk with a second screen, the picture as large as the first screen lets it
 * be and the console at the corner of the second; on a desk with one screen, both where the bench itself has
 * always put them, the console at the left and the picture beside it.
 *
 * @param picture where the card's window goes
 * @param console where JavaFX's window goes
 */
public record Spread(Seat picture, Seat console) {

    /** How far in from the corner of the second screen the console is put, where nothing is kept. */
    private static final int IN = 40;

    /**
     * The usual spread: on a desk with {@code two} screens, the picture as large as the first lets it be and the
     * console on the other; with one, the two as the bench itself puts them, {@code before}.
     */
    public Spread(final boolean two, final Spread before) {
        this(two ? new Seat(Seat.Which.FIRST, true, 0, 0, 0, 0) : before.picture(),
                two ? new Seat(Seat.Which.OTHER, IN, IN) : before.console());
    }

    /** It, as the lines of a kept profile. */
    public List<String> written() {
        return List.of("spread 1", "picture " + picture.written(), "console " + console.written());
    }
}
