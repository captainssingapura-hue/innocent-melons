package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;

import java.util.List;
import java.util.Optional;

/**
 * What keeps a region's muscles apart by their own surfaces, and out of the bones: each point of a muscle's surface
 * carried in its elements, placed where its four nodes have it; the nearest of every other muscle's surface's
 * triangles to it found; if it lies behind that triangle, inside the other muscle, it is moved out along the way the
 * triangle faces, as far as it went in, its move shared among its four nodes as they carry it; and, as near as the
 * bones ({@link Walls}) are, moved out of them the same way. The triangles are found near each point by a grid over
 * them, sorted into it afresh each sweep. A point not carried, out of the region, stays where it is, and is a wall to
 * the others.
 */
final class SurfaceContact {

    /** How far a point is looked about for another muscle's surface, cm: further in than this, it is not seen. */
    private static final double REACH = 0.8;

    private final Surfaces s;
    private final Walls walls;
    private final TriangleGrid grid;

    /**
     * Contact between the surfaces {@code s}, carried in nodes as they are, kept out of {@code walls}.
     */
    SurfaceContact(final Surfaces s, final Walls walls) {
        this.s = s;
        this.walls = walls;
        this.grid = new TriangleGrid(REACH);
    }

    /**
     * Every carried point found inside another muscle moved out, the nodes at {@code x}, each as free as {@code w},
     * kept {@code gap} apart, if {@code apart}; and out of the bones by {@code clear}. Every point found where it is
     * at the sweep's start, and each node then moved by the mean of what the points it carries asked of it, so that
     * many points carried by the same node do not each move it all the way. How many were inside another, and how
     * many in bone; and, if there is a {@code fascia}, drawn tighter, how many pressed on it, the muscle and the
     * sleeve sharing what is between. Only the muscles {@code taking} says take part, as points and as walls; and the
     * points of those {@code firm} says, which do not move, push them back where they reach into them.
     */
    int[] kept(final double[] x, final double[] w, final double gap, final double clear, final boolean apart,
               final Optional<Fascia> fascia, final boolean[] taking, final boolean[] firm) {
        fascia.ifPresent(Fascia::tightened);
        final double[] at = s.placed(x);
        if (apart) {
            grid.sorted(at, s, taking);
        }
        int inside = 0;
        int bone = 0;
        int pressed = 0;
        final double[] move = new double[2];
        final double[] q = new double[3 * s.muscles()];
        final double[] n = new double[3 * s.muscles()];
        final double[] delta = new double[x.length];
        final int[] count = new int[w.length];
        for (int p = 0; p < s.count(); p++) {
            if (!s.carried(p) || !taking[s.owner(p)]) {
                continue;
            }
            final double px = at[3 * p];
            final double py = at[3 * p + 1];
            final double pz = at[3 * p + 2];
            final int near = apart ? grid.nearestEach(at, s, p, px, py, pz, q, n) : 0;
            for (int k = 0; k < near; k++) {
                final double depth = gap - ((px - q[3 * k]) * n[3 * k] + (py - q[3 * k + 1]) * n[3 * k + 1]
                        + (pz - q[3 * k + 2]) * n[3 * k + 2]);
                if (depth > 0) {
                    s.moved(delta, count, w, p, n[3 * k] * depth, n[3 * k + 1] * depth, n[3 * k + 2] * depth);
                    inside++;
                }
            }
            final double d = walls.distance(px, py, pz);
            if (d < clear) {
                final V3 out = walls.out(px, py, pz);
                s.moved(delta, count, w, p, out.x() * (clear - d), out.y() * (clear - d), out.z() * (clear - d));
                bone++;
            }
            if (fascia.isPresent() && fascia.get().pressed(px, py, pz, move)) {
                s.moved(delta, count, w, p, move[0], 0, move[1]);
                pressed++;
            }
        }
        if (apart) {
            inside += reached(at, w, gap, firm, q, n, delta, count);
        }
        fascia.ifPresent(f -> {
            f.yielded();
            f.keptOutOf(walls, clear);
        });
        for (int i = 0; i < count.length; i++) {
            if (count[i] > 0) {
                x[3 * i] += delta[3 * i] / count[i];
                x[3 * i + 1] += delta[3 * i + 1] / count[i];
                x[3 * i + 2] += delta[3 * i + 2] / count[i];
            }
        }
        return new int[] {inside, bone, pressed};
    }

