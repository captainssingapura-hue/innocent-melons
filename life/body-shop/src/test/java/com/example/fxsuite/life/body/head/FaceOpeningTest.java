package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A skin left open where the face is to go: the back of the head kept whole; the forehead, ahead of the line down the
 * side of the head, gone; a triangle across the line kept to the line and no further; the throat, under the head's
 * line, kept though it is as far ahead.
 */
class FaceOpeningTest {

    private final Rig bind = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);
    private final Rig.Frame head = bind.frame(Part.HEAD);

    @Test
    void theBackOfTheHeadIsKeptAndTheForeheadGone() {
        final Flesh open = FaceOpening.INSTANCE.open(skin(
                new V3(0, 8, -6), new V3(1, 8, -6), new V3(0, 9, -6),
                new V3(0, 8, 9), new V3(1, 8, 9), new V3(0, 9, 9)));
        assertEquals(1, open.triangleCount(), "the back's kept, the forehead's gone");
        for (final int i : open.triangles()) {
            assertTrue(head.local(open.point(i)).z() < 0, "what is kept is behind");
        }
    }

    @Test
    void aTriangleAcrossTheLineIsKeptToItAndNoFurther() {
        final Flesh open = FaceOpening.INSTANCE.open(skin(new V3(0, 8, 2), new V3(0, 9, 8), new V3(1, 8, 2)));
        assertTrue(open.triangleCount() > 0, "its part behind the line kept");
        final double most = Arrays.stream(open.triangles()).mapToDouble(i -> head.local(open.point(i)).z()).max()
                .orElseThrow();
        assertEquals(4.1 + (14 - 8.5) / 21.6 * 2.2, most, 0.15, "as far ahead as the line, there");
    }

    @Test
    void aPieceLeftApartBehindTheLineGoes() {
        final Flesh open = FaceOpening.INSTANCE.open(skin(
                new V3(0, 8, -6), new V3(1, 8, -6), new V3(0, 9, -6),
                new V3(1, 8, -6), new V3(1, 9, -6), new V3(0, 9, -6),
                new V3(0, 2, 1), new V3(1, 2, 1), new V3(0, 3, 1)));
        assertEquals(2, open.triangleCount(), "the back's two, joined, kept; the one apart gone");
    }

    @Test
    void theThroatUnderTheHeadsLineIsKept() {
        final Flesh open = FaceOpening.INSTANCE.open(skin(new V3(0, -11, 7), new V3(1, -11, 7), new V3(0, -10.5, 7)));
        assertEquals(1, open.triangleCount());
    }

    /**
     * A skin of the triangles through {@code at}, three points each, in the head's frame, each following the head; a
     * point named twice one point, the triangles through it joined.
     */
    private Flesh skin(final V3... at) {
        final List<V3> points = new ArrayList<>();
        final int[] t = new int[at.length];
        for (int i = 0; i < at.length; i++) {
            if (!points.contains(at[i])) {
                points.add(at[i]);
            }
            t[i] = points.indexOf(at[i]);
        }
        final int n = points.size();
        final float[] p = new float[3 * n];
        final int[] which = new int[Flesh.PARTS * n];
        final float[] shares = new float[Flesh.PARTS * n];
        Arrays.fill(which, -1);
        for (int i = 0; i < n; i++) {
            final V3 q = head.place(points.get(i));
            p[3 * i] = (float) q.x();
            p[3 * i + 1] = (float) q.y();
            p[3 * i + 2] = (float) q.z();
            which[Flesh.PARTS * i] = 0;
            shares[Flesh.PARTS * i] = 1;
        }
        return new Flesh(p, new float[3 * n], t, List.of(Part.HEAD), List.of(head), which, shares);
    }
}
