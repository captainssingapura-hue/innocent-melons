package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;

/**
 * A sheet over one spot of its floor: how high it is there and how it rises from there, worked out exactly from what
 * the sheet is, never from facets. A sheet that passes over itself, as a spiral ramp does turn over turn, has one of
 * these to each time it passes.
 *
 * @param layer      which of its passes over the spot this is, counted from nothing: a ramp's first turn, its second
 * @param high       how high it is there, in centimetres
 * @param acrossRise how much higher it is a centimetre further across: its rise for a step that way
 * @param alongRise  how much higher it is a centimetre further along
 * @param within     how far it goes on from there before its nearest edge, in centimetres, as seen from above
 */
public record Lift(int layer, double high, double acrossRise, double alongRise, double within) {

    /** Which way it faces there, one long: straight up where it is level. */
    public Spot faces() {
        return new Spot(-acrossRise, 1, -alongRise).unit();
    }

    /** How steep it is there, in degrees: nothing where it is level. */
    public double steep() {
        return Math.toDegrees(Math.atan(Math.hypot(acrossRise, alongRise)));
    }

    /** How much higher it is a short way off, {@code across} and {@code along} from there: by its rise alone. */
    public double rising(final double across, final double along) {
        return acrossRise * across + alongRise * along;
    }
}
