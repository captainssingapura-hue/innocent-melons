package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.existence.Wearables;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.Nearness;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A plate shaped over a trunk: standing off its faces as far as told, as thick as told; nowhere further off it than
 * that, nothing of it astray in the space round it; ending where its edges cut it.
 */
class PlatingTest {

    private static final double OFF = 1.0;
    private static final double THICK = 0.4;
    /** How near the grid finds a flat face, cm. */
    private static final double NEAR = 0.06;

    @Test
    void aPlateStandsOffTheTrunkAsToldAndNowhereElse() {
        final Beneath trunk = box(new V3(-5, -15, -5), new V3(5, 15, 5), Part.CHEST);
        final List<Field.Element> edges = List.of(
                new Field.Element("top", Field.Plane.of(new V3(0, 6, 0), V3.Y), Field.Op.CLIP, 0),
                new Field.Element("foot", Field.Plane.of(new V3(0, -6, 0), new V3(0, -1, 0)), Field.Op.CLIP, 0));
        final Plating.Cut cut = new Plating.Cut(new Wearables.Cuirass(), Part.CHEST, OFF, THICK, 0.25, 0.2,
                new double[] {-12, -8, -12, 12, 8, 12}, edges, Optional.empty());
        final float[] at = Plating.INSTANCE.of(new Nearness(trunk), cut).mesh().points();
        assertTrue(at.length > 0, "a plate");
        int faces = 0;
        for (int i = 0; i < at.length; i += 3) {
            final double x = Math.abs(at[i]);
            final double y = at[i + 1];
            final double z = Math.abs(at[i + 2]);
            assertTrue(Math.abs(y) < 6 + NEAR, "within its edges: " + y);
            final double off = Math.hypot(Math.max(x - 5, 0), Math.max(z - 5, 0));
            assertTrue(off < OFF + NEAR, "no further off the trunk than told: " + off);
            if (Math.min(x, z) < 3) {
                assertTrue(off > OFF - THICK - NEAR, "as far off a face as told: " + off);
                faces++;
            }
        }
        assertTrue(faces > 0, "over the trunk's faces");
    }

    /** A box from {@code lo} to {@code hi}, its faces outward, following {@code part}. */
    private Beneath box(final V3 lo, final V3 hi, final Part part) {
        final List<V3> c = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            c.add(new V3((i & 1) == 0 ? lo.x() : hi.x(), (i & 2) == 0 ? lo.y() : hi.y(), (i & 4) == 0 ? lo.z() : hi.z()));
        }
        final V3 middle = lo.plus(hi).times(0.5);
        final int[][] faces = {{0, 2, 6, 4}, {1, 3, 7, 5}, {0, 1, 5, 4}, {2, 3, 7, 6}, {0, 1, 3, 2}, {4, 5, 7, 6}};
        final List<Integer> t = new ArrayList<>();
        for (final int[] f : faces) {
            for (final int[] tri : new int[][] {{f[0], f[1], f[2]}, {f[0], f[2], f[3]}}) {
                final V3 n = c.get(tri[1]).minus(c.get(tri[0])).cross(c.get(tri[2]).minus(c.get(tri[0])));
                final boolean out = n.dot(c.get(tri[0]).minus(middle)) > 0;
                t.addAll(out ? List.of(tri[0], tri[1], tri[2]) : List.of(tri[0], tri[2], tri[1]));
            }
        }
        final float[] points = new float[24];
        final Part[] main = new Part[8];
        final Weights[] w = new Weights[8];
        for (int i = 0; i < 8; i++) {
            points[3 * i] = (float) c.get(i).x();
            points[3 * i + 1] = (float) c.get(i).y();
            points[3 * i + 2] = (float) c.get(i).z();
            main[i] = part;
            final float[] alone = new float[Part.values().length];
            alone[part.ordinal()] = 1;
            w[i] = new Weights(alone);
        }
        return new Beneath(points, t.stream().mapToInt(Integer::intValue).toArray(), main, w);
    }
}
