package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;

import java.util.List;

/**
 * A straight line: from a spot, heading one way, so long, rising evenly.
 *
 * @param from    where it starts
 * @param heading which way it heads, as a compass counts, in degrees: nothing is along, ninety is across
 * @param length  how long it is, as seen from above, in centimetres
 * @param grade   how much it rises for each centimetre it goes on; nothing, level
 */
public record Straight(Spot from, double heading, double length, double grade) implements Line {

    public Straight {
        if (!(length > 0)) {
            throw new IllegalArgumentException("a line some way long: " + length);
        }
    }

    /** A level line. */
    public Straight(final Spot from, final double heading, final double length) {
        this(from, heading, length, 0);
    }

    @Override
    public Spot at(final double far) {
        final double way = Math.toRadians(heading);
        return new Spot(from.across() + far * Math.sin(way), from.up() + far * grade,
                from.along() + far * Math.cos(way));
    }

    @Override
    public double heading(final double far) {
        return Math.toRadians(heading);
    }

    @Override
    public double grade(final double far) {
        return grade;
    }

    @Override
    public double bends(final double far) {
        return 0;
    }

    @Override
    public List<Abreast> abreast(final double across, final double along) {
        final double way = Math.toRadians(heading);
        final double x = across - from.across();
        final double z = along - from.along();
        return List.of(new Abreast(0, x * Math.sin(way) + z * Math.cos(way), x * Math.cos(way) - z * Math.sin(way)));
    }
}