    /**
     * Where a point of a muscle that holds firm, a deeper layer at rest, lies inside a muscle that is moving, that
     * muscle's surface there asked to move back past it, each corner of its nearest triangle as far: so a firm muscle's
     * edge cannot reach into a moving one between its points. How many did.
     */
    private int reached(final double[] at, final double[] w, final double gap, final boolean[] firm, final double[] q,
                        final double[] n, final double[] delta, final int[] count) {
        final int[] tris = new int[s.muscles()];
        int reached = 0;
        for (int p = 0; p < s.count(); p++) {
            if (!firm[s.owner(p)]) {
                continue;
            }
            final int near = grid.nearestEach(at, s, p, at[3 * p], at[3 * p + 1], at[3 * p + 2], q, n, tris);
            for (int k = 0; k < near; k++) {
                final double depth = gap - ((at[3 * p] - q[3 * k]) * n[3 * k] + (at[3 * p + 1] - q[3 * k + 1])
                        * n[3 * k + 1] + (at[3 * p + 2] - q[3 * k + 2]) * n[3 * k + 2]);
                if (depth > 0) {
                    for (int c = 0; c < 3; c++) {
                        s.moved(delta, count, w, s.corner(tris[k], c), -n[3 * k] * depth, -n[3 * k + 1] * depth,
                                -n[3 * k + 2] * depth);
                    }
                    reached++;
                }
            }
        }
        return reached;
    }

    /**
     * The surfaces of a region's muscles, carried in their nodes: every point and triangle of them all, by one count,
     * each point's muscle, the four nodes it is carried by (all the muscles' nodes counted together) and their weights,
     * or none, and where it was at first; and which way round each muscle's triangles go.
     */
    static final class Surfaces {

        /** The furthest a point is moved at once, cm; and how little of it its free nodes may carry to be moved. */
        private static final double MOST = 0.25;
        private static final double SOME = 0.05;

        private final int[] owner;
        private final int[] holder;
        private final float[] weights;
        private final float[] rest;
        private final int[] triangles;
        private final int[] triangleOwner;
        private final boolean[] turned;

        /** The surfaces {@code meshes} of the muscles of {@code lattices}, their first nodes {@code first}. */
        Surfaces(final List<Mesher.Mesh> meshes, final List<Lattice> lattices, final int[] first) {
            int points = 0;
            int corners = 0;
            for (final Mesher.Mesh m : meshes) {
                points += m.points().length / 3;
                corners += m.triangles().length;
            }
            owner = new int[points];
            holder = new int[4 * points];
            weights = new float[4 * points];
            rest = new float[3 * points];
            triangles = new int[corners];
            triangleOwner = new int[corners / 3];
            turned = new boolean[meshes.size()];
            int p = 0;
            int c = 0;
            for (int k = 0; k < meshes.size(); k++) {
                final Mesher.Mesh m = meshes.get(k);
                final Lattice l = lattices.get(k);
                final int base = p;
                for (int i = 0; i < m.points().length / 3; i++, p++) {
                    owner[p] = k;
                    System.arraycopy(m.points(), 3 * i, rest, 3 * p, 3);
                    final int t = l.holder()[i];
                    for (int j = 0; j < 4; j++) {
                        holder[4 * p + j] = t < 0 ? -1 : first[k] + l.tets()[4 * t + j];
                        weights[4 * p + j] = t < 0 ? 0 : l.weights()[4 * i + j];
                    }
                }
                for (final int corner : m.triangles()) {
                    triangleOwner[c / 3] = k;
                    triangles[c++] = base + corner;
                }
                turned[k] = room(m) < 0;
            }
        }

