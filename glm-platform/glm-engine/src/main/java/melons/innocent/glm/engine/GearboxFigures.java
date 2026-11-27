package melons.innocent.glm.engine;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * Where a twin's gearbox lies in its crankcase, and how big what turns in it is, in the block's frame, across the
 * crank as x and up as y, its back bank, and the gearbox, toward -x and its front bank toward +x, the way the bike
 * goes as its crank turns: after the reference twin's figures, its primary drive 89 teeth to 49, its six gears 2.857,
 * 2.111, 1.636, 1.320, 1.107 and 0.967 to one; where its shafts lie and how far apart, estimates. Its mainshaft
 * behind the crank and a little under it, as far off as the crank's swing and the mainshaft's biggest gear clear each
 * other; its countershaft behind the mainshaft and lower, the pairs' gears between them. The clutch on the mainshaft
 * at the crankcase's front end, the bike's left, outside the cam chains, the primary drive's gear round its basket;
 * the output pulley on the countershaft at its back end, the bike's right.
 *
 * @param figures the engine's figures
 */
record GearboxFigures(EngineFigures figures) {

    /** The primary drive's teeth, the crank's gear's and the clutch's. */
    static final int CRANK_TEETH = 49;
    static final int CLUTCH_TEETH = 89;
    /** Each gear, how many times the mainshaft turns to the countershaft's once, first to sixth. */
    static final List<Double> GEARS = List.of(2.857, 2.111, 1.636, 1.320, 1.107, 0.967);
    /** How far a gear's teeth stand past its pitch circle; how far the case stands clear of what turns, and its wall. */
    private static final double TOOTH = 0.25;
    private static final double CLEAR = 0.5;
    private static final double WALL = 0.5;
    /** How far the mainshaft is from the countershaft; which way each lies from the one before, in degrees. */
    private static final double APART = 7.0;
    private static final double MAIN_DOWN = 10;
    private static final double COUNTER_DOWN = 25;
    /** Which way the shift shaft lies from the countershaft, in degrees under level. */
    private static final double SHIFT_DOWN = 38;

    /** How far the crank's axis is from the mainshaft's: the crank's swing and the mainshaft's biggest gear clear. */
    double primary() {
        return new BlockForm(figures).clear() + mainGear() + TOOTH + CLEAR;
    }

    /** Where the mainshaft lies across the crank. */
    Spot mainshaft() {
        return toward(new Spot(0, 0), primary(), MAIN_DOWN);
    }

    /** Where the countershaft lies across the crank. */
    Spot countershaft() {
        return toward(mainshaft(), APART, COUNTER_DOWN);
    }

    /** How far round the crank's primary gear is to its pitch, and the clutch's. */
    double crankGear() {
        return primary() * CRANK_TEETH / (CRANK_TEETH + CLUTCH_TEETH);
    }

    double clutchGear() {
        return primary() * CLUTCH_TEETH / (CRANK_TEETH + CLUTCH_TEETH);
    }

    /** How far round the mainshaft's biggest gear is to its pitch: the top gear's, the pairs sharing their spacing. */
    double mainGear() {
        return APART / (1 + GEARS.stream().mapToDouble(Double::doubleValue).min().orElseThrow());
    }

    /** How far round the countershaft's biggest gear is to its pitch: first gear's. */
    double counterGear() {
        final double first = GEARS.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
        return APART * first / (1 + first);
    }

    /** How far round the case stands about the mainshaft, and the countershaft: past their gears, and its wall. */
    double mainCase() {
        return mainGear() + TOOTH + CLEAR + WALL;
    }

    double counterCase() {
        return counterGear() + TOOTH + CLEAR + WALL;
    }

    /**
     * Where the shift shaft lies across the crank: behind the countershaft and under it, just inside its case's wall,
     * clear of the clutch's basket, out through the case's front end, the bike's left, behind the clutch cover.
     */
    Spot shiftShaft() {
        return toward(countershaft(), counterCase() - WALL - 0.2, SHIFT_DOWN);
    }

    /** How far down the case's bottom is: under the countershaft's case. */
    double bottom() {
        return countershaft().v() - counterCase();
    }

    /** The spot {@code far} off {@code from}, back toward -x and {@code down} degrees under level. */
    private Spot toward(final Spot from, final double far, final double down) {
        final double a = Math.toRadians(-down);
        return new Spot(from.u() - far * Math.cos(a), from.v() + far * Math.sin(a));
    }
}
