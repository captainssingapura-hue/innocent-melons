package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import melons.innocent.math.curves.CurveImpl;

/**
 * A coast along a {@code shore} of the map's lines, the sea on its right as it goes: where the sea's level meets the
 * land. Seaward of it the ground lies as {@code seabed} says for how far out a spot is, under the sea's level, the
 * curve held at its end over the open sea; the ground there is never higher, but lower where something laid before
 * cut it deeper. Landward, the shore rises from the sea's level as {@code beach} says for how far in a spot is, never
 * raising the ground, until the curve ends and the coast is nothing to the ground. The shore's line runs on past the
 * map's edges at both ends, so every spot is told the side it is on.
 *
 * @param name   its name
 * @param shore  the waterline, the sea on its right
 * @param seabed how high the ground lies under the sea, for how far out from the shore: nothing or less
 * @param beach  how high the land rises from the shore, for how far in
 */
public record Coast(String name, Path shore, CurveImpl<?> seabed, CurveImpl<?> beach) implements Landform {

    @Override
    public Shaping shaping(final Graph<Spot, Course> map) {
        final Traced traced = new Traced(shore, map);
        return (under, x, z) -> {
            final Traced.Near near = traced.near(x, z);
            if (near.side() < 0) {
                return Math.min(under, seabed.held(near.distance()));
            }
            if (near.distance() > beach.end()) {
                return under;
            }
            return Math.min(under, beach.at(near.distance()));
        };
    }
}
