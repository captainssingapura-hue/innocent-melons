package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import com.example.fxsuite.life.body.cage.Subdivision;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A smooth surface fitted to a scan of a ball with a spike on it: through the ball's points near enough, the spike
 * drawing it hardly at all.
 */
class ScanFitTest {

    private final Subdivision s = Subdivision.INSTANCE;

    @Test
    void itPassesThroughTheScanButNotItsSpike() {
        final Subdivision.Level base = s.once(s.of(cube(), 1));
        final Subdivision.Level smooth = s.times(base, 2);
        final V3[] scan = new V3[smooth.size()];
        for (int i = 0; i < scan.length; i++) {
            scan[i] = smooth.point(i).unit().times(BALL);
        }
        final int spike = scan.length / 2;
        scan[spike] = scan[spike].times((BALL + 1) / BALL);
        final V3[] fitted = ScanFit.INSTANCE.fitted(base, 2, scan);
        double worst = 0;
        for (int i = 0; i < fitted.length; i++) {
            if (i != spike) {
                worst = Math.max(worst, Math.abs(fitted[i].length() - BALL));
            }
        }
        assertTrue(worst < 0.05, "the ball's points " + worst + " off it at worst");
        final double drawn = fitted[spike].length() - BALL;
        assertTrue(drawn < 0.1, "the spike, 1 cm out, drew the surface " + drawn + " out");
    }

    /** How big the ball is round, cm. */
    private static final double BALL = 1.2;

    /** A cube 2 cm a side round the origin, its faces facing out. */
    private Cage cube() {
        final List<V3> p = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            p.add(new V3((i & 1) == 0 ? -1 : 1, (i & 2) == 0 ? -1 : 1, (i & 4) == 0 ? -1 : 1));
            names.add("corner " + i);
        }
        final List<int[]> f = List.of(new int[] {0, 2, 3, 1}, new int[] {4, 5, 7, 6}, new int[] {0, 1, 5, 4},
                new int[] {2, 6, 7, 3}, new int[] {0, 4, 6, 2}, new int[] {1, 3, 7, 5});
        return new Cage(p, names, f, List.of());
    }
}
