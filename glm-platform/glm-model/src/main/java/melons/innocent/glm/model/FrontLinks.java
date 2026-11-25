package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.BallJoint;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The front axle's links each side, after the current large SUV's, placed as its workshop manual draws them, every
 * one forged aluminium, bare, but the tie rod. The control arm, an I beam bushed at both ends, its boss for the
 * shock absorber's fork part way out, on a steel ball joint of its own in the wheel bearing housing; the guide link,
 * strongly bowed, its big bush bolted in the subframe's front mount and its ball pin pressed into its outer end; the
 * two upper links, slim, bushed to the strut tower and each with its ball pin pressed in at the housing; and the tie
 * rod, steel, from the steering gear's rack to the housing's arm, a ball joint at each end. Every figure an estimate.
 *
 * @param points the front axle's points
 * @param shapes what it is shaped with
 */
record FrontLinks(FrontPoints points, Shapes shapes) {

    private static final Making FORGED = new Making(new Made(Material.ALUMINIUM, true));
    private static final Making STEEL = new Making(new Made(Material.STEEL, true));
    private static final int ROUND = 24;
    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final LinkForms.Beam ARM = new LinkForms.Beam(3.0, 4.2, 1.0, 0.8);
    private static final LinkForms.Beam GUIDE = new LinkForms.Beam(3.2, 4.4, 1.0, 0.8);
    private static final LinkForms.Beam UPPER = new LinkForms.Beam(2.4, 3.0, 0.8, 0.6);

    private LinkForms forms() {
        return new LinkForms(ROUND);
    }

    /** The control arm, its boss for the fork part way out. */
    Part controlArm() {
        final Vec3 in = points.controlArmInner();
        final Vec3 out = points.controlArmOuter();
        final Vec3 bolt = forms().bolt(in, out);
        final Mesh form = shapes.joined(List.of(
                forms().link(in, out, new Vec3(0, -1.5, 0), new LinkForms.Eye(5.4, 2.8, 2.0),
                        new LinkForms.Eye(5, 3.0, 2.2), ARM),
                new Sleeve(points.fork(), bolt, 5.4, 2.6, 1.6, ROUND).mesh()));
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", in);
        named.put("outer", out);
        named.put("fork", points.fork());
        return FORGED.part("front control arm", Group.LINKS, true, named, form);
    }

    /** The control arm's ball joint, bolted in the housing, the arm's outer bush on it. */
    Part controlArmBallJoint() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("ball", points.controlArmOuter());
        return STEEL.part("front control arm ball joint", Group.LINKS, true, named,
                new BallJoint(points.controlArmOuter(), UP, 1.8, 9, ROUND).mesh());
    }

    /** The guide link: its big bush across the car in the front mount, its ball pin up into the housing. */
    Part guideLink() {
        final Vec3 in = points.guideInner();
        final Vec3 out = points.guideOuter();
        final Mesh form = shapes.joined(List.of(
                forms().beam(in, 4.2, out, 2.6, forms().bolt(in, out).times(-6), GUIDE),
                new Sleeve(in, new Vec3(1, 0, 0), 7, 4.2, 3.0, ROUND).mesh(),
                new Sleeve(out, UP, 3.4, 2.6, 1.4, ROUND).mesh(),
                new BallJoint(out, UP, 1.6, 8, ROUND).mesh()));
        return link("guide link", in, out, form);
    }

    /** The upper front link. */
    Part upperFront() {
        return upper("upper front link", points.upperFrontInner(), points.upperFrontOuter(), 1.5);
    }

    /** The upper rear link. */
    Part upperRear() {
        return upper("upper rear link", points.upperRearInner(), points.upperRearOuter(), -1.5);
    }

    /** A tie rod: its inner ball joint on the rack, the rod, its outer ball joint in the housing's arm. */
    Part tieRod() {
        final Vec3 in = points.rackEnd();
        final Vec3 out = points.tieRodOuter();
        final Vec3 way = out.minus(in).unit();
        final List<Mesh> made = new ArrayList<>();
        made.add(shapes.capsule(in, in.plus(way.times(4)), 1.9));
        made.add(shapes.drum(in, out.minus(way.times(3)), 0.95));
        made.add(shapes.drum(out.minus(way.times(7)), out.minus(way.times(1)), 1.3));
        made.add(new BallJoint(out, UP, 1.3, 6, ROUND).mesh());
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", in);
        named.put("outer", out);
        return STEEL.part("tie rod", Group.STEERING, true, named, shapes.joined(made));
    }

    /** An upper link, bowed {@code bow} in plan, its ball pin pressed in at the housing, pointing down into it. */
    private Part upper(final String name, final Vec3 in, final Vec3 out, final double bow) {
        final Mesh form = shapes.joined(List.of(
                forms().beam(in, 2.4, out, 2.2, forms().bolt(in, out).times(bow), UPPER),
                new Sleeve(in, forms().bolt(in, out), 4.6, 2.4, 1.6, ROUND).mesh(),
                new Sleeve(out, UP, 3, 2.2, 1.2, ROUND).mesh(),
                new BallJoint(out, UP.times(-1), 1.3, 6, ROUND).mesh()));
        return link(name, in, out, form);
    }

    private Part link(final String name, final Vec3 inner, final Vec3 outer, final Mesh form) {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", inner);
        named.put("outer", outer);
        return FORGED.part("front " + name, Group.LINKS, true, named, form);
    }
}
