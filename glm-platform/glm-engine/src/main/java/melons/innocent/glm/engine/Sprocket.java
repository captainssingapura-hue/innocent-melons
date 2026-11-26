package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A chain's sprocket, cut from steel plate: so many teeth round it, its chain's line so far round, each tooth rising
 * from the roots either side of it to its flat tip past the line, as far past it as the roots are short of it.
 *
 * @param teeth how many teeth it has
 * @param round how far round it is to its chain's line
 * @param wide  how thick it is
 */
record Sprocket(int teeth, double round, double wide) {

    /** How far a tooth's tip stands past the chain's line, and its root short of it. */
    static final double TOOTH = 0.25;

    /** It, its middle at {@code middle}, square to the crank, turned {@code turned} degrees on toward +x from up. */
    Mesh mesh(final Vec3 middle, final double turned) {
        final List<Spot> outline = new ArrayList<>();
        final double each = 2 * Math.PI / teeth;
        final double from = Math.toRadians(90 - turned);
        for (int k = 0; k < teeth; k++) {
            final double a = from + k * each;
            outline.add(at(a, round - TOOTH));
            outline.add(at(a + each * 0.35, round + TOOTH));
            outline.add(at(a + each * 0.65, round + TOOTH));
        }
        return new Plate(outline, List.of(), middle, new Vec3(1, 0, 0), new Vec3(0, 1, 0), wide).mesh();
    }

    private Spot at(final double a, final double r) {
        return new Spot(r * Math.cos(a), r * Math.sin(a));
    }
}
