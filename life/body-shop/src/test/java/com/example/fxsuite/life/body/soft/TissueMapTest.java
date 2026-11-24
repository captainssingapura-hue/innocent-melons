package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.MuscleShape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TissueMapTest {

    private static final SoftRegion ROOM = new SoftRegion(new V3(-1, -1, -1), new V3(6, 3, 3));
    private static final double CELL = 0.1;

    private final Walls none = new Walls() {
        @Override
        public double distance(final double x, final double y, final double z) {
            return 100;
        }

        @Override
        public V3 out(final double x, final double y, final double z) {
            return V3.ZERO;
        }
    };

    private Mesher.Mesh box(final double x0, final double x1) {
        final float[] p = new float[8 * 3];
        for (int b = 0; b < 8; b++) {
            p[3 * b] = (float) ((b & 1) == 0 ? x0 : x1);
            p[3 * b + 1] = (b >> 1 & 1) == 0 ? 0 : 2;
            p[3 * b + 2] = (b >> 2 & 1) == 0 ? 0 : 2;
        }
        final int[] t = {0, 2, 1, 1, 2, 3, 4, 5, 6, 5, 7, 6, 0, 1, 4, 1, 5, 4, 2, 6, 3, 3, 6, 7, 0, 4, 2, 2, 4, 6,
            1, 3, 5, 3, 7, 5};
        return new Mesher.Mesh(p, t);
    }

    @Test
    void twoDrawnOneInTheOtherShareTheirOverlapEachKeepingItsCore() {
        final TissueMap map = new TissueMap(ROOM, CELL, List.of(box(0, 2), box(1.5, 3.5)), none, 0);
        assertEquals(8, map.drawn(0), 0.1);
        assertEquals(8, map.drawn(1), 0.1);
        assertEquals(8 + 8 - 2, map.room(0) + map.room(1), 0.1, "the overlap given once");
        assertEquals(map.room(0), map.room(1), 1.0, "and shared between them, as near as the cells allow");
        final int[] n = map.counts();
        for (int c = 0; c < n[0] * n[1] * n[2]; c++) {
            final V3 m = map.middle(c);
            if (m.y() > 0.5 && m.y() < 1.5 && m.z() > 0.5 && m.z() < 1.5) {
                if (m.x() > 0 && m.x() < 1.6) {
                    assertEquals(0, map.label(c), "the first one's core its own at " + m);
                }
                if (m.x() > 1.9 && m.x() < 3.5) {
                    assertEquals(1, map.label(c), "the second one's at " + m);
                }
            }
        }
    }

    @Test
    void aNarrowGapBetweenMusclesIsFilledAndAWideOneNot() {
        final TissueMap narrow = new TissueMap(ROOM, CELL, List.of(box(0, 2), box(2.4, 4.4)), none, 0.3);
        assertEquals(0.4 * 4, narrow.filler(), 0.3, "the gap between them filled");
        final TissueMap wide = new TissueMap(ROOM, CELL, List.of(box(0, 2), box(3, 5)), none, 0.3);
        assertTrue(wide.filler() < 0.5, "a wide gap left: " + wide.filler());
    }

    @Test
    void aTissuesSurfaceClosesInItsRoomAndFacesOut() {
        final TissueMap map = new TissueMap(ROOM, CELL, List.of(box(0, 2), box(1.5, 3.5)), none, 0);
        final Mesher.Mesh first = TissueSurfaces.INSTANCE.of(map, 0, 0);
        assertEquals(map.room(0), MuscleShape.INSTANCE.volume(first), 1e-3);
        final Mesher.Mesh smoothed = TissueSurfaces.INSTANCE.of(map, 0, 10);
        assertEquals(map.room(0), MuscleShape.INSTANCE.volume(smoothed), 0.05 * map.room(0), "smoothed, no smaller");
    }
}
