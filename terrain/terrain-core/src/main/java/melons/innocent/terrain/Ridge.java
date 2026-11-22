package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import melons.innocent.math.curves.CurveImpl;

/**
 * A ridge raised on the ground along a {@code crest} of the map's lines: at each distance along the crest, its top as
 * high as {@code height} says there, peaks where it is highest and saddles between; either side, its flanks falling
 * from the crest as {@code fall} says for how far from the crest a spot is. It never lowers the ground: where the
 * ground is higher, it stays as it was, and past the fall's curve the ridge is nothing to it. Where stretches of the
 * crest lie near one another, as round a bend or where a spur leaves the ridge from a spot they share, the highest
 * flank holds, so the ground never jumps between them. A range is ridges sharing spots: the main ridge and its spurs.
 *
 * @param name   its name
 * @param crest  its crest, from one end to the other
 * @param height how high its top is, over the sea, for how far along the crest
 * @param fall   how far its flanks have fallen from its top, for how far from the crest
 */
public record Ridge(String name, Path crest, CurveImpl<?> height, CurveImpl<?> fall) implements Landform {

    @Override
    public Shaping shaping(final Graph<Spot, Course> map) {
        final Traced traced = new Traced(crest, map);
        final double reach = fall.end();
        return (under, x, z) -> {
            if (traced.outside(x, z) > reach) {
                return under;
            }
            final double raised = -traced.least(x, z, reach,
                    (distance, station) -> fall.at(distance) - height.held(station));
            return Math.max(under, raised);
        };
    }
}
