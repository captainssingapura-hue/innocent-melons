package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.head.Crossings;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Weights;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Muscles at rest, soft ({@link Packing}): two passing into each other pressed apart, each keeping how much of it there
 * is, swelling where nothing presses on it.
 */
class PackingTest {

    @Test
    void twoPassingIntoEachOtherArePressedApartEachKeepingItsVolume() {
        final List<MuscleSurface.Surface> drawn = List.of(ball(-2), ball(2));
        final List<MuscleSurface.Surface> packed = Packing.INSTANCE.packed(drawn, nothing());
        assertTrue(both(drawn) > 10, "drawn, they pass into each other");
        assertTrue(both(packed) < 0.5, "at rest, they do not: " + both(packed));
        for (int i = 0; i < 2; i++) {
            assertEquals(new Packed(drawn.get(i)).volume(), new Packed(packed.get(i)).volume(),
                    0.02 * new Packed(drawn.get(i)).volume(), "as much of each");
        }
    }

    private MuscleSurface.Surface ball(final double x) {
        final Sketch s = new Sketch();
        s.ball(new V3(x, 0, 0), 3);
        final Mesher.Mesh m = Mesher.of(new Field(s.done().elements()), 0.4);
        final float[] all = new float[m.size()];
        Arrays.fill(all, 1);
        return new MuscleSurface.Surface(m, new float[m.size()], List.of(Part.CHEST), new float[][] {all}, 1);
    }

    /** No bones near. */
    private Beneath nothing() {
        final Weights w = new Weights(new float[Part.values().length]);
        return new Beneath(new float[] {100, 100, 100, 101, 100, 100, 100, 101, 100}, new int[] {0, 1, 2},
                new Part[] {Part.CHEST, Part.CHEST, Part.CHEST}, new Weights[] {w, w, w});
    }

    /** How much is inside both, cm3. */
    private double both(final List<MuscleSurface.Surface> two) {
        final double cell = 0.1;
        final int[] n = {120, 80, 80};
        final boolean[] a = Crossings.INSTANCE.inside(two.get(0).mesh(), new double[] {-6, -4, -4}, cell, n);
        final boolean[] b = Crossings.INSTANCE.inside(two.get(1).mesh(), new double[] {-6, -4, -4}, cell, n);
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            count += a[i] && b[i] ? 1 : 0;
        }
        return count * cell * cell * cell;
    }
}
