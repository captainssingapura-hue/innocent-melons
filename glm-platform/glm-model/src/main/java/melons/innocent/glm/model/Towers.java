package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The towers a body holds its suspension's tops in, either side, {@code skin} thick: each a box of panels, its top
 * holed where it holds a top, its walls down to the rail below. Ahead, over the front axle, the strut's top in its
 * top and the upper links' bushes inside it; behind, over the rear axle, the damper's top in its top and the spring's
 * cup under it.
 *
 * @param front the front axle's points
 * @param rear  the rear axle's points
 * @param skin  how thick its panels are
 */
record Towers(FrontPoints front, RearPoints rear, double skin) {

    private static final int ROUND = 16;
    private static final Vec3 ACROSS = new Vec3(1, 0, 0);
    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final Vec3 ALONG = new Vec3(0, 0, 1);

    /** A front tower, on the right if {@code side} is 1, the left if -1. */
    List<Mesh> front(final int side) {
        final Vec3 top = front.strutTop();
        final double in = front.upperFrontInner().x() - 4;
        final double out = top.x() + 6;
        final double high = top.y() + 2;
        final double low = front.mountFront().y() + 7;
        final double half = Math.abs(front.upperFrontInner().z()) + 4;
        return tower(side, in, out, low, high, top.z() - half, top.z() + half, List.of(top), 4);
    }

    /** A rear tower, on the right if {@code side} is 1, the left if -1. */
    List<Mesh> rear(final int side) {
        final Vec3 spring = rear.springTop();
        final Vec3 damper = rear.damperTop();
        final double high = damper.y() + 1.5;
        final double low = rear.mountBack().y() + 4 + 12;
        final List<Mesh> made = new ArrayList<>(tower(side, spring.x() - 9, damper.x() + 5, low, high,
                Math.min(spring.z(), damper.z()) - 10, Math.max(spring.z(), damper.z()) + 10, List.of(damper), 2.5));
        final Vec3 cup = new Vec3(side * spring.x(), spring.y(), spring.z());
        made.add(new Sleeve(cup.plus(UP.times((high - spring.y()) / 2)), UP, high - spring.y(), 7, 6.2, ROUND)
                .mesh());
        return made;
    }

    /** A tower over {@code in} to {@code out} across and {@code back} to {@code ahead}, holed where it holds. */
    private List<Mesh> tower(final int side, final double in, final double out, final double low, final double high,
                             final double back, final double ahead, final List<Vec3> holds, final double hole) {
        final Outlines outlines = new Outlines(ROUND);
        final List<List<Spot>> holes = holds.stream().map(h -> outlines.circle(new Spot(side * h.x(), h.z()), hole))
                .toList();
        final double a = side * in;
        final double b = side * out;
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(rectangle(Math.min(a, b), back, Math.max(a, b), ahead), holes, new Vec3(0, high, 0),
                ACROSS, ALONG, skin).mesh());
        made.add(new Plate(rectangle(back, low, ahead, high), List.of(), new Vec3(a, 0, 0), ALONG, UP, skin)
                .mesh());
        for (final double z : List.of(back, ahead)) {
            made.add(new Plate(rectangle(Math.min(a, b), low, Math.max(a, b), high), List.of(), new Vec3(0, 0, z),
                    ACROSS, UP, skin).mesh());
        }
        return made;
    }

    private List<Spot> rectangle(final double u0, final double v0, final double u1, final double v1) {
        return List.of(new Spot(u0, v0), new Spot(u1, v0), new Spot(u1, v1), new Spot(u0, v1));
    }
}
