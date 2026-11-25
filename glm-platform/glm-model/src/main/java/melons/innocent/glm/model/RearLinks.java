package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.BallJoint;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The rear axle's five links each side, after the current large SUV's, every one forged aluminium, bare, placed and
 * shaped as its workshop manual draws them. High, from the subframe's front: the upper front link, arched over and
 * stout in its middle; the upper rear link, slim and nearly straight; and the toe link, long and bowed like a banana,
 * bushed at both ends. Low: the lower front link, ahead of the axle, slim and bowed; and the lower rear link, the
 * spring's, behind the axle, a tray carrying the spring's cup and the damper's fork, its eye in its clevis on the
 * subframe and its outer end on a steel ball joint in the carrier, the ball joint a part of its own. Each link's eyes
 * sized to its clevis on the subframe; every figure an estimate scaled from photographs and drawings. Each link with
 * its points {@code inner} and {@code outer}; the spring's its seat and the damper's foot besides.
 *
 * @param points the rear axle's points
 * @param shapes what it is shaped with
 */
record RearLinks(RearPoints points, Shapes shapes) {

    /** What they are made of: forged aluminium, bare; the ball joint steel. */
    private static final Making FORGED = new Making(new Made(Material.ALUMINIUM, true));
    private static final Making JOINT = new Making(new Made(Material.STEEL, true));
    private static final int ROUND = 24;
    /** The eyes at the carrier, and the beams' sections. */
    private static final LinkForms.Eye OUTER = new LinkForms.Eye(5, 2.6, 1.8);
    private static final LinkForms.Beam ARCHED = new LinkForms.Beam(3.0, 4.2, 1.0, 0.8);
    private static final LinkForms.Beam UPPER = new LinkForms.Beam(2.6, 3.2, 0.8, 0.7);
    private static final LinkForms.Beam LOWER = new LinkForms.Beam(2.8, 3.6, 0.9, 0.7);
    private static final LinkForms.Beam TOE = new LinkForms.Beam(2.4, 3.0, 0.8, 0.6);
    /** How big the spring link's ball joint is, and how far its stud reaches up into the carrier. */
    private static final double BALL = 1.6;
    private static final double STUD = 8;

    private LinkForms forms() {
        return new LinkForms(ROUND);
    }

    /** The upper front link, arched over. */
    Part upperFront() {
        final Vec3 in = points.upperFrontInner();
        final Vec3 out = points.upperFrontOuter();
        return link("upper front link", in, out, forms().link(in, out, new Vec3(0, 3.5, 0), eye(5.2), OUTER, ARCHED));
    }

    /** The upper rear link, slim, nearly straight. */
    Part upperRear() {
        final Vec3 in = points.upperRearInner();
        final Vec3 out = points.upperRearOuter();
        return link("upper rear link", in, out, forms().link(in, out, forms().bolt(in, out).times(1.5), eye(5.2),
                OUTER, UPPER));
    }

    /** The lower rear link, the spring's: its tray, the spring's cup and the damper's fork on it. */
    Part spring() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", points.springInner());
        named.put("outer", points.springOuter());
        named.put("spring seat", points.springSeat());
        named.put("damper", points.damperFoot());
        return FORGED.part("spring link", Group.LINKS, true, named, forms().springLink(points.springInner(),
                points.springOuter(), points.springSeat(), points.damperFoot(), new LinkForms.Eye(6.3, 3.2, 2.3),
                14));
    }

    /** The spring link's ball joint, pressed into its outer end, its stud rising into the carrier. */
    Part springBallJoint() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("ball", points.springOuter());
        return JOINT.part("spring link ball joint", Group.LINKS, true, named,
                new BallJoint(points.springOuter(), new Vec3(0, 1, 0), BALL, STUD, ROUND).mesh());
    }

    /** The lower front link, slim, bowed a little in plan. */
    Part lowerFront() {
        final Vec3 in = points.lowerFrontInner();
        final Vec3 out = points.lowerFrontOuter();
        return link("lower front link", in, out, forms().link(in, out, forms().bolt(in, out).times(2.5),
                eye(5.6), OUTER, LOWER));
    }

    /** The toe link, long and bowed in plan like a banana, bushed at both ends. */
    Part toe() {
        final Vec3 in = points.toeInner();
        final Vec3 out = points.toeOuter();
        return link("toe link", in, out, forms().link(in, out, forms().bolt(in, out).times(-4), eye(4.6),
                new LinkForms.Eye(4.4, 2.4, 1.6), TOE));
    }

    /** An eye inboard, as long as its clevis on the subframe is wide, less its bush's flanges. */
    private LinkForms.Eye eye(final double clevis) {
        return new LinkForms.Eye(clevis - 0.2, 2.7, 1.9);
    }

    private Part link(final String name, final Vec3 inner, final Vec3 outer, final Mesh form) {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", inner);
        named.put("outer", outer);
        return FORGED.part(name, Group.LINKS, true, named, form);
    }
}
