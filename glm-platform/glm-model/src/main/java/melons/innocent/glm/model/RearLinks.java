package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The rear axle's five links each side, each a bar from its pivot on the subframe, inboard, out to the wheel's carrier,
 * round-ended where it is joined: the two upper links, slim, ahead of the axle and behind; the spring's link, the
 * stoutest, under the axle, its seat for the spring three fifths of the way out; the lower front link; and the toe
 * link, slimmest, behind. Each with its points {@code inner} and {@code outer}, and the spring's its seat besides.
 *
 * @param points the rear axle's points
 * @param shapes what it is shaped with
 */
record RearLinks(RearPoints points, Shapes shapes) {

    /** How thick each link is, from its middle line out; how wide the spring's seat is, and how deep. */
    private static final double UPPER = 1.6;
    private static final double SPRING = 2.6;
    private static final double LOWER = 2.0;
    private static final double TOE = 1.3;
    private static final double SEAT = 7.5;
    private static final double SEAT_DEEP = 2;

    /** The upper front link. */
    Part upperFront() {
        return link("upper front link", points.upperFrontInner(), points.upperFrontOuter(), UPPER);
    }

    /** The upper rear link. */
    Part upperRear() {
        return link("upper rear link", points.upperRearInner(), points.upperRearOuter(), UPPER);
    }

    /** The spring's link, its seat on it. */
    Part spring() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", points.springInner());
        named.put("outer", points.springOuter());
        named.put("spring seat", points.springSeat());
        final Mesh form = shapes.joined(List.of(shapes.capsule(points.springInner(), points.springOuter(), SPRING),
                shapes.drum(points.springSeat(), points.springSeat().plus(new Vec3(0, SEAT_DEEP, 0)), SEAT)));
        return new Making().part("spring link", Group.LINKS, true, named, form);
    }

    /** The lower front link. */
    Part lowerFront() {
        return link("lower front link", points.lowerFrontInner(), points.lowerFrontOuter(), LOWER);
    }

    /** The toe link. */
    Part toe() {
        return link("toe link", points.toeInner(), points.toeOuter(), TOE);
    }

    private Part link(final String name, final Vec3 inner, final Vec3 outer, final double thick) {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", inner);
        named.put("outer", outer);
        return new Making().part(name, Group.LINKS, true, named, shapes.capsule(inner, outer, thick));
    }
}
