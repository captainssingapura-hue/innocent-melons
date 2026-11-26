package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Reckoning;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;

/**
 * The colour a bark comes to over a piece of wood seen from too far to tell anything of it apart: its unlit colour
 * worked out by Java from the bark's own terms at points spread over the wood, each counted for as much bark as it
 * stands for. So a far birch's twigs come to the reddish brown its slender wood is, and its trunk to its white, each
 * as the bark says, not as anyone states. Worked out at some thousands of points at most, spread evenly over the
 * wood's triangles, the same every time.
 */
public record Afar(Surface bark) {

    /** How many of the wood's triangles are sampled, at most. */
    private static final int AT_MOST = 4000;

    /** What {@code bark} comes to over {@code wood}, as much red, green and blue as it has. */
    public Vec3 over(final Mesh wood) {
        final Reckoning colour = new Reckoning(bark.colour().node());
        final Seen seen = new Seen();
        final int step = Math.max(1, wood.triangles() / AT_MOST);
        double red = 0;
        double green = 0;
        double blue = 0;
        double all = 0;
        for (int triangle = 0; triangle < wood.triangles(); triangle += step) {
            final Vec3 middle = wood.on(triangle, 0).plus(wood.on(triangle, 1)).plus(wood.on(triangle, 2))
                    .times(1.0 / 3);
            final double area = wood.area(triangle);
            final double[] there = colour.at(seen.sample(middle, 0));
            red += area * there[0];
            green += area * there[1];
            blue += area * there[2];
            all += area;
        }
        if (all <= 0) {
            throw new IllegalArgumentException("wood with no bark has no colour");
        }
        return new Vec3(red / all, green / all, blue / all);
    }
}
