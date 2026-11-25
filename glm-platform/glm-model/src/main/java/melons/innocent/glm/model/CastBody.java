package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;
import melons.innocent.vehiclelab.forms.Swept;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The GLM platform's body, cast whole as one piece, the part of the platform that makes it one car or another: a
 * casting no one makes yet, in titanium, bare, laid out from the platform's figures and the body's own. Its members
 * as its lines run them ({@link BodyLines}); its panels: the bulkhead across the windscreen's foot, the floor between
 * the sills and the boot's floor behind; the four wheel arches' inner skins; the front towers each holding its air
 * strut's top and its upper links' bushes; the rear towers each holding its spring's cup and its damper's top; and a
 * pad over each subframe mount. Its points, both sides: every mount the chassis is fixed to it by.
 *
 * @param lines its lines
 */
record CastBody(BodyLines lines) {

    private static final Making CAST = new Making(new Made(Material.TITANIUM, true));
    private static final int ROUND = 16;
    private static final Vec3 ACROSS = new Vec3(1, 0, 0);
    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How thick its panels are cast; how far its arches stand off the tyre and how wide they are. */
    private static final double SKIN = 0.4;
    private static final double ARCH_OFF = 4;
    private static final double ARCH_WIDE = 11;

    /** It, its own frame the platform's. */
    Part part() {
        final FrontPoints front = lines.front();
        final RearPoints rear = lines.rear();
        final Map<String, Vec3> named = new LinkedHashMap<>();
        for (final int side : List.of(1, -1)) {
            final String s = side > 0 ? ", right" : ", left";
            named.put("subframe mount, front" + s, mirrored(rear.mountFront(), side));
            named.put("subframe mount, back" + s, mirrored(rear.mountBack(), side));
            named.put("spring top" + s, mirrored(rear.springTop(), side));
            named.put("damper top" + s, mirrored(rear.damperTop(), side));
            named.put("front subframe mount, front" + s, mirrored(front.mountFront(), side));
            named.put("front subframe mount, back" + s, mirrored(front.mountBack(), side));
            named.put("upper front link" + s, mirrored(front.upperFrontInner(), side));
            named.put("upper rear link" + s, mirrored(front.upperRearInner(), side));
            named.put("strut top" + s, mirrored(front.strutTop(), side));
        }
        return CAST.root("cast body", Group.UNDERBODY, named, form());
    }

    private Mesh form() {
        final List<Mesh> made = new ArrayList<>();
        for (final BodyLines.Member m : lines.sides()) {
            for (final int side : List.of(1, -1)) {
                made.add(member(m.through().stream().map(p -> mirrored(p, side)).toList(), m));
            }
        }
        lines.across().forEach(m -> made.add(member(m.through(), m)));
        made.add(member(lines.tunnel().through(), lines.tunnel()));
        made.add(floor());
        made.add(bulkhead());
        made.add(bootFloor());
        final Shapes shapes = new Shapes(ROUND);
        for (final int side : List.of(1, -1)) {
            for (final double z : List.of(0.0, lines.rearAxle())) {
                final double x = lines.proportions().halfTrack() - lines.proportions().width() * 0.1 / 2 - 1
                        - ARCH_WIDE / 2;
                made.add(shapes.band(new Vec3(side * x, lines.proportions().tyre(), z),
                        lines.proportions().tyre() + ARCH_OFF, SKIN, ARCH_WIDE, -15, 195));
            }
            made.addAll(frontTower(side));
            made.addAll(rearTower(side));
            for (final Vec3 mount : List.of(lines.rear().mountFront(), lines.rear().mountBack(),
                    lines.front().mountBack())) {
                made.add(new Sleeve(mirrored(mount, side).plus(UP.times(5)), UP, 2, 5, 1, ROUND).mesh());
            }
        }
        return shapes.joined(made);
    }

    private Mesh member(final List<Vec3> through, final BodyLines.Member m) {
        return new Swept(new Course(through, 8), through.stream().map(p -> m.section()).toList(), ROUND).mesh();
    }

    /** The floor, between the sills, from the bulkhead to the rear seat's member. */
    private Mesh floor() {
        final double x = lines.sillX() - 5;
        return new Plate(rectangle(-x, lines.cowlZ() - 2, x, lines.rearAxle() + 62), List.of(),
                new Vec3(0, lines.floorY(), 0), ACROSS, ALONG, SKIN).mesh();
    }

    /** The bulkhead across the windscreen's foot, from the floor up to the cowl. */
    private Mesh bulkhead() {
        final double x = lines.sillX() - 6;
        return new Plate(rectangle(-x, lines.floorY(), x, 93), List.of(), new Vec3(0, 0, lines.cowlZ()), ACROSS, UP,
                SKIN).mesh();
    }

    /** The boot's floor, between the rear rails, behind the axle. */
    private Mesh bootFloor() {
        final RearPoints rear = lines.rear();
        final double x = rear.mountBack().x() + 4;
        final double y = rear.mountBack().y() + 4 + 12 + SKIN;
        return new Plate(rectangle(-x, lines.tailZ() + 2, x, lines.rearAxle() - 20), List.of(), new Vec3(0, y, 0),
                ACROSS, ALONG, SKIN).mesh();
    }

    /** A front tower: its top holding the strut's top, its walls down to the rail, the links' bushes inside. */
    private List<Mesh> frontTower(final int side) {
        final FrontPoints front = lines.front();
        final Vec3 top = front.strutTop();
        final double in = front.upperFrontInner().x() - 4;
        final double out = top.x() + 6;
        final double high = top.y() + 2;
        final double low = front.mountFront().y() + 7;
        final double half = Math.abs(front.upperFrontInner().z()) + 4;
        return tower(side, in, out, low, high, top.z() - half, top.z() + half, List.of(top), 4);
    }

    /** A rear tower: its top holding the damper's top, the spring's cup under it, its walls down to the rail. */
    private List<Mesh> rearTower(final int side) {
        final RearPoints rear = lines.rear();
        final Vec3 spring = rear.springTop();
        final Vec3 damper = rear.damperTop();
        final double high = damper.y() + 1.5;
        final double low = rear.mountBack().y() + 4 + 12;
        final List<Mesh> made = new ArrayList<>(tower(side, spring.x() - 9, damper.x() + 5, low, high,
                Math.min(spring.z(), damper.z()) - 10, Math.max(spring.z(), damper.z()) + 10, List.of(damper), 2.5));
        final Vec3 cup = mirrored(spring, side);
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
                ACROSS, ALONG, SKIN).mesh());
        made.add(new Plate(rectangle(back, low, ahead, high), List.of(), new Vec3(a, 0, 0), ALONG, UP, SKIN)
                .mesh());
        for (final double z : List.of(back, ahead)) {
            made.add(new Plate(rectangle(Math.min(a, b), low, Math.max(a, b), high), List.of(), new Vec3(0, 0, z),
                    ACROSS, UP, SKIN).mesh());
        }
        return made;
    }

    private List<Spot> rectangle(final double u0, final double v0, final double u1, final double v1) {
        return List.of(new Spot(u0, v0), new Spot(u1, v0), new Spot(u1, v1), new Spot(u0, v1));
    }

    private Vec3 mirrored(final Vec3 p, final int side) {
        return new Vec3(side * p.x(), p.y(), p.z());
    }
}
