package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Whether two placed forms touch: whether any triangle of the one meets any of the other's, found by looking along
 * every way that could part them, each triangle's facing, each square to the other's in its plane, and each across an
 * edge of each; two triangles meet if no way parts them. Only triangles near the other form at all are looked at.
 */
final class Touching {

    private static final double TINY = 1e-12;

    /** Whether {@code a} placed at {@code at} and {@code b} placed at {@code bt} touch. */
    boolean touch(final Mesh a, final Vec3 at, final Mesh b, final Vec3 bt) {
        final List<Vec3[]> ours = triangles(a, at);
        final List<Vec3[]> theirs = triangles(b, bt);
        final double[] ourReach = reach(ours.stream().flatMap(Arrays::stream).toList());
        final double[] theirReach = reach(theirs.stream().flatMap(Arrays::stream).toList());
        final List<Vec3[]> near = ours.stream().filter(t -> meet(reach(List.of(t)), theirReach)).toList();
        final List<Vec3[]> nearer = theirs.stream().filter(t -> meet(reach(List.of(t)), ourReach)).toList();
        for (final Vec3[] t : near) {
            final double[] tr = reach(List.of(t));
            for (final Vec3[] s : nearer) {
                if (meet(tr, reach(List.of(s))) && meet(t, s)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Vec3[]> triangles(final Mesh mesh, final Vec3 at) {
        final List<Vec3[]> all = new ArrayList<>();
        for (int t = 0; t < mesh.triangles(); t++) {
            all.add(new Vec3[] {mesh.corner(t, 0).plus(at), mesh.corner(t, 1).plus(at), mesh.corner(t, 2).plus(at)});
        }
        return all;
    }

    /** Whether triangles {@code t} and {@code s} meet: no way parts them. */
    private boolean meet(final Vec3[] t, final Vec3[] s) {
        final Vec3 n = t[1].minus(t[0]).cross(t[2].minus(t[0]));
        final Vec3 m = s[1].minus(s[0]).cross(s[2].minus(s[0]));
        final List<Vec3> ways = new ArrayList<>(List.of(n, m));
        for (int i = 0; i < 3; i++) {
            final Vec3 e = t[(i + 1) % 3].minus(t[i]);
            final Vec3 f = s[(i + 1) % 3].minus(s[i]);
            ways.add(n.cross(e));
            ways.add(m.cross(f));
            for (int j = 0; j < 3; j++) {
                ways.add(e.cross(s[(j + 1) % 3].minus(s[j])));
            }
        }
        for (final Vec3 w : ways) {
            if (w.dot(w) > TINY && parted(t, s, w)) {
                return false;
            }
        }
        return true;
    }

    private boolean parted(final Vec3[] t, final Vec3[] s, final Vec3 w) {
        double tLow = Double.MAX_VALUE;
        double tHigh = -Double.MAX_VALUE;
        double sLow = Double.MAX_VALUE;
        double sHigh = -Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            tLow = Math.min(tLow, t[i].dot(w));
            tHigh = Math.max(tHigh, t[i].dot(w));
            sLow = Math.min(sLow, s[i].dot(w));
            sHigh = Math.max(sHigh, s[i].dot(w));
        }
        return tHigh < sLow || sHigh < tLow;
    }

    private double[] reach(final List<Vec3> points) {
        return new double[] {points.stream().mapToDouble(Vec3::x).min().orElseThrow(),
            points.stream().mapToDouble(Vec3::y).min().orElseThrow(), points.stream().mapToDouble(Vec3::z).min()
            .orElseThrow(), points.stream().mapToDouble(Vec3::x).max().orElseThrow(),
            points.stream().mapToDouble(Vec3::y).max().orElseThrow(), points.stream().mapToDouble(Vec3::z).max()
            .orElseThrow()};
    }

    private boolean meet(final double[] a, final double[] b) {
        for (int i = 0; i < 3; i++) {
            if (a[i] > b[i + 3] || b[i] > a[i + 3]) {
                return false;
            }
        }
        return true;
    }
}
