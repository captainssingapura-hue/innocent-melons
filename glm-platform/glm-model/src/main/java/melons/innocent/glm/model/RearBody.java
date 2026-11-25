package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Swept;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The underbody's rear, the root the platform is put together from: a rail each side, running along over the rear
 * subframe's mounts, low ahead of the axle and rising over it to the back mounts, high; the floor between them, rising
 * with them; and a tower each side, outboard of its rail and clear of the wheel, holding the spring's top and the
 * damper's. Its points, both sides: the subframe's mounts, ahead of the axle and behind, under the rails, where the
 * bushes' sleeves are; the springs' tops; the dampers' tops.
 *
 * @param points the rear axle's points
 * @param shapes what it is shaped with
 */
record RearBody(RearPoints points, Shapes shapes) {

    /** How far the rails reach ahead of the axle and behind it; how wide and deep they are; how thick the floor is. */
    private static final double AHEAD = 90;
    private static final double BEHIND = 100;
    private static final double RAIL_WIDE = 8;
    private static final double RAIL_DEEP = 10;
    private static final double FLOOR = 1.5;
    /** How far over a mount's middle the rail is, where the sleeve's top and its bush are. */
    private static final double SEAT = 4;
    /** Where the rails rise, from ahead of the axle to behind it. */
    private static final double RISE_FROM = 22;
    private static final double RISE_TO = -18;
    /** How far the towers reach ahead of the axle and behind it, and how tall they are. */
    private static final double TOWER_AHEAD = 4;
    private static final double TOWER_BEHIND = 16;
    private static final double TOWER_TALL = 11;

    /** It. */
    Part part() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        for (final int side : List.of(1, -1)) {
            final String s = side > 0 ? ", right" : ", left";
            named.put("subframe mount, front" + s, mirrored(points.mountFront(), side));
            named.put("subframe mount, back" + s, mirrored(points.mountBack(), side));
            named.put("spring top" + s, mirrored(points.springTop(), side));
            named.put("damper top" + s, mirrored(points.damperTop(), side));
        }
        return new Making().root("underbody", Group.UNDERBODY, named, form());
    }

    /** Its rails, its floor and its towers. */
    private Mesh form() {
        final double rail = points.mountFront().x();
        final double tower = points.damperTop().x() + 1;
        final double back = points.wheel().z();
        final double top = points.springTop().y();
        final List<Mesh> forms = new ArrayList<>();
        final double floor = 2 * rail + RAIL_WIDE;
        final Section across = new Section(floor, FLOOR, 12);
        forms.add(new Swept(course(0, RAIL_DEEP + FLOOR / 2), List.of(across, across, across, across), 24).mesh());
        final Section box = new Section(RAIL_WIDE, RAIL_DEEP, 12);
        for (final int side : List.of(1, -1)) {
            forms.add(new Swept(course(side * rail, RAIL_DEEP / 2), List.of(box, box, box, box), 24).mesh());
            final double inner = rail + RAIL_WIDE / 2;
            forms.add(shapes.box(new Vec3(side * (inner + tower) / 2, top + TOWER_TALL / 2,
                    back + (TOWER_AHEAD - TOWER_BEHIND) / 2), tower - inner, TOWER_TALL, TOWER_AHEAD + TOWER_BEHIND));
        }
        return shapes.joined(forms);
    }

    /** A line along the underbody {@code x} across, {@code over} its rails' undersides, rising over the axle. */
    private Course course(final double x, final double over) {
        final double back = points.wheel().z();
        final double low = points.mountFront().y() + SEAT + over;
        final double high = points.mountBack().y() + SEAT + over;
        return new Course(List.of(new Vec3(x, low, back + AHEAD), new Vec3(x, low, back + RISE_FROM),
                new Vec3(x, high, back + RISE_TO), new Vec3(x, high, back - BEHIND)), 8);
    }

    private Vec3 mirrored(final Vec3 p, final int side) {
        return new Vec3(side * p.x(), p.y(), p.z());
    }
}
