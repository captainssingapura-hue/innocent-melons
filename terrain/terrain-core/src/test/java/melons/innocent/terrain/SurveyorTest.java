package melons.innocent.terrain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A ground surveyed: each level's loops running where the ground is at that level, no further off it than the
 * tolerance and a little for the grid; closed where they run off the stretch surveyed; the top marked inside a loop
 * with nothing else in it.
 */
class SurveyorTest {

    private static final double M = 100;

    /** A cone 100 m high, falling a metre for every ten out from its top at the middle. */
    private final Ground cone = new Ground() {
        public Extent extent() {
            return new Extent(-1_200 * M, -1_200 * M, 1_200 * M, 1_200 * M);
        }

        public double height(final double x, final double z) {
            return 100 * M - Math.hypot(x, z) / 10;
        }
    };

    private final Surveyor surveyor = new Surveyor(cone, cone.extent(), 10 * M);

    @Test
    void eachLevelsLoopsWhereTheGroundIsAtThatLevel() {
        final List<Loop> loops = surveyor.loops(50 * M, 1 * M, 0);
        assertEquals(1, loops.size());
        for (final Spot spot : loops.getFirst().spots()) {
            assertEquals(500 * M, Math.hypot(spot.x(), spot.z()), 2 * M, "500 m out, at " + spot);
        }
        assertTrue(loops.getFirst().spots().size() < 200, "kept no finer than need be");
        assertTrue(surveyor.loops(50 * M, 1 * M, Math.PI * 600 * 600 * M * M).isEmpty(), "none round too little");
    }

    @Test
    void closedWhereTheyRunOffTheStretch() {
        // at -80 m the cone is above it everywhere surveyed: one loop, round the stretch, half a step outside it
        final List<Loop> loops = surveyor.loops(-80 * M, 1 * M, 0);
        assertEquals(1, loops.size());
        assertTrue(loops.getFirst().inside(1_199 * M, 1_199 * M));
    }

    @Test
    void theTopMarkedInsideALoop() {
        final Loop loop = surveyor.loops(90 * M, 1 * M, 0).getFirst();
        final Optional<SpotHeight> top = surveyor.extreme(loop, 90 * M, 50);
        assertTrue(top.isPresent());
        assertEquals(100 * M, top.get().height(), 1);
        assertEquals(0, top.get().at().to(new Spot(0, 0)), 1);
    }
}
