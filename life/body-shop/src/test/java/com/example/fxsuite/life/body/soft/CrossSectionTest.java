package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrossSectionTest {

    /** A round rod of radius {@code r} along the way up, from nought to {@code high}, closed at its ends. */
    private Mesher.Mesh rod(final double r, final double high, final int round) {
        final List<Float> p = new ArrayList<>();
        final List<Integer> t = new ArrayList<>();
        for (final double y : new double[] {0, high}) {
            for (int a = 0; a < round; a++) {
                final double angle = 2 * Math.PI * a / round;
                p.add((float) (r * Math.cos(angle)));
                p.add((float) y);
                p.add((float) (r * Math.sin(angle)));
            }
        }
        p.add(0f);
        p.add(0f);
        p.add(0f);
        p.add(0f);
        p.add((float) high);
        p.add(0f);
        final int bottom = 2 * round;
        final int top = bottom + 1;
        for (int a = 0; a < round; a++) {
            final int b = (a + 1) % round;
            t.addAll(List.of(a, round + a, round + b, a, round + b, b, bottom, a, b, top, round + b, round + a));
        }
        final float[] f = new float[p.size()];
        for (int i = 0; i < f.length; i++) {
            f[i] = p.get(i);
        }
        return new Mesher.Mesh(f, t.stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    void aRodCutAcrossShowsItsRoundAndCutSlantlyLonger() {
        final int round = 64;
        final Mesher.Mesh rod = rod(1.5, 10, round);
        final double polygon = round / 2.0 * 1.5 * 1.5 * Math.sin(2 * Math.PI / round);
        assertEquals(polygon, CrossSection.INSTANCE.area(rod, new V3(0, 4, 0), new V3(0, 1, 0)), 1e-6);
        final double tilt = Math.toRadians(30);
        assertEquals(polygon / Math.cos(tilt), CrossSection.INSTANCE.area(rod, new V3(0, 5, 0),
                new V3(Math.sin(tilt), Math.cos(tilt), 0)), 1e-3);
        assertEquals(0, CrossSection.INSTANCE.area(rod, new V3(0, 12, 0), new V3(0, 1, 0)), 1e-9);
    }
}
