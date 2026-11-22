package melons.innocent.terrain;

import com.example.fxsuite.taxonomy.Leaf;
import melons.innocent.math.curves.CurveImpl;

/**
 * Water running along a {@code course} of the map's lines: a river, a stream. At each distance along it, its top lies
 * as high as {@code surface} says, held at the curve's ends; it lies wherever the ground is lower than that, no
 * further from its course than {@code spread}, so it fills its bed to its banks and floods nothing beyond them.
 *
 * @param label   its label
 * @param kind    the kind of thing it is
 * @param hue     the colour it is shown in
 * @param course  its way, from its head to its foot
 * @param surface how high its top lies, for how far along it
 * @param spread  how far from its course it may lie, at most, in centimetres
 */
public record Watercourse(String label, Leaf<?> kind, Hue hue, Path course, CurveImpl<?> surface, double spread)
        implements WaterBody {

    @Override
    public Coverage coverage(final Terrain terrain) {
        final Traced traced = new Traced(course, terrain.map());
        return at -> {
            if (traced.outside(at.x(), at.z()) > spread) {
                return false;
            }
            final Traced.Near near = traced.near(at.x(), at.z());
            return near.distance() <= spread && at.height() < surface.held(near.station());
        };
    }

    /** How high its top is at the nearest point of its course, anywhere within its spread of it. */
    @Override
    public Level level(final Terrain terrain) {
        final Traced traced = new Traced(course, terrain.map());
        return (x, z) -> {
            if (traced.outside(x, z) > spread) {
                return Double.NaN;
            }
            final Traced.Near near = traced.near(x, z);
            return near.distance() <= spread ? surface.held(near.station()) : Double.NaN;
        };
    }
}
