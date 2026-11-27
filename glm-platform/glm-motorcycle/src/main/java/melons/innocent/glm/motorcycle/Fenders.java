package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * A motorcycle's fenders, short as the reference bike's are: the front one a band over the front tyre, from a little
 * ahead of the axle back to the fork's legs, held to each leg by a bracket; the back one a band over the back tyre,
 * from over its front back to behind it, held to the subframe's rails over it by a strut each side.
 *
 * @param figures the bike's figures
 * @param frames  its frames
 */
record Fenders(BikeFigures figures, Frames frames) {

    private static final int ROUND = 24;
    /** How far a fender stands off its tyre, how thick it is, and how much wider than its tyre. */
    private static final double OFF = 3;
    private static final double THICK = 0.4;
    private static final double WIDER = 2;
    /** Where each fender runs from and to, in degrees round from ahead toward up. */
    private static final double FRONT_FROM = 30;
    private static final double FRONT_TO = 120;
    private static final double REAR_FROM = 45;
    private static final double REAR_TO = 160;

    /** The front fender, on the fork's legs. */
    Part front() {
        final Vec3 axle = figures.frontAxle();
        final double reach = BikeFigures.FRONT.radius() + OFF;
        final double wide = BikeFigures.FRONT.wide() + WIDER;
        final Shapes shapes = new Shapes(12);
        final List<Mesh> made = new ArrayList<>(List.of(band(axle, reach, wide, FRONT_FROM, FRONT_TO)));
        final Vec3 at = figures.leg(reach + THICK / 2);
        for (final int side : List.of(-1, 1)) {
            made.add(shapes.drum(at.plus(new Vec3(0, 0, side * (wide / 2 - 0.2))),
                    at.plus(new Vec3(0, 0, side * (BikeFigures.LEGS / 2 - 1.8))), 0.8));
        }
        return new Part("front fender", Group.BODYWORK, false, List.of(
                new Point("fork", axle.plus(new Vec3(0, 0, -BikeFigures.LEGS / 2))),
                new Point("fork, right", axle.plus(new Vec3(0, 0, BikeFigures.LEGS / 2)))), shapes.joined(made),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.FrontFender());
    }

    /** The back fender, on the subframe. */
    Part rear() {
        final Vec3 axle = figures.rearAxle();
        final double reach = BikeFigures.REAR.radius() + OFF;
        final double wide = BikeFigures.REAR.wide() + WIDER;
        final Shapes shapes = new Shapes(12);
        final List<Mesh> made = new ArrayList<>(List.of(band(axle, reach, wide, REAR_FROM, REAR_TO)));
        final double up = Math.toRadians(100);
        final Vec3 top = axle.plus(new Vec3(Math.cos(up), Math.sin(up), 0).times(reach + THICK));
        final Subframe subframe = frames.subframe();
        final double rail = subframe.top(top.x()) - Subframe.RAIL;
        for (final int side : List.of(-1, 1)) {
            made.add(shapes.drum(new Vec3(top.x(), top.y(), figures.centre() + side * (wide / 2 - 0.5)),
                    new Vec3(top.x(), rail, figures.centre() + side * (Subframe.RAILS - 1.2)), 0.7));
        }
        return new Part("rear fender", Group.BODYWORK, false, List.of(new Point("subframe", subframe.tail())),
                shapes.joined(made), new Made(Material.STEEL, false)).as(new MotorcycleParts.RearFender());
    }

    /** A band round the axle at {@code axle}, made round the lab's axis across, turned to the bike's. */
    private Mesh band(final Vec3 axle, final double reach, final double wide, final double from, final double to) {
        final Mesh band = new Shapes(ROUND).band(new Vec3(0, 0, 0), reach, THICK, wide, from, to);
        final Mat4 at = new Mat4(axle).times(new Turned(new Vec3(0, 1, 0), 90).matrix());
        return new Merged(List.of(new Merged.Placed(band, at))).mesh();
    }

}
