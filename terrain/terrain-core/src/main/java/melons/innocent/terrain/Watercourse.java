package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import com.example.fxsuite.taxonomy.Leaf;
import melons.innocent.math.curves.CurveImpl;

/**
 * Water running along a {@code course} of the map's lines: a river, a stream. Its top lies {@code depth} over the
 * ground along its course, the bed it has worn, at each distance along it, held at the curve's ends, and never under
 * {@code lowest}, the level of what it runs into; level across, as water is. It lies wherever the ground is lower than
 * its top, no further from its course than {@code spread}, so it fills its bed to its banks and floods nothing beyond
 * them. Its top is read from the ground; it changes the ground not at all.
 *
 * @param label  its label
 * @param kind   the kind of thing it is
 * @param hue    the colour it is shown in
 * @param course its way, from its head to its foot
 * @param depth  how deep it runs over the ground along its course, for how far along it
 * @param lowest how low its top ever lies, in centimetres: the level of the sea or lake it runs into
 * @param spread how far from its course it may lie, at most, in centimetres
 */
public record Watercourse(String label, Leaf<?> kind, Hue hue, Path course, CurveImpl<?> depth, double lowest,
                          double spread) implements WaterBody {

    @Override
    public Coverage coverage(final Graph<Spot, Course> map, final Ground ground) {
        final Level level = level(map, ground);
        return at -> {
            final double top = level.at(at.x(), at.z());
            return !Double.isNaN(top) && at.height() < top;
        };
    }

    /** How high its top is at the nearest point of its course, anywhere within its spread of it. */
    @Override
    public Level level(final Graph<Spot, Course> map, final Ground ground) {
        final Traced traced = new Traced(course, map);
        return (x, z) -> {
            if (traced.outside(x, z) > spread) {
                return Double.NaN;
            }
            final Traced.Near near = traced.near(x, z);
            return near.distance() <= spread
                    ? Math.max(lowest, ground.height(near.x(), near.z()) + depth.held(near.station())) : Double.NaN;
        };
    }
}
