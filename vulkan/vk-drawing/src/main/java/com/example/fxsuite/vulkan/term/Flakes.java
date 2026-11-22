package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * Flakes of metal lying in a paint, under its clear skin, which is what makes a paint metallic. Each is a small
 * mirror lying nearly flat, so between them they send the sun on in a wide, soft shine of their own {@code colour}
 * round where a mirror would show it, as wide as {@code spread} is low; and the paint is duller the more nearly it
 * is seen edge on, by as much as {@code flop} at the very edge, since the flakes then send the light elsewhere.
 *
 * <p>Seen close, the flakes are told apart: {@code size} is how far it is from one to the next, in centimetres,
 * and {@code few} how many of them catch the sun at once, of them all. Which ones do changes as the thing is
 * turned or the eye moves, so they glitter. Further off, as a pixel comes to cover several, their glints run
 * together into the soft shine, which is what they come to between them: nothing is brighter or duller for it,
 * and nothing has to be made anew for far off.
 */
public record Flakes(Colour colour, double spread, double flop, double size, double few) {

    /** How far from the middle of its cell a flake reaches, of the cell's width: so no flake touches the next. */
    private static final double REACH = 0.35;
    /** How much of all there is round a place a ball one across takes up, over its reach three times multiplied. */
    private static final double BALL = 4.0 / 3 * Math.PI;
    /** How much of the shine is of flakes big enough to tell apart; the rest is of ones too fine at any distance. */
    private static final double TOLD = 0.6;
    private static final double MIDDLE = 0.5;

    public Flakes {
        if (!(spread > 0) || !(size > 0) || !(few > 0 && few <= 1) || !(flop >= 0 && flop <= 1)) {
            throw new IllegalArgumentException("flakes shine so wide, lie so far apart, so many catch the sun,"
                    + " and dull the paint so much: " + spread + ", " + size + ", " + few + ", " + flop);
        }
    }

    /**
     * How much of the flakes' shine shows at a point, about one: more where a flake there catches the sun, less
     * between the flakes, and one exactly where a pixel covers several. {@code turned} is any number that changes
     * as the thing is turned or the eye moves: which flakes catch the sun goes by it, a whole turn of it bringing
     * the same ones round again.
     */
    public Real caught(final Real turned) {
        final Seen seen = new Seen();
        final Point at = seen.at();
        // the cells stand askew to the thing, so a flat face of it cuts them every way and not all alike
        final Real first = at.across().times(0.36).plus(at.up().times(0.48)).plus(at.along().times(0.8)).over(size);
        final Real second = at.across().times(-0.8).plus(at.up().times(0.6)).over(size);
        final Real third = at.across().times(-0.48).plus(at.up().times(-0.64)).plus(at.along().times(0.6))
                .over(size);
        final Real own = new Real(new Applied(Chance.SCATTERED,
                List.of(first.floored().node(), second.floored().node(), third.floored().node())));
        // a flake is round: a ball about its cell's middle, which the surface cuts where it will
        final Real within = new Real(REACH * REACH).past(off(first).plus(off(second)).plus(off(third)));
        final Real glint = own.plus(turned).part().past(new Real(1 - few)).times(within).times(1 / share());
        return new Real(1 - TOLD).plus(glint.times(TOLD))
                .mixed(new Real(1), new Blurred(seen.covers(), size).share());
    }

    /** How much less of the paint's own colour shows, seen at an angle whose cosine is {@code square}: 1 square on. */
    public Real dulled(final Real square) {
        final Real aside = new Real(1).less(square);
        return new Real(1).less(aside.times(aside).times(flop));
    }

    /**
     * How much of a surface is flakes catching the sun: the few that do, of the room their balls take up among
     * the cells, which is as much of any surface cutting through them as it is of the whole.
     */
    public double share() {
        return few * BALL * REACH * REACH * REACH;
    }

    /** How far a place is from the middle of its cell, one way, multiplied by itself. */
    private Real off(final Real along) {
        final Real from = along.part().less(new Real(MIDDLE));
        return from.times(from);
    }
}
