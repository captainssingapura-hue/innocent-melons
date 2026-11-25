package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A corner's wheel end, front or rear, inside the wheel: the wheel's carrier, cast, tall, inboard of the hub, a boss
 * where each link meets it and the caliper's mount behind; the hub, turning in the carrier on its bearing, its flange
 * for the wheel outboard; the brake's disc on the hub's flange, inside the rim; and the caliper, bolted to the
 * carrier, astride the disc behind the axle.
 *
 * @param points the corner's points
 * @param shapes what it is shaped with
 */
record Corner(WheelEnd points, Shapes shapes) {

    /** What its parts are made of, and whether they are bare. */
    private static final Making CARRIER_MADE = new Making(new Made(Material.ALUMINIUM, true));
    private static final Making HUB_MADE = new Making(new Made(Material.STEEL, true));
    private static final Making DISC_MADE = new Making(new Made(Material.IRON, true));
    private static final Making CALIPER_MADE = new Making(new Made(Material.ALUMINIUM, false));

    /** The carrier's arms, forged slim or cast stouter; its caliper's boss; how many points round. */
    private static final Section FORGED_ARM = new Section(3.4, 4.2, 2.6);
    private static final Section CAST_ARM = new Section(4.2, 4.8, 3);
    private static final double BOSS = 2.6;
    private static final int ROUND = 24;
    /** The brake's shield, pressed steel: how far out past the disc and in round the ring, its gap at the caliper. */
    private static final Making SHIELD_MADE = new Making(new Made(Material.STEEL, false));
    private static final double SHIELD_PAST = 1.5;
    private static final double SHIELD_IN = 8;
    private static final double SHIELD_GAP = 40;
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
        named.putAll(points.carried());
        named.put("caliper", points.caliper());
        final Section arm = points.forged() ? FORGED_ARM : CAST_ARM;
        return CARRIER_MADE.part(points.called() + "wheel carrier", Group.CARRIERS, true, named,
                new CarrierForm(points, arm, ROUND).mesh());
    }

    /** The hub, from the carrier out to its flange. */
    Part hub() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("carrier", points.hub());
        named.put("disc", points.disc());
        named.put("wheel", points.hubFace());
        return HUB_MADE.part(points.called() + "hub", Group.CARRIERS, true, named,
                shapes.drum(points.hub(), points.hubFace(), HUB));
    }

    /** The brake's disc, on the hub's flange. */
    Part disc() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("hub", points.disc());
        final Vec3 across = new Vec3(DISC_THICK / 2, 0, 0);
        return DISC_MADE.part(points.called() + "brake disc", Group.BRAKES, true, named,
                shapes.drum(points.disc().minus(across), points.disc().plus(across), DISC));
    }

    /**
     * The brake's shield, pressed steel, between the carrier and the disc: a ring of sheet from round the carrier's
     * ring out past the disc, open where the caliper sits.
     */
    Part shield() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("carrier", points.hub());
        final Vec3 at = points.disc().minus(new Vec3(DISC_THICK + 0.6, 0, 0));
        final Vec3 off = points.caliper().minus(points.wheel());
        final double gap = Math.toDegrees(Math.atan2(off.y(), off.z()));
        final List<Spot> outline = new ArrayList<>();
        final int steps = 36;
        for (int k = 0; k <= steps; k++) {
            final double a = Math.toRadians(gap + SHIELD_GAP + (360 - 2 * SHIELD_GAP) * k / steps);
            outline.add(new Spot((DISC + SHIELD_PAST) * Math.cos(a), (DISC + SHIELD_PAST) * Math.sin(a)));
        }
        for (int k = steps; k >= 0; k--) {
            final double a = Math.toRadians(gap + SHIELD_GAP + (360 - 2 * SHIELD_GAP) * k / steps);
            outline.add(new Spot(SHIELD_IN * Math.cos(a), SHIELD_IN * Math.sin(a)));
        }
        return SHIELD_MADE.part(points.called() + "brake shield", Group.BRAKES, true, named,
                new Plate(outline, List.of(), new Vec3(at.x(), points.wheel().y(), points.wheel().z()),
                        new Vec3(0, 0, 1), new Vec3(0, 1, 0), 0.15).mesh());
    }

    /** The caliper, astride the disc behind the axle. */
    Part caliper() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("carrier", points.caliper());
        final Vec3 wheel = points.wheel();
        final Vec3 middle = new Vec3(points.disc().x(), wheel.y() + CALIPER_TALL / 3, wheel.z() - DISC + 1);
        return CALIPER_MADE.part(points.called() + "brake caliper", Group.CALIPERS, true, named, shapes.joined(List.of(
                shapes.box(middle, CALIPER_WIDE, CALIPER_TALL, CALIPER_LONG),
                shapes.capsule(points.caliper(), middle, BOSS))));
    }
}
