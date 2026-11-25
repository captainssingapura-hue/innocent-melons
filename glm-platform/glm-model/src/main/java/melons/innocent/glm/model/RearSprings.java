package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A rear corner's spring and damper: the air spring, a rounded bellows standing on the spring link's seat up to its
 * top on the body; and the damper, from its foot on the carrier behind the axle up to its top in the body's tower, its
 * body stout below and its rod slim above.
 *
 * @param points the rear axle's points
 * @param shapes what it is shaped with
 */
record RearSprings(RearPoints points, Shapes shapes) {

    /** What its parts are made of, and whether they are bare. */
    private static final Making AIR_MADE = new Making(new Made(Material.RUBBER, true));
    private static final Making DAMPER_MADE = new Making(new Made(Material.STEEL, false));

    /** How round the air spring is; how round the damper's body is and its rod; how much of it is body. */
    private static final double BELLOWS = 7;
    private static final double BODY = 3.2;
    private static final double ROD = 1.2;
    private static final double BODY_SHARE = 0.55;

    /** The air spring. */
    Part spring() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("seat", points.springSeat());
        named.put("top", points.springTop());
        final Vec3 lift = new Vec3(0, BELLOWS, 0);
        return AIR_MADE.part("air spring", Group.SPRINGS, true, named,
                shapes.capsule(points.springSeat().plus(lift), points.springTop().minus(lift), BELLOWS));
    }

    /** The damper. */
    Part damper() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("foot", points.damperFoot());
        named.put("top", points.damperTop());
        final Vec3 foot = points.damperFoot();
        final Vec3 knee = foot.plus(points.damperTop().minus(foot).times(BODY_SHARE));
        return DAMPER_MADE.part("damper", Group.DAMPERS, true, named, shapes.joined(List.of(
                shapes.capsule(foot, knee, BODY), shapes.drum(knee, points.damperTop(), ROD))));
    }
}
