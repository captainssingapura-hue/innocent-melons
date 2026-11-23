package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Still water within a {@code shore} of the map's lines, closed round: a pond, a lake. Its top lies level at
 * {@code level} over all the ground within its shore, whether that ground is under it or not; it lies wherever the
 * ground within is lower than its top, so it fills its hollow to its level and no further. Read from the ground; it
 * changes the ground not at all.
 *
 * @param label its label
 * @param kind  the kind of thing it is
 * @param hue   the colour it is shown in
 * @param shore the lines round it, ending where they begin
 * @param level how high its top lies, in centimetres
 */
public record Lake(String label, Leaf<?> kind, Hue hue, Path shore, double level) implements WaterBody {

    public Lake {
        if (!shore.spots().getFirst().equals(shore.spots().getLast()) || shore.spots().size() < 4) {
            throw new IllegalArgumentException(label + ": a lake closed round by three lines at least");
        }
    }

    @Override
    public Coverage coverage(final Graph<Spot, Course> map, final Ground ground) {
        final Traced traced = new Traced(shore, map);
        return at -> at.height() < level && traced.inside(at.x(), at.z());
    }

    /** Its level, everywhere within its shore, whether the ground there is under it or not. */
    @Override
    public Level level(final Graph<Spot, Course> map, final Ground ground) {
        final Traced traced = new Traced(shore, map);
        return (x, z) -> traced.inside(x, z) ? level : Double.NaN;
    }
}
