package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.head.Crossings;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.muscle.Section;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * What a region to be simulated soft ({@link SoftRegion}) takes in: every muscle with enough of it in the region, to be
 * simulated, and the rest held, around it; where each taken is cut by the region's faces, to be fixed there; where each
 * is held to its bones at its ends, in the region; and how much of the taken lies in the region, alone and together,
 * and how much in two of them or more, which the simulation is to bring to nothing.
 */
public record RegionPieces() implements StatelessFunctionalObject {

    public static final RegionPieces INSTANCE = new RegionPieces();

    /** How finely the region is gone over for how much lies in it, cm. */
    private static final double CELL = 0.3;

    /**
     * One muscle on one side, its surface on the body as it is bound.
     *
     * @param muscle  the muscle
     * @param left    whether the left one
     * @param surface its surface
     */
    public record Piece(Muscle muscle, boolean left, MuscleSurface.Surface surface) {
    }

    /**
     * What the region takes in: the muscles taken, those held round them, where the taken are cut, where they are held
     * to their bones.
     *
     * @param taken    the muscles taken in, any of each in the region
     * @param held     the rest
     * @param cuts     where the taken cross the region's faces
     * @param attached where the taken are held to their bones, in the region
     */
    public record Taken(List<Piece> taken, List<Piece> held, List<V3> cuts, List<V3> attached) {
    }

    /**
     * How much of the taken lies in the region, cm3: each, by its place among the taken; all together; where two or more
     * are.
     *
     * @param each     each one's, in the region
     * @param together all of them together
     * @param shared   where two or more are
     */
    public record Filled(double[] each, double together, double shared) {
    }

    /**
     * What {@code region} takes in of {@code pieces}, on the body {@code rig} holds as it is bound: each with at least
     * {@code share} of its points in it; the rest held, a sliver of a muscle in the region a wall to it, not moved.
     */
    public Taken taken(final SoftRegion region, final List<Piece> pieces, final Rig rig, final double share) {
        final List<Piece> taken = new ArrayList<>();
        final List<Piece> held = new ArrayList<>();
        final List<V3> cuts = new ArrayList<>();
        final List<V3> attached = new ArrayList<>();
        for (final Piece p : pieces) {
            final V3[] at = points(p);
            int in = 0;
            for (final V3 v : at) {
                in += region.has(v) ? 1 : 0;
            }
            if (in == 0 || in < share * at.length) {
                held.add(p);
                continue;
            }
            taken.add(p);
            final int[] t = p.surface().mesh().triangles();
            for (int i = 0; i < t.length; i += 3) {
                for (int e = 0; e < 3; e++) {
                    final V3 a = at[t[i + e]];
                    final V3 b = at[t[i + (e + 1) % 3]];
                    if (region.has(a) && !region.has(b)) {
                        cuts.add(a.plus(b.minus(a).times(region.leaves(a, b))));
                    }
                }
            }
            final List<Section> s = p.muscle().sections();
            for (final Section end : List.of(s.getFirst(), s.getLast())) {
                final V3 v = end.at().in(rig, p.left());
                if (region.has(v)) {
                    attached.add(v);
                }
            }
        }
        return new Taken(taken, held, cuts, attached);
    }

    /** How much of {@code taken} lies in {@code region}, each, together, and in two or more. */
    public Filled filled(final SoftRegion region, final List<Piece> taken) {
        final double[] lo = {region.lo().x(), region.lo().y(), region.lo().z()};
        final int[] n = {(int) Math.ceil((region.hi().x() - lo[0]) / CELL) + 1,
            (int) Math.ceil((region.hi().y() - lo[1]) / CELL) + 1, (int) Math.ceil((region.hi().z() - lo[2]) / CELL) + 1};
        final int[] count = new int[n[0] * n[1] * n[2]];
        final double[] each = new double[taken.size()];
        final double cube = CELL * CELL * CELL;
        for (int k = 0; k < taken.size(); k++) {
            final boolean[] in = Crossings.INSTANCE.inside(taken.get(k).surface().mesh(), lo, CELL, n);
            for (int i = 0; i < in.length; i++) {
                if (in[i]) {
                    count[i]++;
                    each[k] += cube;
                }
            }
        }
        int together = 0;
        int shared = 0;
        for (final int c : count) {
            together += c > 0 ? 1 : 0;
            shared += c > 1 ? 1 : 0;
        }
        return new Filled(each, together * cube, shared * cube);
    }

    private V3[] points(final Piece p) {
        final float[] f = p.surface().mesh().points();
        final V3[] at = new V3[f.length / 3];
        for (int i = 0; i < at.length; i++) {
            at[i] = new V3(f[3 * i], f[3 * i + 1], f[3 * i + 2]);
        }
        return at;
    }
}
