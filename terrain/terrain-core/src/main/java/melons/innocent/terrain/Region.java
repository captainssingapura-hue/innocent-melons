package melons.innocent.terrain;

import java.util.List;

/**
 * A stretch of the map within loops: a spot is within it if a line from it on across crosses its loops an odd number
 * of times all told, so a loop inside another cuts a hole in it; or, if it is the {@code outside}, an even number,
 * everything else. And how far a spot is from its edge, its nearest loop, told less than nothing within it.
 *
 * @param loops   its edges
 * @param outside whether it is what lies outside them rather than within
 */
public record Region(List<Loop> loops, boolean outside) {

    public Region {
        loops = List.copyOf(loops);
        if (loops.isEmpty()) {
            throw new IllegalArgumentException("a region within one loop at least");
        }
    }

    /** Whether ({@code x}, {@code z}) lies within it. */
    public boolean holds(final double x, final double z) {
        boolean odd = false;
        for (final Loop loop : loops) {
            odd ^= loop.inside(x, z);
        }
        return odd != outside;
    }

    /** How far ({@code x}, {@code z}) is from its edge: less than nothing within it. */
    public double signed(final double x, final double z) {
        double least = Double.MAX_VALUE;
        for (final Loop loop : loops) {
            least = Math.min(least, loop.distance(x, z));
        }
        return holds(x, z) ? -least : least;
    }
}
