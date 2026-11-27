package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.List;

/**
 * A twin's oil pan, cast in aluminium, closing its crankcase's flat bottom under the crank and the gearbox, where the
 * oil falls to be drawn back: a shallow tray, its floor under the bottom's whole width and length and its walls up to
 * the bottom's edge. Its point: the middle of its rim, where it is bolted to the crankcase.
 *
 * @param figures the engine's figures
 */
record OilPan(EngineFigures figures) {

    /** How deep it is; how thick it is cast. */
    private static final double DEEP = 2.5;
    private static final double THICK = 0.5;

    /** Where the crankcase's bottom is, and how far it runs, across the crank: from the countershaft to its front. */
    double[] bottom() {
        final GearboxFigures gearbox = new GearboxFigures(figures);
        return new double[] {gearbox.countershaft().u(), new UprightCase(figures, new BlockForm(figures)).skirt(),
            gearbox.bottom()};
    }

    /** Where its rim's middle is, on the crankcase's bottom. */
    Vec3 rim() {
        final double[] at = bottom();
        return new Vec3((at[0] + at[1]) / 2, at[2], figures.length() / 2);
    }

    /** It. */
    Part part() {
        final double[] at = bottom();
        final double across = at[1] - at[0];
        final double length = figures.length();
        final double low = at[2] - DEEP;
        final double middle = (at[0] + at[1]) / 2;
        final Shapes shapes = new Shapes(8);
        return new Part(figures.name() + " oil pan", Group.ENGINE, false, List.of(new Point("rim", rim())),
                shapes.joined(List.of(
                        shapes.box(new Vec3(middle, low + THICK / 2, length / 2), across, THICK, length),
                        shapes.box(new Vec3(at[0] + THICK / 2, low + DEEP / 2, length / 2), THICK, DEEP, length),
                        shapes.box(new Vec3(at[1] - THICK / 2, low + DEEP / 2, length / 2), THICK, DEEP, length),
                        shapes.box(new Vec3(middle, low + DEEP / 2, THICK / 2), across, DEEP, THICK),
                        shapes.box(new Vec3(middle, low + DEEP / 2, length - THICK / 2), across, DEEP, THICK))),
                new Made(Material.ALUMINIUM, true)).as(new EngineParts.OilSump());
    }
}
