package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The underbody's rear, the root the platform is put together from: a rail each side, running along over the rear
 * subframe's mounts; the floor between them, over the axle; and a tower each side, outboard of its rail and clear of
 * the wheel, holding the spring's top and the damper's. Its points, both sides: the subframe's mounts, ahead of the
 * axle and behind, under the rails; the springs' tops; the dampers' tops.
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
        final double under = points.mountFront().y();
        final double back = points.wheel().z();
        final double along = AHEAD + BEHIND;
        final double middle = back + (AHEAD - BEHIND) / 2;
        final double rail = points.mountFront().x();
        final double tower = points.damperTop().x() + 1;
        final List<Mesh> forms = new ArrayList<>();
        for (final int side : List.of(1, -1)) {
            forms.add(shapes.box(new Vec3(side * rail, under + RAIL_DEEP / 2, middle), RAIL_WIDE, RAIL_DEEP, along));
            final double inner = rail + RAIL_WIDE / 2;
            forms.add(shapes.box(new Vec3(side * (inner + tower) / 2, under + TOWER_TALL / 2,
                    back + (TOWER_AHEAD - TOWER_BEHIND) / 2), tower - inner, TOWER_TALL, TOWER_AHEAD + TOWER_BEHIND));
        }
        forms.add(shapes.box(new Vec3(0, under + RAIL_DEEP + FLOOR / 2, middle), 2 * rail + RAIL_WIDE, FLOOR, along));
        return shapes.joined(forms);
    }

    private Vec3 mirrored(final Vec3 p, final int side) {
        return new Vec3(side * p.x(), p.y(), p.z());
    }
}
