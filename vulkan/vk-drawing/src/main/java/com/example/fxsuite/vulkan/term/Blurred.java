package com.example.fxsuite.vulkan.term;

/**
 * How far a pixel has blurred together things of a size: {@code covers} is how much of the surface the pixel covers,
 * {@code size} how big each thing is, both in centimetres. Whatever tells one thing from the next (a brick's own
 * colour, a stone's edge) is mixed toward its average by this much.
 */
public record Blurred(Real covers, double size) {

    /** A pixel covering this many of the things has begun to blur them together; this many, altogether. */
    private static final double BEGINS = 0.5;
    private static final double ENDS = 2;

    public Blurred {
        if (!(size > 0)) {
            throw new IllegalArgumentException("a thing has some size: " + size);
        }
    }

    /** From nothing, where the pixel is well within one thing, to one, where it covers several. */
    public Real share() {
        return covers.over(size).less(new Real(BEGINS)).over(ENDS - BEGINS).held(0, 1);
    }
}