        int count() {
            return owner.length;
        }

        int muscles() {
            return turned.length;
        }

        int owner(final int p) {
            return owner[p];
        }

        int triangles() {
            return triangleOwner.length;
        }

        int corner(final int t, final int k) {
            return triangles[3 * t + k];
        }

        int triangleOwner(final int t) {
            return triangleOwner[t];
        }

        boolean turned(final int muscle) {
            return turned[muscle];
        }

        boolean carried(final int p) {
            return holder[4 * p] >= 0;
        }

        /** Whether each point is carried. */
        boolean[] carried() {
            final boolean[] c = new boolean[owner.length];
            for (int p = 0; p < c.length; p++) {
                c[p] = carried(p);
            }
            return c;
        }

        /** Where every point is, the nodes at {@code x}: carried, or where it was. */
        double[] placed(final double[] x) {
            final double[] at = new double[rest.length];
            for (int p = 0; p < owner.length; p++) {
                if (!carried(p)) {
                    at[3 * p] = rest[3 * p];
                    at[3 * p + 1] = rest[3 * p + 1];
                    at[3 * p + 2] = rest[3 * p + 2];
                    continue;
                }
                for (int j = 0; j < 4; j++) {
                    final int n = holder[4 * p + j];
                    final double wj = weights[4 * p + j];
                    at[3 * p] += wj * x[3 * n];
                    at[3 * p + 1] += wj * x[3 * n + 1];
                    at[3 * p + 2] += wj * x[3 * n + 2];
                }
            }
            return at;
        }

        /**
         * Point {@code p} asked to move by {@code dx}, {@code dy}, {@code dz}, no further than {@link #MOST}: what
         * each of its nodes would move, added to {@code delta}, and counted in {@code count}, as each carries it, as
         * free, by its weight's part above nothing, so that a point just outside its element, carried by weights
         * reaching past it, is not thrown far by its nodes. A point not carried, out of the region, is not moved.
         */
        void moved(final double[] delta, final int[] count, final double[] w, final int p, final double dx,
                   final double dy, final double dz) {
            if (!carried(p)) {
                return;
            }
            final double far = Math.sqrt(dx * dx + dy * dy + dz * dz);
            final double cut = far > MOST ? MOST / far : 1;
            double sum = 0;
            for (int j = 0; j < 4; j++) {
                final double wj = weights[4 * p + j];
                sum += wj * Math.max(0, wj) * w[holder[4 * p + j]];
            }
            if (sum < SOME) {
                return;
            }
            for (int j = 0; j < 4; j++) {
                final int n = holder[4 * p + j];
                final double k = Math.max(0, weights[4 * p + j]) * w[n] / sum * cut;
                if (k > 0) {
                    delta[3 * n] += k * dx;
                    delta[3 * n + 1] += k * dy;
                    delta[3 * n + 2] += k * dz;
                    count[n]++;
                }
            }
        }

        /** Six times the room {@code m} closes in, by the way its triangles go round. */
        private double room(final Mesher.Mesh m) {
            final float[] q = m.points();
            final int[] t = m.triangles();
            double sum = 0;
            for (int i = 0; i < t.length; i += 3) {
                final V3 a = new V3(q[3 * t[i]], q[3 * t[i] + 1], q[3 * t[i] + 2]);
                final V3 b = new V3(q[3 * t[i + 1]], q[3 * t[i + 1] + 1], q[3 * t[i + 1] + 2]);
                final V3 c = new V3(q[3 * t[i + 2]], q[3 * t[i + 2] + 1], q[3 * t[i + 2] + 2]);
                sum += a.dot(b.cross(c));
            }
            return sum;
        }
    }
}
