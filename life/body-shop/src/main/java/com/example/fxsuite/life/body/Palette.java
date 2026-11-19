package com.example.fxsuite.life.body;

import java.util.EnumMap;
import java.util.Map;

/**
 * The colours a body is drawn in: each paint's own ({@link Paint#colour}) but where it is changed. Made afresh for
 * every change, never changed.
 */
public final class Palette {

    /** Every paint its own colour. */
    public static final Palette DEFAULT = new Palette(new EnumMap<>(Paint.class));

    private final Map<Paint, String> changed;

    private Palette(final Map<Paint, String> changed) {
        this.changed = changed;
    }

    /** {@code paint}'s colour, as a web colour. */
    public String colour(final Paint paint) {
        return changed.getOrDefault(paint, paint.colour());
    }

    /** This palette with {@code paint} coloured {@code colour} (a web colour). */
    public Palette with(final Paint paint, final String colour) {
        final Map<Paint, String> c = new EnumMap<>(Paint.class);
        c.putAll(changed);
        c.put(paint, colour);
        return new Palette(c);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Palette p && changed.equals(p.changed);
    }

    @Override
    public int hashCode() {
        return changed.hashCode();
    }
}
