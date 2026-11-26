package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.physics.existences.Shapes;

/**
 * An ellipsoid: round every way, so far across, so far up and so far along through its middle, in centimetres. A
 * watermelon, an egg; and a ball, where it is as far each way. It has no face and no corner: it touches a flat thing
 * at the one spot of it that reaches furthest toward that thing.
 *
 * @param across how far across it is, side to side, through its middle
 * @param up     how high it is through its middle
 * @param along  how far along it is through its middle
 */
public record Ellipsoid(double across, double up, double along) implements ShapeImpl<Shapes.Ellipsoid> {

    public Ellipsoid {
        if (!(across > 0) || !(up > 0) || !(along > 0)) {
            throw new IllegalArgumentException("an ellipsoid of some size each way: " + across + ", " + up + ", "
                    + along);
        }
    }

    /** A ball so far across. */
    public Ellipsoid(final double across) {
        this(across, across, across);
    }

    @Override
    public Shapes.Ellipsoid kind() {
        return new Shapes.Ellipsoid();
    }

    @Override
    public double room() {
        return Math.PI * across * up * along / 6 / 1_000_000;
    }

    @Override
    public double reaches() {
        return Math.max(across, Math.max(up, along)) / 2;
    }

    @Override
    public double turnsHard(final Spot along, final double kilograms) {
        final double twentieth = kilograms / 20;
        return twentieth * (up * up + this.along * this.along) * along.across() * along.across()
                + twentieth * (across * across + this.along * this.along) * along.up() * along.up()
                + twentieth * (across * across + up * up) * along.along() * along.along();
    }

    /** How far its surface is from its middle the way {@code way} goes, in its own frame, {@code way} one long. */
    public double out(final Spot way) {
        final Spot scaled = new Spot(2 * way.across() / across, 2 * way.up() / up, 2 * way.along() / along);
        return 1 / scaled.length();
    }

    /** The spot of it, from its middle, that reaches furthest the {@code way} said, in its own frame. */
    public Spot furthest(final Spot way) {
        final Spot scaled = new Spot(way.across() * across / 2, way.up() * up / 2, way.along() * along / 2);
        final double length = scaled.length();
        return new Spot(scaled.across() * across / 2 / length, scaled.up() * up / 2 / length,
                scaled.along() * along / 2 / length);
    }
}
