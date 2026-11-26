package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The maker's roundel, as it was worn in the eighties: a ring, a V in its top half, its arms from the ring meeting
 * below the middle, and a W under it, its outer arms from the ring, its two feet pointed down into the ring, its
 * middle peak up into the V's point; each letter one line of strokes as wide as the ring, mitred where they meet, all
 * of it standing proud of a disc behind, its edges bevelled. Made in its own frame, {@code radius} round, across and up
 * on its face, out toward whoever looks at it; each of its measures a share of its radius.
 *
 * @param radius how far round it is
 */
record Roundel(double radius) {

    /** The ring's inside; how wide every stroke is; how far a mitred point may reach, as so many half-strokes. */
    private static final double INSIDE = 0.84;
    private static final double STROKE = 0.15;
    private static final double POINT = 2.2;
    /** How proud the letters stand, and the ring a hair prouder over their ends; their bevels; the disc behind. */
    private static final double PROUD = 0.1;
    private static final double RING = 0.105;
    private static final double BEVEL = 0.035;
    private static final double DISC = 0.06;
    /** How finely its round is drawn. */
    private static final int ROUND = 64;
    /** The V and the W, each a line of points across and up, its ends into the ring. */
    private static final double[][][] LETTERS = {
        {{-0.46, 0.80}, {0, -0.16}, {0.46, 0.80}},
        {{-0.89, 0.24}, {-0.39, -0.70}, {0, 0.05}, {0.39, -0.70}, {0.89, 0.24}}};

    /** The ring, the V and the W, standing proud of the disc's face. */
    Mesh relief() {
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>();
        for (int k = 0; k < ROUND; k++) {
            made.add(ringPiece(shapes, 2 * Math.PI * k / ROUND, 2 * Math.PI * (k + 1) / ROUND));
        }
        for (final double[][] letter : LETTERS) {
            final List<Spot> line = new ArrayList<>();
            for (final double[] p : letter) {
                line.add(new Spot(p[0], p[1]));
            }
            final List<Spot[]> base = sides(line, STROKE / 2);
            final List<Spot[]> top = sides(line, STROKE / 2 - BEVEL);
            for (int k = 0; k + 1 < line.size(); k++) {
                made.add(raised(shapes, base.get(k), base.get(k + 1), top.get(k), top.get(k + 1)));
            }
        }
        return shapes.joined(made);
    }

    /** The disc behind it, as round as it is. */
    Mesh disc() {
        final List<Vec3> back = new ArrayList<>();
        final List<Vec3> front = new ArrayList<>();
        for (int k = 0; k < ROUND; k++) {
            final double a = 2 * Math.PI * k / ROUND;
            back.add(polar(1, a, -DISC));
            front.add(polar(1, a, 0));
        }
        return new Shapes(ROUND).lofted(List.of(back, front), new Vec3(0, 0, -DISC * radius / 2));
    }

    /** One piece of the ring, from {@code a} round to {@code b}: its top drawn in from both its edges, a bevel. */
    private Mesh ringPiece(final Shapes shapes, final double a, final double b) {
        final List<Vec3> low = List.of(polar(INSIDE, a, 0), polar(1, a, 0), polar(1, b, 0), polar(INSIDE, b, 0));
        final List<Vec3> high = List.of(polar(INSIDE + BEVEL, a, RING), polar(1 - BEVEL, a, RING),
                polar(1 - BEVEL, b, RING), polar(INSIDE + BEVEL, b, RING));
        return shapes.lofted(List.of(low, high), polar((INSIDE + 1) / 2, (a + b) / 2, RING / 2));
    }

    /**
     * Where a line's sides are at each of its points, {@code half} out each way: square to it at its ends, mitred where
     * it turns, the mitre's point no further out than so many half-strokes; the left side's, then the right side's.
     */
    private List<Spot[]> sides(final List<Spot> line, final double half) {
        final List<Spot[]> out = new ArrayList<>();
        for (int k = 0; k < line.size(); k++) {
            final Spot before = k > 0 ? normal(line.get(k - 1), line.get(k)) : normal(line.get(k), line.get(k + 1));
            final Spot after = k + 1 < line.size() ? normal(line.get(k), line.get(k + 1)) : before;
            final double mu = before.u() + after.u();
            final double mv = before.v() + after.v();
            final double length = Math.hypot(mu, mv);
            final double cos = (mu * before.u() + mv * before.v()) / length;
            final double reach = Math.min(half / cos, POINT * STROKE / 2);
            final Spot p = line.get(k);
            out.add(new Spot[] {new Spot(p.u() + mu / length * reach, p.v() + mv / length * reach),
                new Spot(p.u() - mu / length * reach, p.v() - mv / length * reach)});
        }
        return out;
    }

    /** The way square to the line from {@code a} to {@code b}, to its left, a unit long. */
    private Spot normal(final Spot a, final Spot b) {
        final double du = b.u() - a.u();
        final double dv = b.v() - a.v();
        final double length = Math.hypot(du, dv);
        return new Spot(-dv / length, du / length);
    }

    /** One stroke between two points of a line, its sides at each as {@code from} and {@code to}, raised, bevelled. */
    private Mesh raised(final Shapes shapes, final Spot[] from, final Spot[] to, final Spot[] topFrom,
                        final Spot[] topTo) {
        final List<Vec3> low = List.of(at(from[0], 0), at(to[0], 0), at(to[1], 0), at(from[1], 0));
        final List<Vec3> high = List.of(at(topFrom[0], PROUD), at(topTo[0], PROUD), at(topTo[1], PROUD),
                at(topFrom[1], PROUD));
        final Spot middle = new Spot((from[0].u() + from[1].u() + to[0].u() + to[1].u()) / 4,
                (from[0].v() + from[1].v() + to[0].v() + to[1].v()) / 4);
        return shapes.lofted(List.of(low, high), at(middle, PROUD / 2));
    }

    private Vec3 polar(final double reach, final double a, final double out) {
        return at(new Spot(reach * Math.cos(a), reach * Math.sin(a)), out);
    }

    /** A spot on its face, across and up, {@code out} proud of it, all as shares of its radius, in centimetres. */
    private Vec3 at(final Spot s, final double out) {
        return new Vec3(s.u() * radius, s.v() * radius, out * radius);
    }
}
