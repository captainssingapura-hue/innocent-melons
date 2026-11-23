package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The band joining a body's skin, cut round its neck, to a clay head, cut under its chin ({@link NeckJoin}): rings from
 * the one edge up to the other, {@link #ROUND} points round each, each as far round either edge, along it, from its
 * front, each point leaving the body's edge as the body runs there and coming to the clay head's as it runs there, a
 * smooth curve between; stitched to either edge point by point as they come round, each triangle facing out. All one
 * surface: the body's skin kept, the band, the clay head kept ({@link NeckShares}).
 */
public record NeckBand() implements StatelessFunctionalObject {

    public static final NeckBand INSTANCE = new NeckBand();

    /**
     * The two edges the band joins and the cuts a little in from each, telling which way each surface runs from its
     * edge: the body's edge, its foot; the body cut lower; the clay head's edge, its brim; the clay head cut higher.
     *
     * @param foot the body's edge
     * @param below the body cut lower
     * @param brim the clay head's edge
     * @param above the clay head cut higher
     */
    public record Seam(NeckEdge.Edge foot, NeckEdge.Edge below, NeckEdge.Edge brim, NeckEdge.Edge above) {
    }

    /**
     * How many points round each ring, how many spans from edge to edge, how far each end leaves as its surface goes.
     */
    private static final int ROUND = 160;
    private static final int RINGS = 6;
    private static final double LEAVE = 0.5;

    /**
     * {@code skin}, its head's frame {@code f}, what is kept of it cut, {@code body}, in that frame; what is kept of
     * the clay head cut, {@code clay}; the edges between, {@code seam}: one surface.
     */
    public Flesh joined(final Flesh skin, final Rig.Frame f, final NeckCut.Cut body, final NeckCut.Cut clay,
                        final Seam seam) {
        final int band = body.where().length;
        final int top = band + ROUND * (RINGS - 1);
        final V3[] all = new V3[top + clay.where().length];
        System.arraycopy(body.where(), 0, all, 0, band);
        for (int r = 1; r < RINGS; r++) {
            for (int q = 0; q < ROUND; q++) {
                all[band + (r - 1) * ROUND + q] = between(seam, round(q), (double) r / RINGS);
            }
        }
        System.arraycopy(clay.where(), 0, all, top, clay.where().length);
        final List<int[]> faces = new ArrayList<>(body.kept());
        stitch(faces, seam.foot(), 0, band, all);
        stitch(faces, seam.brim(), top, band + (RINGS - 2) * ROUND, all);
        for (int r = 1; r + 1 < RINGS; r++) {
            final int a = band + (r - 1) * ROUND;
            for (int q = 0; q < ROUND; q++) {
                final int q1 = (q + 1) % ROUND;
                faces.add(out(new int[] {a + q, a + q1, a + ROUND + q1}, all, seam.foot().middle()));
                faces.add(out(new int[] {a + q, a + ROUND + q1, a + ROUND + q}, all, seam.foot().middle()));
            }
        }
        for (final int[] t : clay.kept()) {
            faces.add(new int[] {t[0] + top, t[1] + top, t[2] + top});
        }
        return NeckShares.INSTANCE.flesh(skin, f, all, faces, seam.foot(), body, new int[] {band, top, ROUND, RINGS});
    }

    /** How far round the {@code q}th point of a ring is, a share of the way round from the front. */
    public double round(final int q) {
        return (double) q / ROUND;
    }

    /** A share {@code t} of the way up from the body's edge to the clay head's, {@code a} of the way round. */
    private V3 between(final Seam seam, final double a, final double t) {
        final V3 from = seam.foot().at(a);
        final V3 to = seam.brim().at(a);
        final double far = LEAVE * to.minus(from).length();
        final V3 leave = way(from.minus(seam.below().at(a))).times(far);
        final V3 come = way(seam.above().at(a).minus(to)).times(far);
        final double t2 = t * t;
        final double t3 = t2 * t;
        return from.times(2 * t3 - 3 * t2 + 1).plus(leave.times(t3 - 2 * t2 + t)).plus(to.times(-2 * t3 + 3 * t2))
                .plus(come.times(t3 - t2));
    }

    /** {@code v} as a way, a unit; up, should it be none. */
    private V3 way(final V3 v) {
        return v.length() < 1e-9 ? V3.Y : v.unit();
    }

    /**
     * Triangles between {@code edge}, its points numbered from {@code from} among all, and the band's ring next to it,
     * its points from {@code ring}: each point taken as it comes round, from the one or the other.
     */
    private void stitch(final List<int[]> faces, final NeckEdge.Edge edge, final int from, final int ring,
                        final V3[] all) {
        final int[] ia = edge.at();
        final double[] ra = edge.round();
        final int n = ia.length;
        int i = 0;
        int j = 0;
        while (i < n || j < ROUND) {
            final double na = i < n ? (i + 1 < n ? ra[i + 1] : 1) : Double.MAX_VALUE;
            final double nb = j < ROUND ? round(j + 1) : Double.MAX_VALUE;
            final int a = from + ia[i % n];
            if (na <= nb) {
                faces.add(out(new int[] {a, from + ia[(i + 1) % n], ring + j % ROUND}, all, edge.middle()));
                i++;
            } else {
                faces.add(out(new int[] {a, ring + (j + 1) % ROUND, ring + j % ROUND}, all, edge.middle()));
                j++;
            }
        }
    }

    /** {@code t}, turned round if it faces in, toward the neck's middle {@code c}, rather than out. */
    private int[] out(final int[] t, final V3[] all, final V3 c) {
        final V3 n = all[t[1]].minus(all[t[0]]).cross(all[t[2]].minus(all[t[0]]));
        final V3 m = all[t[0]].plus(all[t[1]]).plus(all[t[2]]).times(1.0 / 3).minus(c);
        return n.dot(new V3(m.x(), 0, m.z())) < 0 ? new int[] {t[0], t[2], t[1]} : t;
    }
}
