package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Whether two forms, each carried where it has moved to, touch: whether any triangle of the one meets any of the
 * other's, found by looking along every way that could part them, each triangle's facing, each square to the
 * other's in its plane, and each across an edge of each; two triangles meet if no way parts them. The other's
 * triangles are sorted into cubes a centimetre a side, so only those near each are looked at.
 */
final class TurnedTouching {

    private static final double TINY = 1e-12;
    private static final double CUBE = 1;

    /**
     * Where {@code a} carried by {@code at} and {@code b} carried by {@code bt} touch: the middle of each of
     * {@code a}'s triangles that meets one of {@code b}'s; none, if they do not.
     */
    List<Vec3> wheres(final Mesh a, final Mat4 at, final Mesh b, final Mat4 bt) {
        final List<Vec3> found = new ArrayList<>();
        final List<Vec3[]> theirs = triangles(b, bt);
        final Map<List<Long>, List<Integer>> cubes = new HashMap<>();
        for (int i = 0; i < theirs.size(); i++) {
            for (final List<Long> cube : cubes(theirs.get(i))) {
                cubes.computeIfAbsent(cube, c -> new ArrayList<>()).add(i);
            }
        }
        for (final Vec3[] t : triangles(a, at)) {
            final Set<Integer> near = new HashSet<>();
            for (final List<Long> cube : cubes(t)) {
                near.addAll(cubes.getOrDefault(cube, List.of()));
            }
            if (near.stream().anyMatch(i -> meet(t, theirs.get(i)))) {
                found.add(t[0].plus(t[1]).plus(t[2]).times(1.0 / 3));
            }
        }
        return found;
    }

    private List<List<Long>> cubes(final Vec3[] t) {
        final List<List<Long>> all = new ArrayList<>();
        final long[] low = new long[3];
        final long[] high = new long[3];
        for (int d = 0; d < 3; d++) {
            double lo = Double.MAX_VALUE;
            double hi = -Double.MAX_VALUE;
            for (final Vec3 p : t) {
                final double v = d == 0 ? p.x() : d == 1 ? p.y() : p.z();
                lo = Math.min(lo, v);
                hi = Math.max(hi, v);
            }
            low[d] = (long) Math.floor(lo / CUBE);
            high[d] = (long) Math.floor(hi / CUBE);
        }
        for (long x = low[0]; x <= high[0]; x++) {
            for (long y = low[1]; y <= high[1]; y++) {
                for (long z = low[2]; z <= high[2]; z++) {
                    all.add(List.of(x, y, z));
                }
            }
        }
        return all;
    }

    private List<Vec3[]> triangles(final Mesh mesh, final Mat4 m) {
        final List<Vec3[]> all = new ArrayList<>();
        for (int t = 0; t < mesh.triangles(); t++) {
            all.add(new Vec3[] {moved(m, mesh.corner(t, 0)), moved(m, mesh.corner(t, 1)), moved(m, mesh.corner(t, 2))});
        }
        return all;
    }

    private Vec3 moved(final Mat4 m, final Vec3 p) {
        return new Vec3(m.at(0, 0) * p.x() + m.at(0, 1) * p.y() + m.at(0, 2) * p.z() + m.at(0, 3),
                m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3),
                m.at(2, 0) * p.x() + m.at(2, 1) * p.y() + m.at(2, 2) * p.z() + m.at(2, 3));
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
}
