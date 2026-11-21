package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * One thing a surface's colour is made of, laid over what is under it: its colour, how much of it shows at a point
 * (from nothing to one), and the smallest feature it adds, in centimetres. A stripe, a board's mark, a stain. What
 * it averages to over a thing is not said here: Java works that out ({@link Averaged}).
 *
 * <p>A term may belong to part of a surface only (a wall's bricks, over its stone footing): {@code within} is how
 * much of a point is that part, one all over for a term of the whole surface. What shows is the share within the
 * part; and what the term averages to is its average over its own part, not over the whole.
 */
public record Term(String name, Colour colour, Real share, double smallest, Real within) {

    public Term {
        if (!(smallest > 0)) {
            throw new IllegalArgumentException(name + " adds a feature of some size: " + smallest);
        }
    }

    /** A term of the whole surface. */
    public Term(final String name, final Colour colour, final Real share, final double smallest) {
        this(name, colour, share, smallest, new Real(1));
    }

    /** How much of it shows at a point: its share, in the part of the surface it belongs to. */
    public Real shown() {
        return whole() ? share : share.times(within);
    }

    /** The same, belonging only to {@code part} of what it belonged to. */
    public Term within(final Real part) {
        return new Term(name, colour, share, smallest, whole() ? part : within.times(part));
    }

    /** The same, showing {@code share} of itself everywhere: what it comes to, its features too small to see. */
    public Term evened(final double share) {
        return new Term(name, colour, new Real(share), smallest, within);
    }

    private boolean whole() {
        return within.node().equals(new Constant(List.of(1.0)));
    }
}
