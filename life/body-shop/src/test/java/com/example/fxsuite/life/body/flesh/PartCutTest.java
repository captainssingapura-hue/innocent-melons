package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * A hand shown alone ending in an even ring a little up the forearm, not where the shares change: the forearm's
 * flesh just above the wrist the hand's, all round; further up, the forearm's; the hand's own flesh above that ring
 * the forearm's; the other arm's left to it.
 */
class PartCutTest {

    private final Rig bind = new Rig(Proportions.PAPERS, BoundPose.INSTANCE.pose(), false);

    @Test
    void theHandEndsInARingJustUpTheForearm() {
        final V3 wrist = bind.frame(Part.RIGHT_HAND).at();
        final V3 down = wrist.minus(bind.frame(Part.RIGHT_FOREARM).at()).unit();
        final V3 round = down.cross(V3.Z).unit().times(3);
        final V3[] at = {wrist.minus(down).plus(round), wrist.minus(down).minus(round), wrist.minus(down.times(5)),
            wrist.minus(down.times(3)), wrist.plus(down.times(4))};
        final Part[] most = {Part.RIGHT_FOREARM, Part.RIGHT_FOREARM, Part.RIGHT_FOREARM, Part.RIGHT_HAND,
            Part.RIGHT_HAND};
        final Part[] expected = {Part.RIGHT_HAND, Part.RIGHT_HAND, Part.RIGHT_FOREARM, Part.RIGHT_FOREARM,
            Part.RIGHT_HAND};
        assertArrayEquals(expected, new PartCut(bind).of(flesh(at, most)));
        final V3[] mirrored = new V3[at.length];
        final Part[] other = new Part[at.length];
        final Part[] otherExpected = new Part[at.length];
        for (int i = 0; i < at.length; i++) {
            mirrored[i] = new V3(-at[i].x(), at[i].y(), at[i].z());
            other[i] = most[i].other();
            otherExpected[i] = expected[i].other();
        }
        assertArrayEquals(otherExpected, new PartCut(bind).of(flesh(mirrored, other)), "the left alike");
    }

    /** Flesh of points {@code at}, each following {@code most} alone. */
    private Flesh flesh(final V3[] at, final Part[] most) {
        final List<Part> parts = List.of(Part.values());
        final float[] points = new float[3 * at.length];
        final int[] which = new int[Flesh.PARTS * at.length];
        final float[] shares = new float[Flesh.PARTS * at.length];
        java.util.Arrays.fill(which, -1);
        for (int i = 0; i < at.length; i++) {
            points[3 * i] = (float) at[i].x();
            points[3 * i + 1] = (float) at[i].y();
            points[3 * i + 2] = (float) at[i].z();
            which[Flesh.PARTS * i] = most[i].ordinal();
            shares[Flesh.PARTS * i] = 1;
        }
        return new Flesh(points, points, new int[0], parts, parts.stream().map(bind::frame).toList(), which, shares);
    }
}
