package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A round line: round a middle at one distance from it, so many turns, climbing evenly. Level and one turn, it is a
 * ring; climbing over several, a helix, a spiral ramp's line; less than a turn, a bend. It passes a spot beside it
 * once in each turn, each pass a layer over the one before.
 *
 * @param middle what it turns round, and how high it starts
 * @param radius how far from the middle it runs, in centimetres
 * @param from   where round the middle it starts, as a compass counts from the middle, in degrees
 * @param turns  how many times round it goes: to its right, as a clock's hands go seen from above; less than
 *               nothing, to its left
 * @param climbs how much it rises in each turn, in centimetres; nothing, level
 */
public record Round(Spot middle, double radius, double from, double turns, double climbs) implements Line {

    /** How near the end of a turn a spot is taken to be at the start of the next: in radians. */
    private static final double SEAM = 1e-9;

    public Round {
        if (!(radius > 0) || turns == 0) {
            throw new IllegalArgumentException("a round line some way out, going some way round: " + radius + ", "
                    + turns);
        }
    }

    /** One for a line turning to its right, less one for one turning to its left. */
    private double hand() {
        return Math.signum(turns);
    }

    /** How far round the middle it is, so {@code far} along itself: as a compass counts from the middle, radians. */
    private double bearing(final double far) {
        return Math.toRadians(from) + hand() * far / radius;
    }

    @Override
    public double length() {
        return Math.abs(turns) * 2 * Math.PI * radius;
    }

    @Override
    public Spot at(final double far) {
        final double round = bearing(far);
        return new Spot(middle.across() + radius * Math.sin(round), middle.up() + grade(far) * far,
                middle.along() + radius * Math.cos(round));
    }

    @Override
    public double heading(final double far) {
        return bearing(far) + hand() * Math.PI / 2;
    }

    @Override
    public double grade(final double far) {
        return climbs / (2 * Math.PI * radius);
    }

    @Override
    public double bends(final double far) {
        return hand() / radius;
    }

    @Override
    public boolean closed() {
        return climbs == 0 && Math.abs(turns) >= 1;
    }

    @Override
    public List<Abreast> abreast(final double across, final double along) {
        final double x = across - middle.across();
        final double z = along - middle.along();
        final double aside = hand() * (radius - Math.hypot(x, z));
        final double whole = 2 * Math.PI;
        double gone = (hand() * (Math.atan2(x, z) - Math.toRadians(from))) % whole;
        gone = gone < 0 ? gone + whole : gone;
        gone = gone > whole - SEAM ? 0 : gone;
        // a ring is passed once; a helix once in each turn, its very end included
        final double all = closed() ? 0 : Math.abs(turns) * whole - gone;
        final List<Abreast> passes = new ArrayList<>();
        for (int layer = 0; layer * whole <= all + SEAM; layer++) {
            passes.add(new Abreast(layer, Math.min(length(), (gone + layer * whole) * radius), aside));
        }
        return passes;
    }
}
