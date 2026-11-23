package melons.innocent.vehicles.osprey;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Skins made of rings: each ring a loop of as many points as the next, joined to it by faces, the first and last
 * closed by a point at their middles; each point facing out from its ring's middle. A body round in section, a
 * fuselage, a nacelle, a wing, is so many rings along it. And a box, as a skin of two rings of four.
 */
public record Lofting(Forming forming) {

    /**
     * A ring about {@code middle}, square to {@code along}, {@code wide} either side along {@code across} and
     * {@code high} either side along the third way, rounded as a squircle of power {@code round} (2 an ellipse,
     * more toward a box), in {@code sides} points.
     */
    public List<Vec3> ring(final Vec3 middle, final Vec3 across, final Vec3 up, final double wide, final double high,
                           final double round, final int sides) {
        final List<Vec3> points = new ArrayList<>();
        for (int k = 0; k < sides; k++) {
            final double angle = 2 * Math.PI * k / sides;
            final double c = Math.cos(angle);
            final double s = Math.sin(angle);
            final double x = Math.signum(c) * Math.pow(Math.abs(c), 2 / round) * wide;
            final double y = Math.signum(s) * Math.pow(Math.abs(s), 2 / round) * high;
            points.add(middle.plus(across.times(x)).plus(up.times(y)));
        }
        return points;
    }

    /** {@code rings} joined in order, both ends closed. */
    public void lofted(final List<List<Vec3>> rings) {
        final List<int[]> made = new ArrayList<>();
        for (final List<Vec3> ring : rings) {
            final Vec3 middle = middle(ring);
            final int[] at = new int[ring.size()];
            for (int k = 0; k < ring.size(); k++) {
                final Vec3 out = ring.get(k).minus(middle);
                at[k] = forming.point(ring.get(k), out.length() < 1e-9 ? new Vec3(0, 1, 0) : out.unit());
            }
            made.add(at);
        }
        for (int r = 0; r + 1 < made.size(); r++) {
            final int[] a = made.get(r);
            final int[] b = made.get(r + 1);
            for (int k = 0; k < a.length; k++) {
                final int next = (k + 1) % a.length;
                forming.faced(a[k], b[k], b[next]);
                forming.faced(a[k], b[next], a[next]);
            }
        }
        closed(rings.getFirst(), rings.get(1));
        closed(rings.getLast(), rings.get(rings.size() - 2));
    }

    /** {@code end} closed by a point at its middle, facing away from {@code inside}. */
    private void closed(final List<Vec3> end, final List<Vec3> inside) {
        final Vec3 middle = middle(end);
        final Vec3 away = middle.minus(middle(inside));
        final Vec3 facing = away.length() < 1e-9 ? new Vec3(0, 1, 0) : away.unit();
        final int centre = forming.point(middle, facing);
        final int[] at = new int[end.size()];
        for (int k = 0; k < end.size(); k++) {
            at[k] = forming.point(end.get(k), facing);
        }
        for (int k = 0; k < end.size(); k++) {
            forming.faced(centre, at[k], at[(k + 1) % end.size()]);
        }
    }

    /** A box about {@code middle}, {@code size} each way of the world, its faces flat. */
    public void box(final Vec3 middle, final Vec3 size) {
        final Vec3 h = size.times(0.5);
        final double[][] faces = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
        for (final double[] f : faces) {
            final Vec3 n = new Vec3(f[0], f[1], f[2]);
            final Vec3 u = Math.abs(f[1]) > 0 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
            final Vec3 v = n.cross(u);
            final Vec3 c = middle.plus(new Vec3(n.x() * h.x(), n.y() * h.y(), n.z() * h.z()));
            final Vec3 du = new Vec3(u.x() * h.x(), u.y() * h.y(), u.z() * h.z());
            final Vec3 dv = new Vec3(v.x() * h.x(), v.y() * h.y(), v.z() * h.z());
            final int a = forming.point(c.minus(du).minus(dv), n);
            final int b = forming.point(c.plus(du).minus(dv), n);
            final int d = forming.point(c.plus(du).plus(dv), n);
            final int e = forming.point(c.minus(du).plus(dv), n);
            forming.faced(a, b, d);
            forming.faced(a, d, e);
        }
    }

    /** A round bar from {@code from} to {@code to}, {@code radius} thick, in {@code sides}, both ends closed. */
    public void bar(final Vec3 from, final Vec3 to, final double radius, final int sides) {
        final Vec3 along = to.minus(from).unit();
        final Vec3 any = Math.abs(along.y()) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        final Vec3 across = along.cross(any).unit();
        final Vec3 up = along.cross(across).unit();
        lofted(List.of(ring(from, across, up, radius, radius, 2, sides),
                ring(to, across, up, radius, radius, 2, sides)));
    }

    private Vec3 middle(final List<Vec3> ring) {
        Vec3 sum = new Vec3(0, 0, 0);
        for (final Vec3 point : ring) {
            sum = sum.plus(point);
        }
        return sum.times(1.0 / ring.size());
    }
}
