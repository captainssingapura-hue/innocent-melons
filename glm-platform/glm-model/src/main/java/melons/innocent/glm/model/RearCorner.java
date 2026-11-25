package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A rear corner's wheel end, inside the wheel: the wheel's carrier, cast, tall, inboard of the hub, a boss where each
 * link meets it and the caliper's mount behind; the hub, turning in the carrier on its bearing, its
 * flange for the wheel outboard; the brake's disc on the hub's flange, inside the rim; and the caliper, bolted to the
 * carrier, astride the disc behind the axle.
 *
 * @param points the rear axle's points
 * @param shapes what it is shaped with
 */
record RearCorner(RearPoints points, Shapes shapes) {

    /** What its parts are made of, and whether they are bare. */
    private static final Making CARRIER_MADE = new Making(new Made(Material.ALUMINIUM, true));
    private static final Making HUB_MADE = new Making(new Made(Material.STEEL, true));
    private static final Making DISC_MADE = new Making(new Made(Material.IRON, true));
    private static final Making CALIPER_MADE = new Making(new Made(Material.ALUMINIUM, false));

    /** The carrier: how wide, how far it reaches over and under the wheel's middle, how long; its bosses. */
    private static final double CARRIER_WIDE = 7;
    private static final double CARRIER_OVER = 24;
    private static final double CARRIER_UNDER = 20;
    private static final double CARRIER_LONG = 16;
    private static final double BOSS = 2.6;
    /** The hub, how round; the disc, how round and how thick; the caliper, how big. */
    private static final double HUB = 7.5;
    private static final double DISC = 17.5;
    private static final double DISC_THICK = 2;
    private static final double CALIPER_WIDE = 7;
    private static final double CALIPER_TALL = 10;
    private static final double CALIPER_LONG = 7;

    /** The wheel's carrier. */
    Part carrier() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("hub", points.hub());
        named.put("upper front", points.upperFrontOuter());
        named.put("upper rear", points.upperRearOuter());
        named.put("spring link", points.springOuter());
        named.put("lower front", points.lowerFrontOuter());
        named.put("toe link", points.toeOuter());
        named.put("caliper", points.caliper());
        final Vec3 wheel = points.wheel();
        final Vec3 middle = new Vec3(points.hub().x() - CARRIER_WIDE / 2, wheel.y() + (CARRIER_OVER - CARRIER_UNDER)
                / 2, wheel.z());
        final List<Mesh> forms = new ArrayList<>();
        forms.add(shapes.body(middle, CARRIER_WIDE, CARRIER_OVER + CARRIER_UNDER, CARRIER_LONG));
        named.values().stream().skip(1).forEach(p -> forms.add(shapes.capsule(p, middle.plus(p.minus(middle)
                .times(0.6)), BOSS)));
        return CARRIER_MADE.part("wheel carrier", Group.CARRIERS, true, named, shapes.joined(forms));
    }

    /** The hub, from the carrier out to its flange. */
    Part hub() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("carrier", points.hub());
        named.put("disc", points.disc());
        named.put("wheel", points.hubFace());
        return HUB_MADE.part("hub", Group.CARRIERS, true, named, shapes.drum(points.hub(), points.hubFace(), HUB));
    }

    /** The brake's disc, on the hub's flange. */
    Part disc() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("hub", points.disc());
        final Vec3 across = new Vec3(DISC_THICK / 2, 0, 0);
        return DISC_MADE.part("brake disc", Group.BRAKES, true, named,
                shapes.drum(points.disc().minus(across), points.disc().plus(across), DISC));
    }

    /** The caliper, astride the disc behind the axle. */
    Part caliper() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("carrier", points.caliper());
        final Vec3 wheel = points.wheel();
        final Vec3 middle = new Vec3(points.disc().x(), wheel.y() + CALIPER_TALL / 3, wheel.z() - DISC + 1);
        return CALIPER_MADE.part("brake caliper", Group.CALIPERS, true, named, shapes.joined(List.of(
                shapes.box(middle, CALIPER_WIDE, CALIPER_TALL, CALIPER_LONG),
                shapes.capsule(points.caliper(), middle, BOSS))));
    }
}
