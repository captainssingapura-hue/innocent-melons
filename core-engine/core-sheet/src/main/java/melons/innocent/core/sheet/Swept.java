package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A sheet swept along a line: at every place of the line its profile stands square across it, upright. Along a
 * straight line it is a strip of road or a vault; round a ring, a banked bend or a dome; round a helix, a spiral
 * ramp. Its height over a spot is the line's height abreast of the spot and the profile's that far aside, and its
 * rise there is worked out from the line's grade, its bend and the profile's own rise: nothing is cut into pieces.
 *
 * @param line    the line it is swept along
 * @param profile its shape across the line
 */
public record Swept(Line line, Profile profile) implements Sheet {

    /** How much of a line's own length a line beside it must still have, to be part of the sheet. */
    private static final double LEAST = 1e-9;
    /** How many places each way it is looked at to find how far it reaches. */
    private static final int LOOKS = 48;

    @Override
    public List<Lift> over(final double across, final double along) {
        final List<Lift> lifts = new ArrayList<>();
        for (final Line.Abreast pass : line.abreast(across, along)) {
            final double far = pass.far();
            final double aside = pass.aside();
            // a line beside the sheet's own, so far aside, is shorter or longer by this, as the line bends
            final double drawn = 1 - aside * line.bends(far);
            if (far < 0 || far > line.length() || aside < profile.from() || aside > profile.to()
                    || drawn < LEAST && line.grade(far) != 0) {
                continue;
            }
            final double way = line.heading(far);
            final double onward = line.grade(far) == 0 ? 0 : line.grade(far) / drawn;
            final double aslant = profile.rises(aside);
            final double ends = line.closed() ? Double.POSITIVE_INFINITY
                    : Math.min(far, line.length() - far) * drawn;
            lifts.add(new Lift(pass.layer(), line.at(far).up() + profile.high(aside),
                    onward * Math.sin(way) + aslant * Math.cos(way), onward * Math.cos(way) - aslant * Math.sin(way),
                    Math.min(ends, Math.min(edge(far, aside, profile.from()), edge(far, aside, profile.to())))));
        }
        lifts.sort(Comparator.comparingDouble(Lift::high));
        return lifts;
    }

    /**
     * How far a spot so far {@code aside} is from the profile's edge at {@code edge}: no edge at all where the line
     * beside the sheet's own has shrunk to a point there, as a dome's has at its middle.
     */
    private double edge(final double far, final double aside, final double edge) {
        return 1 - edge * line.bends(far) < LEAST ? Double.POSITIVE_INFINITY : Math.abs(aside - edge);
    }

    /** The spot of it so far along its line and so far to the line's right, each in centimetres. */
    public Spot on(final double far, final double aside) {
        final Spot abreast = line.at(far);
        final double way = line.heading(far);
        return new Spot(abreast.across() + aside * Math.cos(way), abreast.up() + profile.high(aside),
                abreast.along() - aside * Math.sin(way));
    }

    @Override
    public Spot at(final double far, final double over) {
        return on(far * line.length(), profile.from() + over * (profile.to() - profile.from()));
    }

    @Override
    public double reaches() {
        double reaches = 0;
        for (int far = 0; far <= LOOKS; far++) {
            for (int over = 0; over <= LOOKS; over++) {
                reaches = Math.max(reaches, at((double) far / LOOKS, (double) over / LOOKS).length());
            }
        }
        return reaches;
    }
}
