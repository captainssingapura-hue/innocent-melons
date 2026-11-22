package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import melons.innocent.math.curves.CurveImpl;

/**
 * A mount raised on the ground: round its {@code summit}, the ground raised as {@code rise} says for how far a spot is
 * from it, held at the curve's end beyond it (nothing, for a mount whose foot meets the ground round it). Ribbed,
 * as a melon is: {@code ribs} ridges running down from the summit, a gully between each two, standing out and cut in
 * as far as {@code ribbing} says for how far from the summit they are. A mount with no ribs is round.
 *
 * @param name    its name
 * @param summit  its top
 * @param rise    how far the ground is raised, for how far from the summit
 * @param ribs    how many ridges it has, round it
 * @param ribbing how far its ridges stand out and its gullies cut in, for how far from the summit
 */
public record Mount(String name, Spot summit, CurveImpl<?> rise, int ribs, CurveImpl<?> ribbing)
        implements Landform {

    public Mount {
        if (ribs < 0) {
            throw new IllegalArgumentException(name + ": no fewer ribs than none, not " + ribs);
        }
    }

    @Override
    public Shaping shaping(final Graph<Spot, Course> map) {
        return (under, x, z) -> {
            final double dx = x - summit.x();
            final double dz = z - summit.z();
            final double off = Math.hypot(dx, dz);
            final double ribbed = ribs == 0 ? 0 : ribbing.held(off) * Math.cos(ribs * Math.atan2(dz, dx));
            return under + rise.held(off) + ribbed;
        };
    }
}
