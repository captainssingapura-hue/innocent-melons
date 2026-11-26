package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.sheet.Sheet;
import melons.innocent.physics.existences.Shapes;

/**
 * A shell: a sheet that curves, so thick, that never moves. A spiral ramp's deck, a vaulted or a domed roof, a
 * banked bend. It is of the kind physics calls a shell, which is unmoving, so the world never checks it and never
 * lets it go. Its top is the sheet; what is under a spot of it is asked of the sheet, exactly.
 *
 * <p>Nothing loose meets it yet: a thing let go over it falls through it. Only whoever asks what is under a spot
 * finds it there.
 *
 * @param sheet its top, in the shell's own frame
 * @param thick how thick it is under its top, in centimetres
 */
public record Shell(Sheet sheet, double thick) implements ShapeImpl<Shapes.Shell> {

    /** How many places each way it is looked at to find how much room it takes. */
    private static final int LOOKS = 24;

    public Shell {
        if (!(thick > 0)) {
            throw new IllegalArgumentException("a shell some way thick: " + thick);
        }
    }

    @Override
    public Shapes.Shell kind() {
        return new Shapes.Shell();
    }

    /** About: its top's spread, summed over small pieces of it, times how thick it is. */
    @Override
    public double room() {
        double spread = 0;
        for (int far = 0; far < LOOKS; far++) {
            for (int over = 0; over < LOOKS; over++) {
                final Spot corner = sheet.at((double) far / LOOKS, (double) over / LOOKS);
                final Spot onward = sheet.at((far + 1.0) / LOOKS, (double) over / LOOKS).minus(corner);
                final Spot aside = sheet.at((double) far / LOOKS, (over + 1.0) / LOOKS).minus(corner);
                spread += onward.cross(aside).length();
            }
        }
        return spread * thick / 1e6;
    }

    @Override
    public double reaches() {
        return sheet.reaches() + thick;
    }

    /** It is never set turning: however hard is asked for is not enough. */
    @Override
    public double turnsHard(final Spot along, final double kilograms) {
        return Double.POSITIVE_INFINITY;
    }
}
