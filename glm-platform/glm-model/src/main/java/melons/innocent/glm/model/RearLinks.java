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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The rear axle's five links each side, after the current large SUV's, every one forged aluminium, bare: the two
 * upper links, slim, the front one bowed in plan and the rear one arched over; the spring's link, a tray carrying the
 * spring's cup, its eye in its clevis on the subframe and its fork round the carrier's bush; the lower front link,
 * the stoutest beam; and the toe link, slimmest, its outer end a boss clamped on the stud of a steel ball joint in the
 * carrier, the ball joint a part of its own. Each link's eyes sized to its clevis on the subframe; every figure an
 * estimate scaled from photographs of the parts. Each link with its points {@code inner} and {@code outer}; the
 * spring's its seat besides; the toe link its clamp.
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
    private static final LinkForms.Beam UPPER = new LinkForms.Beam(2.6, 3.2, 0.8, 0.7);
    private static final LinkForms.Beam LOWER = new LinkForms.Beam(3.2, 4.0, 1.0, 0.8);
    private static final LinkForms.Beam TOE = new LinkForms.Beam(2.4, 2.8, 0.8, 0.6);
    /** How far over the ball the toe link's clamp grips the stud; how big the ball joint is. */
    private static final double CLAMP = 3.6;
    private static final double BALL = 1.4;
    private static final double STUD = 7;

    private LinkForms forms() {
        return new LinkForms(ROUND);
    }

    /** The upper front link, bowed in plan. */
    Part upperFront() {
        final Vec3 in = points.upperFrontInner();
        final Vec3 out = points.upperFrontOuter();
        return link("upper front link", in, out, forms().link(in, out, forms().bolt(in, out).times(4),
                eye(5.2), OUTER, UPPER));
    }

    /** The upper rear link, arched over. */
    Part upperRear() {
        final Vec3 in = points.upperRearInner();
        final Vec3 out = points.upperRearOuter();
        return link("upper rear link", in, out, forms().link(in, out, new Vec3(0, 3.5, 0), eye(5.2), OUTER, UPPER));
    }

    /** The spring's link: its tray, the spring's cup on it. */
    Part spring() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", points.springInner());
        named.put("outer", points.springOuter());
        named.put("spring seat", points.springSeat());
        return FORGED.part("spring link", Group.LINKS, true, named, forms().springLink(points.springInner(),
                points.springOuter(), points.springSeat(), new LinkForms.Eye(6.3, 3.2, 2.3), 14));
    }

    /** The lower front link, the stoutest beam, bowed a little in plan. */
    Part lowerFront() {
        final Vec3 in = points.lowerFrontInner();
        final Vec3 out = points.lowerFrontOuter();
        return link("lower front link", in, out, forms().link(in, out, forms().bolt(in, out).times(2.5),
                eye(5.6), OUTER, LOWER));
    }

    /** The toe link: its eye inboard, its clamp outboard on the ball joint's stud. */
    Part toe() {
        final Vec3 in = points.toeInner();
        final Vec3 clamp = clamp();
        final LinkForms.Eye eye = eye(4.6);
        final Mesh form = shapes.joined(List.of(
                forms().beam(in, eye.outer(), clamp, 2.3, forms().bolt(in, clamp).times(2), TOE),
                new Sleeve(in, forms().bolt(in, points.toeOuter()), eye.length(), eye.outer(), eye.bore(), ROUND)
                        .mesh(),
                new Sleeve(clamp, new Vec3(0, 1, 0), 3, 2.3, BALL * 0.55, ROUND).mesh()));
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("inner", in);
        named.put("outer", points.toeOuter());
        named.put("clamp", clamp);
        return FORGED.part("toe link", Group.LINKS, true, named, form);
    }

    /** The toe link's ball joint, in the carrier, its stud rising through the link's clamp. */
    Part toeBallJoint() {
        final Map<String, Vec3> named = new LinkedHashMap<>();
        named.put("ball", points.toeOuter());
        named.put("stud", clamp());
        return JOINT.part("toe link ball joint", Group.LINKS, true, named,
                new BallJoint(points.toeOuter(), new Vec3(0, 1, 0), BALL, STUD, ROUND).mesh());
    }

    private Vec3 clamp() {
        return points.toeOuter().plus(new Vec3(0, CLAMP, 0));
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
