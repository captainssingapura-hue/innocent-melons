package melons.innocent.core.surface;

import melons.innocent.core.motion.Spot;

/**
 * How the surface differs between two spots, each as it was found: what two feet, two wheels of an axle, or a
 * vehicle's front and back are asked about together. It is reckoned in the world, so the two may be on one provider
 * or on two; on one curved provider it is as fine as the surface is, however near the two spots are.
 *
 * @param from the surface under the first spot
 * @param to   the surface under the second
 */
public record Span(Underfoot.Found from, Underfoot.Found to) {

    /** How much higher the surface is under the second spot than under the first, in centimetres. */
    public double rise() {
        return to.at().up() - from.at().up();
    }

    /** How far apart the two are on the level, in centimetres. */
    public double run() {
        final Spot apart = to.at().minus(from.at());
        return Math.hypot(apart.across(), apart.along());
    }

    /** How much it rises for each centimetre on the level from the first to the second; nothing, where they are one. */
    public double grade() {
        return run() == 0 ? 0 : rise() / run();
    }

    /** How far the line from the first to the second is tilted up from level, in degrees. */
    public double pitched() {
        return Math.toDegrees(Math.atan2(rise(), run()));
    }

    /** How far the way the surface faces turns from the first to the second, in degrees. */
    public double turned() {
        return Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, from.faces().dot(to.faces())))));
    }

    /** Whether both are on the one provider. */
    public boolean same() {
        return from.patch().provider().equals(to.patch().provider());
    }
}
