package melons.innocent.vehicles.boat;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Lettering;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * The skin of a hull between two of its lines on one side: at each share {@code u} of the way along from the transom,
 * {@code v} of the way from the lower line to the upper. The lines are given as the right half's, evenly along them;
 * the left side ({@code side} -1) is them turned over the middle line. It is patched in pieces, a colour each, and
 * things are laid on it a hair proud: strokes, discs and letters, reading the right way from outside.
 *
 * @param lower the lower line, evenly along it
 * @param upper the upper line, as many points
 * @param side  which side: 1 the right, -1 the left
 */
public record Skin(List<Vec3> lower, List<Vec3> upper, int side) {

    private static final int ROWS = 6;
    private static final int ROUND = 16;

    /** Where on it ({@code u}, {@code v}) is. */
    public Vec3 at(final double u, final double v) {
        final double f = Math.clamp(u, 0, 1) * (lower.size() - 1);
        final int i = Math.min((int) f, lower.size() - 2);
        final double t = f - i;
        final Vec3 low = lower.get(i).plus(lower.get(i + 1).minus(lower.get(i)).times(t));
        final Vec3 high = upper.get(i).plus(upper.get(i + 1).minus(upper.get(i)).times(t));
        final Vec3 right = low.plus(high.minus(low).times(v));
        return new Vec3(side * right.x(), right.y(), right.z());
    }

    /** Which way it faces out there. */
    public Vec3 out(final double u, final double v) {
        final double du = 0.5 / (lower.size() - 1);
        final Vec3 along = at(Math.min(1, u + du), v).minus(at(Math.max(0, u - du), v));
        final Vec3 up = at(u, Math.min(1, v + 0.05)).minus(at(u, Math.max(0, v - 0.05)));
        final Vec3 n = along.cross(up);
        final Vec3 unit = n.length() < 1e-12 ? new Vec3(side, 0, 0) : n.unit();
        return unit.x() * side < 0 ? unit.times(-1) : unit;
    }

    /** The piece from {@code u0} to {@code u1} along and {@code v0} to {@code v1} up, added to {@code forming}. */
    public void patch(final Forming forming, final double u0, final double u1, final double v0, final double v1) {
        final int along = Math.max(2, (int) Math.ceil((u1 - u0) * (lower.size() - 1)));
        final int[][] at = new int[along + 1][ROWS + 1];
        for (int i = 0; i <= along; i++) {
            for (int j = 0; j <= ROWS; j++) {
                final double u = u0 + (u1 - u0) * i / along;
                final double v = v0 + (v1 - v0) * j / ROWS;
                at[i][j] = forming.point(at(u, v), out(u, v));
            }
        }
        for (int i = 0; i < along; i++) {
            for (int j = 0; j < ROWS; j++) {
                forming.faced(at[i][j], at[i + 1][j], at[i + 1][j + 1]);
                forming.faced(at[i][j], at[i + 1][j + 1], at[i][j + 1]);
            }
        }
    }

    /** A stroke from ({@code u0}, {@code v0}) to ({@code u1}, {@code v1}), {@code thick} of the way up, so proud. */
    public void stroke(final Forming forming, final double u0, final double v0, final double u1, final double v1,
                       final double thick, final double proud) {
        final int steps = 8;
        for (int k = 0; k < steps; k++) {
            final double a = (double) k / steps;
            final double b = (double) (k + 1) / steps;
            final double ua = u0 + (u1 - u0) * a;
            final double va = v0 + (v1 - v0) * a;
            final double ub = u0 + (u1 - u0) * b;
            final double vb = v0 + (v1 - v0) * b;
            final Vec3 n = out(ua, va);
            final int p = forming.point(at(ua, va - thick).plus(n.times(proud)), n);
            final int q = forming.point(at(ub, vb - thick).plus(n.times(proud)), n);
            final int r = forming.point(at(ub, vb + thick).plus(n.times(proud)), n);
            final int s = forming.point(at(ua, va + thick).plus(n.times(proud)), n);
            forming.faced(p, q, r);
            forming.faced(p, r, s);
        }
    }

    /** A disc {@code radius} across at ({@code u}, {@code v}), so proud, lying on the skin there. */
    public void disc(final Forming forming, final double u, final double v, final double radius, final double proud) {
        final Vec3 n = out(u, v);
        final Vec3 middle = at(u, v).plus(n.times(proud));
        final Vec3 a = n.cross(new Vec3(0, 1, 0)).unit();
        final Vec3 b = n.cross(a).unit();
        final int centre = forming.point(middle, n);
        for (int k = 0; k < ROUND; k++) {
            final double t0 = 2 * Math.PI * k / ROUND;
            final double t1 = 2 * Math.PI * (k + 1) / ROUND;
            forming.faced(centre, forming.point(middle.plus(a.times(Math.cos(t0) * radius))
                    .plus(b.times(Math.sin(t0) * radius)), n), forming.point(middle.plus(a.times(Math.cos(t1) * radius))
                    .plus(b.times(Math.sin(t1) * radius)), n));
        }
    }

    /** Which way is up the skin at ({@code u}, {@code v}): toward its upper edge, along its surface. */
    private Vec3 upward(final double u, final double v) {
        return at(u, Math.min(1, v + 0.05)).minus(at(u, Math.max(0, v - 0.05))).unit();
    }

    /**
     * {@code text} written on it from ({@code u}, {@code v}), letters {@code high} centimetres high, so proud, reading
     * from outside: toward the stern on the right side and toward the bow on the left.
     */
    public void lettered(final Forming forming, final Lettering lettering, final String text, final double u,
                         final double v, final double high, final double proud) {
        // each letter laid on the skin where it falls, upright along the skin's own up, so none sinks where it flares
        final Vec3 forward = at(u + 0.01, v).minus(at(u - 0.01, v)).times(1 / 0.02);
        final Vec3 n0 = out(u, v);
        final double way = Math.signum(n0.cross(upward(u, v)).dot(forward));
        for (int k = 0; k < text.length(); k++) {
            if (!lettering.has(text.charAt(k))) {
                continue;
            }
            final Mesh letter = lettering.form(text.charAt(k));
            final double from = lettering.set() * high * (k - text.length() / 2.0);
            final double uk = u + way * from / forward.length();
            final Vec3 n = out(uk, v);
            final Vec3 up = upward(uk, v);
            final Vec3 reading = n.cross(up).unit();
            final Vec3 corner = at(uk, v).plus(n.times(proud));
            final int[] at = new int[letter.points().size()];
            for (int p = 0; p < at.length; p++) {
                final Vec3 q = letter.points().get(p);
                at[p] = forming.point(corner.plus(reading.times(q.x() * high)).plus(up.times(q.y() * high)), n);
            }
            for (int c = 0; c + 2 < letter.corners().size(); c += 3) {
                forming.faced(at[letter.corners().get(c)], at[letter.corners().get(c + 1)],
                        at[letter.corners().get(c + 2)]);
            }
        }
    }
}
