package melons.innocent.terrain;

/**
 * A made work, the ground as people shaped it rather than as it was found: a pad, a cutting, a dredged basin, a dug
 * lake, reclaimed land. Within its {@code footprint}, {@code bank} in from its edge, the ground is made as high as its
 * {@code level}; from there outward it is cut down to a slope rising {@code cut} for each unit out, wherever it stands
 * above that, and built up to a slope falling {@code fill} for each unit out, wherever it lies below; beyond where
 * those slopes meet the ground as it was, it is left as it was. A bank of nothing puts the slopes outside the
 * footprint, at its edge; a bank as wide as the slopes take to reach the ground puts the ground as it was at the edge
 * itself, as a shore at the top of the bank under a basin's water. A slope of no end, {@link Double#POSITIVE_INFINITY}, is a work
 * that never reaches out that way: never cuts, or never fills, beyond its level.
 *
 * @param name      what it is called
 * @param footprint where it is made
 * @param level     how high it is made, in centimetres
 * @param bank      how far in from its footprint's edge it is made as high as its level
 * @param cut       how steeply the ground is cut down from there out, rise for each unit across
 * @param fill      how steeply it is built up from there out
 */
public record Work(String name, Region footprint, double level, double bank, double cut, double fill) {

    public Work {
        if (!Double.isFinite(level) || !(bank >= 0) || !(cut > 0) || !(fill > 0)) {
            throw new IllegalArgumentException(name + ": a work at some level, its bank no less than nothing, its"
                    + " slopes some steepness: " + level + ", " + bank + ", " + cut + ", " + fill);
        }
    }

    /** How high the ground at ({@code x}, {@code z}), {@code was} high before, is made by it. */
    public double made(final double was, final double x, final double z) {
        final double out = Math.max(0, footprint.signed(x, z) + bank);
        final double highest = out == 0 ? level : level + cut * out;
        final double lowest = out == 0 ? level : level - fill * out;
        return Math.clamp(was, lowest, highest);
    }
}
