package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;

import java.util.List;

/**
 * A sheet warped like a saddle: an oblong whose two opposite corners are lifted and the other two lowered, every
 * line across it and every line along it staying straight. A roof of that shape; and a sheet that is swept along
 * nothing, which is why it is here: any surface that can say its height and its rise over a spot is a sheet.
 *
 * @param wide how far across it is, in centimetres
 * @param deep how far along it is
 * @param lift how much higher than its middle its two lifted corners are, the other two being as much lower
 */
public record Warped(double wide, double deep, double lift) implements Sheet {

    public Warped {
        if (!(wide > 0) || !(deep > 0)) {
            throw new IllegalArgumentException("a sheet some way across and along: " + wide + ", " + deep);
        }
    }

    /** How much it rises for a centimetre across, a centimetre along from its middle; and so the other way about. */
    private double twist() {
        return lift / (wide / 2 * deep / 2);
    }

    @Override
    public List<Lift> over(final double across, final double along) {
        final double within = Math.min(wide / 2 - Math.abs(across), deep / 2 - Math.abs(along));
        return within < 0 ? List.of()
                : List.of(new Lift(0, twist() * across * along, twist() * along, twist() * across, within));
    }

    @Override
    public Spot at(final double far, final double over) {
        final double across = (over - 0.5) * wide;
        final double along = (far - 0.5) * deep;
        return new Spot(across, twist() * across * along, along);
    }

    @Override
    public double reaches() {
        return new Spot(wide / 2, lift, deep / 2).length();
    }
}
