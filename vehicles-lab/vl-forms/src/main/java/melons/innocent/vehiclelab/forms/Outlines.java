package melons.innocent.vehiclelab.forms;

import java.util.ArrayList;
import java.util.List;

/**
 * Outlines a pressing is drawn with, each round to the left, every round one {@code round} points round: a round hole;
 * a slot, two half rounds joined straight, round where it is no longer than wide; and a tab, as a bracket's cheek is,
 * wide at its foot and rounded round the hole at its tip.
 *
 * @param round how many points a whole round is drawn in
 */
public record Outlines(int round) {

    /** A round {@code radius} about {@code middle}. */
    public List<Spot> circle(final Spot middle, final double radius) {
        final List<Spot> spots = new ArrayList<>();
        for (int k = 0; k < round; k++) {
            final double a = 2 * Math.PI * k / round;
            spots.add(new Spot(middle.u() + radius * Math.cos(a), middle.v() + radius * Math.sin(a)));
        }
        return spots;
    }

    /** A slot about {@code middle}, {@code length} end to end along, {@code wide} across. */
    public List<Spot> slot(final Spot middle, final double length, final double wide) {
        final double r = wide / 2;
        final double reach = Math.max(0, length / 2 - r);
        if (reach < 1e-9) {
            return circle(middle, r);
        }
        final List<Spot> spots = new ArrayList<>();
        final int half = Math.max(2, round / 2);
        for (int k = 0; k <= half; k++) {
            final double a = -Math.PI / 2 + Math.PI * k / half;
            spots.add(new Spot(middle.u() + reach + r * Math.cos(a), middle.v() + r * Math.sin(a)));
        }
        for (int k = 0; k <= half; k++) {
            final double a = Math.PI / 2 + Math.PI * k / half;
            spots.add(new Spot(middle.u() - reach + r * Math.cos(a), middle.v() + r * Math.sin(a)));
        }
        return spots;
    }

    /**
     * A tab standing up from its foot along {@code v} = nought, {@code foot} wide there, to its tip, a round
     * {@code tip} about the spot {@code reach} up the middle.
     */
    public List<Spot> tab(final double foot, final double reach, final double tip) {
        final List<Spot> spots = new ArrayList<>();
        spots.add(new Spot(-foot / 2, 0));
        spots.add(new Spot(foot / 2, 0));
        final int half = Math.max(2, round / 2);
        for (int k = 0; k <= half; k++) {
            final double a = Math.PI * k / half;
            spots.add(new Spot(tip * Math.cos(a), reach + tip * Math.sin(a)));
        }
        return spots;
    }
}
