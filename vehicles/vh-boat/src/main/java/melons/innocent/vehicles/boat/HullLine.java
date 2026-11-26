package melons.innocent.vehicles.boat;

import hue.captains.singapura.tao.ontology.Scalar;
import melons.innocent.math.curves.CurveImpl;

/**
 * What an edge of a hull's lines carries: which line it is part of, and how it runs from its first point to its
 * second: straight, or bowed outward from the middle line along a curve, as a sheer swells amidships. The lines
 * are named as a boatbuilder names them.
 */
public sealed interface HullLine extends Scalar permits HullLine.Straight, HullLine.Bowed {

    /** The lines of a hull. */
    enum Kind {
        /** Where its side meets its deck. */
        SHEER,
        /** Where its side turns under, to its bottom. */
        CHINE,
        /** Along the middle of its bottom. */
        KEEL,
        /** Up its bow, where its two sides meet. */
        STEM
    }

    /** Which line it is part of. */
    Kind kind();

    /** Straight from its first point to its second. */
    record Straight(Kind kind) implements HullLine {
    }

    /**
     * Bowed: at each share of the way from its first point to its second, as far out from the middle line beyond the
     * straight line between them as {@code out} says there. The curve runs from nothing to one, and is nothing at both
     * ends, so the line still meets its points.
     *
     * @param kind which line it is part of
     * @param out  how far out it bows, in centimetres, at each share of the way
     */
    record Bowed(Kind kind, CurveImpl<?> out) implements HullLine {

        public Bowed {
            if (Math.abs(out.at(out.start())) > 1e-9 || Math.abs(out.at(out.end())) > 1e-9) {
                throw new IllegalArgumentException("a bowed line meets its points: nothing out at both ends");
            }
        }
    }
}
