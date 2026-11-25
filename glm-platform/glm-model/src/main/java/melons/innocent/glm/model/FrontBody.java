package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
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
 * The underbody's front, as much of it as the front subframe is fixed to: a rail each side, high ahead where the
 * front mount is bolted to its inner side, falling behind the axle to run over the back mount; and the floor of the
 * footwell between them, behind; and a strut tower each side, over the wheel. Its points, both sides: the subframe's
 * mounts, the upper links' bushes and the struts' tops; and where it is joined to the rest of the underbody, down the
 * middle.
 *
 * @param points the front axle's points
 */
record FrontBody(FrontPoints points) {

    /** What it is made of: pressed steel, painted. */
    private static final Making BODY = new Making(new Made(Material.STEEL, false));
    /** How wide the rails are, how deep ahead and behind; how far over a back mount's middle a rail's middle is. */
    private static final double RAIL_WIDE = 8;
    private static final double RAIL_AHEAD = 14;
    private static final double RAIL_BEHIND = 10;
    private static final double OVER = 9.5;
    /** The footwell's floor: how far behind the axle, how deep along the car and how thick. */
    private static final double FLOOR_AT = -40;
    private static final double FLOOR_DEEP = 30;
    private static final double FLOOR = 1.5;
    /** How much room a strut tower leaves round what it holds, and how long it is along the car. */
    private static final double TOWER_ROOM = 4;
    private static final double TOWER_LONG = 34;

    /** It, its own frame the platform's. */
    Part part() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("rest of the underbody", join());
        for (final int side : List.of(1, -1)) {
            final String s = side > 0 ? ", right" : ", left";
            named.put("front subframe mount, front" + s, mirrored(points.mountFront(), side));
            named.put("front subframe mount, back" + s, mirrored(points.mountBack(), side));
            named.put("upper front link" + s, mirrored(points.upperFrontInner(), side));
            named.put("upper rear link" + s, mirrored(points.upperRearInner(), side));
            named.put("strut top" + s, mirrored(points.strutTop(), side));
        }
        return BODY.root("front underbody", Group.UNDERBODY, named, form());
    }

    /** Where it is joined to the rest of the underbody: the middle of the footwell's floor. */
    Vec3 join() {
        return points.at(0, points.mountBack().y() - points.wheel().y() + OVER, FLOOR_AT);
    }

    private Mesh form() {
        final List<Mesh> forms = new ArrayList<>();
        final double inner = points.mountFront().x() + 1.4;
        final Section ahead = new Section(RAIL_WIDE, RAIL_AHEAD, 12);
        final Section behind = new Section(RAIL_WIDE, RAIL_BEHIND, 12);
        for (final int side : List.of(1, -1)) {
            final double front = side * (inner + RAIL_WIDE / 2);
            final double back = side * points.mountBack().x();
            final double high = points.mountFront().y() + 1;
            final double low = points.mountBack().y() + OVER;
            forms.add(new Swept(new Course(List.of(new Vec3(front, high, 95), new Vec3(front, high, 30),
                    new Vec3(back, low, -14), new Vec3(back, low, -55)), 8),
                    List.of(ahead, ahead, behind, behind), 24).mesh());
            forms.add(tower(side));
        }
        final Vec3 floor = join();
        final double across = 2 * points.mountBack().x() + RAIL_WIDE;
        final Section flat = new Section(FLOOR_DEEP, FLOOR, 12);
        forms.add(new Swept(new Course(List.of(floor.minus(new Vec3(across / 2, 0, 0)),
                floor.plus(new Vec3(across / 2, 0, 0))), 1), List.of(flat, flat), 24).mesh());
        return new Shapes(24).joined(forms);
    }

    /** A strut tower over the wheel, from over the upper links' bushes, which hang under it, past the strut's top. */
    private Mesh tower(final int side) {
        final double inner = points.upperFrontInner().x() - TOWER_ROOM;
        final double outer = points.strutTop().x() + TOWER_ROOM * 1.5;
        final double low = points.upperFrontInner().y() + TOWER_ROOM * 0.6;
        final double high = points.strutTop().y() + TOWER_ROOM;
        return new Shapes(24).box(new Vec3(side * (inner + outer) / 2, (low + high) / 2, points.strutTop().z()),
                outer - inner, high - low, TOWER_LONG);
    }

    private Vec3 mirrored(final Vec3 p, final int side) {
        return new Vec3(side * p.x(), p.y(), p.z());
    }
}
