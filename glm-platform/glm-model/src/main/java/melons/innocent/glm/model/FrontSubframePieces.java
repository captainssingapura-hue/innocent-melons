package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Clevis;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;
import melons.innocent.vehiclelab.forms.Swept;

import java.util.ArrayList;
import java.util.List;

/**
 * The front subframe's pieces as each is made, each where it stands on the platform, after the current large SUV's
 * aluminium subframe: a main node, cast, a ribbed web with a window through it, flanged top and bottom, and cast onto
 * it the sleeve of its back mount, the lower links' ears, the engine mount's pad, a boss for the steering gear and the
 * sockets the cross member and the strut tube are let into; the cross member, a hollow beam pushed through a die, a
 * row of holes drilled through it; the strut tube, bent; and the front mount, cast, hook-shaped, its two bolts' holes
 * lined with steel. Every figure an estimate scaled from photographs of the part.
 *
 * @param points the front axle's points
 */
record FrontSubframePieces(FrontPoints points) {

    private static final int ROUND = 24;
    private static final Vec3 ACROSS = new Vec3(1, 0, 0);
    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How thick the node's web is cast, its flanges, and the front mount. */
    private static final double WEB = 1.2;
    private static final double FLANGE = 1.0;
    private static final double HOOK = 2.8;
    /** The cross member's section, deep along the car and high, and its wall. */
    private static final double BEAM_DEEP = 4;
    private static final double BEAM_HIGH = 7;
    private static final double BEAM_WALL = 0.4;
    /** How far round the strut tube is. */
    private static final double STRUT = 4.6;

