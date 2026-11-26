package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
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
 * A motorcycle's back end, as its figures and frames place it ({@link BikeFigures}, {@link Frames}): the swingarm,
 * steel, an arm each side from its pivot tube in the mid-structure back to the back axle, outside the belt and the
 * brake, a tube across them ahead of the tyre; and a shock each side, its spring round its damper, from a lug on its
 * arm near the axle up to the subframe's rail.
 *
 * @param figures the bike's figures
 * @param frames  its frames
 */
record RearEnd(BikeFigures figures, Frames frames) {

    private static final int ROUND = 16;
    /** How far round an arm is; how far along it toward the axle a shock's lug is; how far round a shock. */
    private static final double ARM = 1.9;
    private static final double LUG = 0.82;
    private static final double SPRING = 2.4;
    private static final double BODY = 1.6;

    /** The swingarm. */
    Part swingarm() {
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>();
        for (final int side : List.of(-1, 1)) {
            made.add(shapes.drum(arm(0, side), arm(1, side), ARM));
            made.add(shapes.drum(arm(LUG, side), arm(LUG, side).plus(new Vec3(0, 0, side * 2)), 1.2));
        }
        made.add(shapes.drum(arm(0, -1), arm(0, 1), 2.2));
        made.add(shapes.drum(arm(0.3, -1), arm(0.3, 1), 1.5));
        return new Part("swingarm", Group.FRAME, false, List.of(new Point("pivot", figures.pivot()),
                new Point("axle", figures.rearAxle()), new Point("shock, left", lug(-1)),
                new Point("shock, right", lug(1))), shapes.joined(made), new Made(Material.STEEL, false))
                .as(new MotorcycleParts.Swingarm());
    }

    /** The shock on side {@code side}, -1 the left: its spring round its damper, from its lug up to the subframe. */
    Part shock(final int side) {
        final Shapes shapes = new Shapes(ROUND);
        final Vec3 low = lug(side);
        final Vec3 high = top(side);
        final Vec3 mid = low.plus(high).times(0.5);
        return new Part("rear shock, " + (side < 0 ? "left" : "right"), Group.SPRINGS, false, List.of(
                new Point("lug", low), new Point("subframe", high)), shapes.joined(List.of(
                        shapes.drum(low, mid, BODY), shapes.drum(mid.minus(high.minus(low).times(0.2)), high, SPRING),
                        shapes.drum(low.minus(new Vec3(0, 0, 1.4)), low.plus(new Vec3(0, 0, 1.4)), 1.3),
                        shapes.drum(high.minus(new Vec3(0, 0, 1.4)), high.plus(new Vec3(0, 0, 1.4)), 1.3))),
                new Made(Material.STEEL, true)).as(new MotorcycleParts.RearShock());
    }

    /** The place {@code t} of the way along the arm on side {@code side}, from the pivot to the axle. */
    Vec3 arm(final double t, final int side) {
        final Vec3 from = figures.pivot();
        final Vec3 to = figures.rearAxle();
        return from.plus(to.minus(from).times(t)).plus(new Vec3(0, 0, side * Frames.ARMS));
    }

    /** Where the shock's lug on side {@code side} is, outside its arm. */
    Vec3 lug(final int side) {
        return arm(LUG, side).plus(new Vec3(0, 0, side * 2));
    }

    /** Where the shock's top on side {@code side} is, on the subframe's rail, outside it. */
    Vec3 top(final int side) {
        return frames.shockTop(side);
    }
}
