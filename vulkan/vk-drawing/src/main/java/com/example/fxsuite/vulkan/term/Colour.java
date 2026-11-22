package com.example.fxsuite.vulkan.term;

/**
 * A colour in an expression: how much red, green and blue, each from nothing to one. Colours are mixed, lit and
 * added; a colour is never a place or a way, and the compiler refuses one where the other is wanted.
 */
public record Colour(Node node) {

    private static final double BRIGHTEST = 255;

    public Colour {
        if (node.size() != 3) {
            throw new IllegalArgumentException("a colour is three numbers, not " + node.size());
        }
    }

    /** The colour of so much {@code red}, {@code green} and {@code blue}, wherever a point is drawn. */
    public Colour(final double red, final double green, final double blue) {
        this(new Constant(new double[] {red, green, blue}));
    }

    /** The colour written as the engine writes its own: {@code #1e4a1c}. */
    public Colour(final String written) {
        this(new Hex(written).share(1), new Hex(written).share(3), new Hex(written).share(5));
    }

    /** This where {@code share} is nothing, {@code towards} where it is one, and between them between. */
    public Colour mixed(final Colour towards, final Real share) {
        return new Colour(three().mixed(towards.three(), share).node());
    }

    /** This and {@code other} together, as lights add. */
    public Colour plus(final Colour other) {
        return new Colour(three().plus(other.three()).node());
    }

    /** This as {@code light} lights it: each of red, green and blue as much as the light has of it. */
    public Colour lit(final Colour light) {
        return new Colour(three().times(light.three()).node());
    }

    /** {@code by} times as bright. */
    public Colour scaled(final Real by) {
        return new Colour(three().scaled(by).node());
    }

    /** How much of it there is, as one number: its red, green and blue, evened. */
    public Real evened() {
        return three().picked(Shaped.FIRST).plus(three().picked(Shaped.SECOND)).plus(three().picked(Shaped.THIRD))
                .over(3);
    }

    private Three three() {
        return new Three(node);
    }

    /** A colour as it is written, {@code #rrggbb}: each pair of its letters a share of the brightest. */
    private record Hex(String written) {

        private Hex {
            if (!written.matches("#[0-9a-fA-F]{6}")) {
                throw new IllegalArgumentException("a colour is written #rrggbb, not " + written);
            }
        }

        private double share(final int from) {
            return Integer.parseInt(written.substring(from, from + 2), 16) / BRIGHTEST;
        }
    }
}