    /** A main node, cast. */
    Mesh node() {
        final Outlines outlines = new Outlines(ROUND);
        final Vec3 n = points.node();
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(List.of(new Spot(-18, -4), new Spot(-16, -8), new Spot(10, -8), new Spot(14, -4),
                new Spot(14, 4), new Spot(10, 9), new Spot(-13, 9), new Spot(-18, 4)),
                List.of(outlines.slot(new Spot(-2, 1), 14, 6)), n, ALONG, UP, WEB).mesh());
        made.add(flange(n.plus(new Vec3(-1, -8 + FLANGE / 2, 0)), -16, 10, 10));
        made.add(flange(n.plus(new Vec3(0, 9 - FLANGE / 2, 0)), -13, 10, 6));
        made.add(new Sleeve(points.mountBack(), UP, 7, 4.5, 3.3, ROUND).mesh());
        made.add(new Swept(new Course(List.of(points.crossMember().minus(ACROSS.times(4)),
                points.crossMember().plus(ACROSS.times(0.6))), 1),
                List.of(new Section(BEAM_DEEP + 1.2, BEAM_HIGH + 1.2, 6), new Section(BEAM_DEEP + 1.2,
                        BEAM_HIGH + 1.2, 6)), ROUND).mesh());
        made.add(new Swept(new Course(List.of(points.strutFoot().minus(ALONG.times(4)),
                points.strutFoot().plus(ALONG.times(2))), 1),
                List.of(new Section(STRUT + 1.4), new Section(STRUT + 1.4)), ROUND).mesh());
        made.add(new Plate(outlines.slot(new Spot(0, 0), 9, 9), List.of(outlines.circle(new Spot(0, 0), 1)),
                points.at(31, 3.7, -6), ALONG, ACROSS, 1.4).mesh());
        made.add(new Sleeve(points.at(31.5, -1, 8.5), UP, 5, 1.7, 0.7, ROUND).mesh());
        made.add(ear(points.lowerFrontInner(), points.lowerFrontOuter()));
        made.add(ear(points.lowerRearInner(), points.lowerRearOuter()));
        return new Shapes(ROUND).joined(made);
    }

    /** The cross member: four walls of a hollow beam, its front and back drilled through, its ends in the nodes. */
    Mesh crossMember() {
        final Outlines outlines = new Outlines(ROUND);
        final Vec3 middle = points.crossMember().minus(new Vec3(points.crossMember().x(), 0, 0));
        final double half = points.crossMember().x() + 0.4;
        final List<List<Spot>> holes = new ArrayList<>();
        for (final double u : List.of(-22.0, -14.0, -6.0, 6.0, 14.0, 22.0)) {
            holes.add(outlines.circle(new Spot(u, 0), 0.8));
        }
        final List<Spot> face = rectangle(half, BEAM_HIGH / 2 - BEAM_WALL);
        final List<Spot> lid = rectangle(half, BEAM_DEEP / 2);
        final double apart = BEAM_DEEP / 2 - BEAM_WALL / 2;
        final double over = BEAM_HIGH / 2 - BEAM_WALL / 2;
        return new Shapes(ROUND).joined(List.of(
                new Plate(face, holes, middle.plus(ALONG.times(apart)), ACROSS, UP, BEAM_WALL).mesh(),
                new Plate(face, holes, middle.minus(ALONG.times(apart)), ACROSS, UP, BEAM_WALL).mesh(),
                new Plate(lid, List.of(), middle.plus(UP.times(over)), ACROSS, ALONG, BEAM_WALL).mesh(),
                new Plate(lid, List.of(), middle.minus(UP.times(over)), ACROSS, ALONG, BEAM_WALL).mesh()));
    }

    /** The strut tube, from the node's top forward and up in an S to the front mount's spigot. */
    Mesh strutTube() {
        final Course course = new Course(List.of(points.strutFoot(), points.at(36.5, 3.5, 14),
                points.at(38.5, 6.5, 28), points.at(41, 9.5, 40), points.strutHead()), 8);
        final Section round = new Section(STRUT);
        return new Swept(course, List.of(round, round, round, round, round), ROUND).mesh();
    }

    /** The front mount: a hook cast flat, its spigot for the strut tube, its two bolts' holes lined with steel. */
    Mesh frontMount() {
        final Outlines outlines = new Outlines(ROUND);
        final Vec3 m = points.mountFront();
        final List<Spot> outline = List.of(new Spot(-4.5, -7), new Spot(-5.5, 0), new Spot(-4, 5.5),
                new Spot(-0.5, 8.5), new Spot(4, 9), new Spot(7.5, 7), new Spot(9.5, 3), new Spot(9, -1),
                new Spot(7.5, -3.5), new Spot(6, -1.5), new Spot(5.5, 1.5), new Spot(3.5, 3), new Spot(1, 2.5),
                new Spot(-0.5, -1), new Spot(-1, -7));
        final List<Spot> bolts = List.of(new Spot(1.5, 6), new Spot(6.2, 5.2));
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(outline, bolts.stream().map(b -> outlines.circle(b, 1.3)).toList(), m, ALONG, UP, HOOK)
                .mesh());
        for (final Spot b : bolts) {
            made.add(new Sleeve(m.plus(ALONG.times(b.u())).plus(UP.times(b.v())), ACROSS, HOOK + 0.6, 1.3, 0.8,
                    ROUND).mesh());
        }
        final Vec3 way = points.strutHead().minus(points.at(41, 9.5, 40)).unit();
        made.add(new Swept(new Course(List.of(points.strutHead().minus(way.times(2)),
                points.strutHead().plus(way.times(3))), 1),
                List.of(new Section(STRUT + 1.4), new Section(STRUT + 1.2)), ROUND).mesh());
        return new Shapes(ROUND).joined(made);
    }

    /** A flange cast along the node's web, level, from {@code from} to {@code to} along it, {@code wide} across. */
    private Mesh flange(final Vec3 middle, final double from, final double to, final double wide) {
        return new Plate(List.of(new Spot(from, -wide / 2), new Spot(to, -wide / 2), new Spot(to, wide / 2),
                new Spot(from, wide / 2)), List.of(), middle, ALONG, ACROSS, FLANGE).mesh();
    }

    /** A lower link's ear, cast under the node, its bolt square to the link. */
    private Mesh ear(final Vec3 inner, final Vec3 outer) {
        final Vec3 foot = new Vec3(inner.x(), points.node().y() - 8, inner.z());
        final Vec3 bolt = UP.cross(outer.minus(inner)).unit();
        return new Clevis(inner, foot, bolt, 5, 1.2, 2.8, 0.75, 6, ROUND).mesh();
    }

    private List<Spot> rectangle(final double halfLong, final double halfHigh) {
        return List.of(new Spot(-halfLong, -halfHigh), new Spot(halfLong, -halfHigh), new Spot(halfLong, halfHigh),
                new Spot(-halfLong, halfHigh));
    }
}
