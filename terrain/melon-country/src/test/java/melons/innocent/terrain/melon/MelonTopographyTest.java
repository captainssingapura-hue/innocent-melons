package melons.innocent.terrain.melon;

import melons.innocent.terrain.Terrain;
import melons.innocent.terrain.Topography;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Melon country as a topographic map: the plain level at 12 m where the village is; the bay under the sea's level
 * and deepening out; Mount Melon 760 m at its top, the Downs 97 m at theirs; the river's bed under the plain beside
 * the village; over the six kilometres square, no step anywhere between two points 10 m apart steeper than three in
 * one, the steepest of the first draft's ridges coming to two; and on the average within a few metres of the first
 * draft, but for the harbour, dredged and no part of it.
 */
class MelonTopographyTest {

    private static final double M = 100;

    private final Topography map = MelonTopography.INSTANCE.topography();

    @Test
    void thePlainLevelWhereTheVillageIs() {
        assertEquals(12 * M, map.height(0, 0), 5);
        assertEquals(12 * M, map.height(-300 * M, 400 * M), 5);
    }

    @Test
    void theBayUnderTheSeaDeepeningOut() {
        final double near = map.height(0, -1_000 * M);
        final double far = map.height(0, -2_400 * M);
        assertTrue(near < 0, "under the sea: " + near);
        assertTrue(far < near, "deeper out: " + far + " under " + near);
    }

    @Test
    void theTopsAsHighAsMarked() {
        assertEquals(760 * M, map.height(2_100 * M, 2_600 * M), 1);
        assertEquals(97 * M, map.height(-1_900 * M, 900 * M), 1);
    }

    @Test
    void theRiversBedUnderThePlain() {
        final double bed = map.height(680 * M, 330 * M);
        assertTrue(bed < 11 * M, "under the plain: " + bed);
    }

    @Test
    void noStepAnywhereAndCloseToTheFirstDraft() {
        final Terrain draft = MelonCountry.INSTANCE.terrain();
        double steepest = 0;
        double off = 0;
        int count = 0;
        for (double x = -2_900 * M; x < 2_900 * M; x += 97 * M) {
            for (double z = -2_500 * M; z < 3_300 * M; z += 101 * M) {
                final double here = map.height(x, z);
                steepest = Math.max(steepest, Math.abs(map.height(x + 10 * M, z) - here) / (10 * M));
                steepest = Math.max(steepest, Math.abs(map.height(x, z + 10 * M) - here) / (10 * M));
                if (Math.hypot(x - 600 * M, z + 1_000 * M) > 900 * M) {
                    off += Math.abs(here - draft.height(x, z));
                    count++;
                }
            }
        }
        assertTrue(steepest < 3, "steepest " + steepest);
        assertTrue(off / count < 3 * M, "off the first draft by " + off / count / M + " m on the average");
    }
}
