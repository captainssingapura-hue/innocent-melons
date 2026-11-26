package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * A twin's alternator cover, cast in aluminium, on the bike's right, at the crankcase's back end: a round cup over
 * the crank's end, where the alternator's rotor turns, its wall out from the crankcase and its face across it. Its
 * point: the crank's axis at the crankcase's back end, where it is bolted.
 *
 * @param figures the engine's figures
 */
record AlternatorCover(EngineFigures figures) {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    private static final int ROUND = 40;
    /** How far round it is, how far out it stands past the crankcase, and how thick it is cast. */
    static final double ROUND_OUT = 8;
    static final double OUT = 4.5;
    private static final double THICK = 0.4;

    /** It. */
    Part part() {
        final double end = figures.length();
        final Shapes shapes = new Shapes(ROUND);
        return new Part(figures.name() + " alternator cover", Group.COVERS, false,
                List.of(new Point("crank", ALONG.times(end))), shapes.joined(List.of(
                        new Sleeve(ALONG.times(end + OUT / 2), ALONG, OUT, ROUND_OUT, ROUND_OUT - THICK, ROUND).mesh(),
                        new Plate(new Outlines(ROUND).circle(new Spot(0, 0), ROUND_OUT), List.of(),
                                ALONG.times(end + OUT), new Vec3(1, 0, 0), new Vec3(0, 1, 0), THICK).mesh())),
                new Made(Material.ALUMINIUM, true)).as(new EngineParts.AlternatorCover());
    }
}
