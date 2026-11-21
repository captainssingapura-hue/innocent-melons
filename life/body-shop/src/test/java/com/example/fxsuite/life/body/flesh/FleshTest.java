package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Dof;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A forearm's flesh twisted as its forearm is, as far as each point lies along it: none at the elbow, half way down
 * half the twist, all of it at the wrist, where the hand takes it; the right and the left alike, each its own way.
 * Only some of it, the triangles whose points are each kept; which part a point follows most.
 */
class FleshTest {

    private final Proportions body = Proportions.PAPERS;
    private final Rig bind = new Rig(body, BoundPose.INSTANCE.pose(), false);

    @Test
    void aForearmsFleshTwistsAsFarAsItLiesAlongIt() {
        for (final Part p : new Part[] {Part.RIGHT_FOREARM, Part.LEFT_FOREARM}) {
            final Rig twisted = new Rig(body, BoundPose.INSTANCE.pose().with(p, Dof.FOREARM_TWIST, 80), false);
            final double[] shares = {0, 0.5, 1};
            for (final double s : shares) {
                final V3 local = new V3(3, -25 * s, 1);
                final Flesh f = one(bind.frame(p).place(local), p);
                final float[] at = new float[3];
                f.posed(twisted, at, new float[3]);
                final V3 expected = twisted.frame(p).place(Rig.twisted(local, 80, s));
                assertEquals(0, new V3(at[0], at[1], at[2]).minus(expected).length(), 0.05,
                        p.said() + ", " + s + " of the way down");
            }
        }
    }

    @Test
    void onlyTheTrianglesWhosePointsAreAllKept() {
        final float[] at = new float[12];
        final List<Part> parts = List.of(Part.RIGHT_FOREARM, Part.RIGHT_HAND);
        final Flesh f = new Flesh(at, at, new int[] {0, 1, 2, 1, 2, 3}, parts,
                List.of(bind.frame(Part.RIGHT_FOREARM), bind.frame(Part.RIGHT_HAND)),
                new int[] {0, -1, -1, -1, 0, 1, -1, -1, 0, 1, -1, -1, 1, -1, -1, -1},
                new float[] {1, 0, 0, 0, 0.6f, 0.4f, 0, 0, 0.7f, 0.3f, 0, 0, 1, 0, 0, 0});
        assertArrayEquals(new int[] {0, 1, 2}, f.only(i -> f.most(i) == Part.RIGHT_FOREARM).triangles(),
                "the forearm's");
        assertArrayEquals(new int[0], f.only(i -> f.most(i) == Part.RIGHT_HAND).triangles(), "none all the hand's");
        assertArrayEquals(new int[] {0, 1, 2, 1, 2, 3}, f.only(i -> true).triangles(), "all");
    }

    /** Flesh of one point at {@code at}, following part {@code p} alone. */
    private Flesh one(final V3 at, final Part p) {
        return new Flesh(new float[] {(float) at.x(), (float) at.y(), (float) at.z()}, new float[] {0, 1, 0},
                new int[0], List.of(p), List.of(bind.frame(p)), new int[] {0, -1, -1, -1}, new float[] {1, 0, 0, 0});
    }
}
