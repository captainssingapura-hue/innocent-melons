package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import melons.innocent.math.curves.CurveImpl;

/**
 * A channel cut into the ground along a {@code course} of the map's lines, from its head to its foot: a river's bed
 * and the valley it runs in. At each distance along it, its bed is as high as {@code bed} says and as wide as
 * {@code width} says, level across; beyond the bed's edge, either side, its sides rise as {@code banks} says for how
 * far beyond it a spot is, until they meet the ground, which they never raise: where the ground is lower, it stays
 * as it was. Past the banks' curve, the channel is nothing to the ground. Its curves along it are held at their ends.
 * Where stretches of it lie near one another, as round a bend, each cuts the ground and the deepest cut holds, so
 * the ground never jumps between them.
 *
 * @param name   its name
 * @param course its way, from its head to its foot
 * @param bed    how high its bed is, for how far along it
 * @param width  how wide its bed is, for how far along it
 * @param banks  how high its sides rise over its bed, for how far beyond the bed's edge
 */
public record Channel(String name, Path course, CurveImpl<?> bed, CurveImpl<?> width, CurveImpl<?> banks)
        implements Landform {

    @Override
    public Shaping shaping(final Graph<Spot, Course> map) {
        final Traced traced = new Traced(course, map);
        final double reach = banks.end() + width.held(Double.MAX_VALUE) + width.held(0);
        return (under, x, z) -> {
            if (traced.outside(x, z) > reach) {
                return under;
            }
            final double cut = traced.least(x, z, reach, (distance, station) -> {
                final double beyond = Math.max(0, distance - width.held(station) / 2);
                return beyond > banks.end() ? Double.POSITIVE_INFINITY : bed.held(station) + banks.at(beyond);
            });
            return Math.min(under, cut);
        };
    }
}
