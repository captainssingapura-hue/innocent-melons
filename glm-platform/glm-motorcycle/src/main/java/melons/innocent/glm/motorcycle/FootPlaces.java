package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * Where a motorcycle's foot controls lie, in its middle across, mid-set as the reference bike's are: each peg ahead of
 * the swingarm's pivot and low, under the rider's knee, as far under the seat's top as the rider's leg bends easily;
 * the gear lever's and the brake pedal's pivots on the hangers ahead of the pegs and under them; the gear lever's toe
 * peg ahead of the left peg, over where the toe lifts it; the brake pedal's pad ahead of the right peg, a little over
 * it; and where the hangers are bolted to the mid-structure, behind the pegs, over the exhaust's headers. Its places
 * estimates.
 *
 * @param figures the bike's figures
 */
record FootPlaces(BikeFigures figures) {

    /** How far ahead of the swingarm's pivot the pegs are, and how high over the ground. */
    private static final double PEG_AHEAD = 12.3;
    private static final double PEG_HIGH = 34;
    /** How far ahead of the pegs the pedals' pivots are, and how far under. */
    private static final double PIVOT_AHEAD = 5;
    private static final double PIVOT_UNDER = 4;
    /** How far ahead of the left peg the gear lever's toe peg is, and how far over. */
    private static final double TOE_AHEAD = 14;
    private static final double TOE_OVER = 6.5;
    /** How far ahead of the right peg the brake pedal's pad is, and how far over. */
    private static final double PAD_AHEAD = 13;
    private static final double PAD_OVER = 1.5;

    /** Where the pegs are. */
    Vec3 peg() {
        final Vec3 pivot = figures.pivot();
        return new Vec3(pivot.x() + PEG_AHEAD, figures.ground() + PEG_HIGH, figures.centre());
    }

    /** Where the gear lever and the brake pedal pivot. */
    Vec3 pivot() {
        return peg().plus(new Vec3(PIVOT_AHEAD, -PIVOT_UNDER, 0));
    }

    /** Where the gear lever's toe peg is. */
    Vec3 toe() {
        return peg().plus(new Vec3(TOE_AHEAD, TOE_OVER, 0));
    }

    /** Where the brake pedal's pad is. */
    Vec3 pad() {
        return peg().plus(new Vec3(PAD_AHEAD, PAD_OVER, 0));
    }

    /** Where the brake pedal's arm meets the master cylinder's pushrod, over its pivot. */
    Vec3 pushrod() {
        return pivot().plus(new Vec3(-1, 4, 0));
    }

    /** Where a hanger is bolted to the mid-structure: its upper bolt or its lower, behind the peg. */
    Vec3 bolt(final boolean upper) {
        final Vec3 pivot = figures.pivot();
        return new Vec3(pivot.x() + 6, pivot.y() + (upper ? 4.5 : 0.5), figures.centre());
    }

    /** {@code p} moved {@code out} from the middle across, toward the right as it is more. */
    Vec3 out(final Vec3 p, final double out) {
        return new Vec3(p.x(), p.y(), figures.centre() + out);
    }
}
