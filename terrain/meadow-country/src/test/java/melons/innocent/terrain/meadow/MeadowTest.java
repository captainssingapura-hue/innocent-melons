package melons.innocent.terrain.meadow;

import melons.innocent.terrain.Contour;
import melons.innocent.terrain.Feature;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Spot;
import melons.innocent.terrain.Topography;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The meadow: the village on the valley's floor, level at the river, in its meadows; the river in its channel, 1.5 m
 * down; the pond in its hollow; the churchyard level at 12 m on its hill; the moors at 100 m at the edge; and no step
 * anywhere between two points 10 m apart steeper than two in one.
 */
class MeadowTest {

    private static final double M = 100;

    private final LandFeatures features = MeadowFeatures.INSTANCE.features();
    private final Topography ground = (Topography) features.ground();

    private Optional<String> at(final double x, final double z) {
        return features.at(x, z).map(Feature::label);
    }

    @Test
    void theVillageOnTheFloorTheRiverInItsChannelThePondInItsHollow() {
        assertEquals(0, ground.height(0, 40 * M), 1, "the village, on the floor, level");
        assertEquals(Optional.of("the meadows"), at(0, 40 * M));
        final double river = MeadowMap.INSTANCE.riverAt(0);
        assertEquals(-150, ground.height(0, river), 1, "the river's bed");
        assertEquals(Optional.of("the river"), at(0, river));
        assertEquals(Optional.of("the meadows"), at(0, river + 12 * M), "past its bank");
        final Contour hollow = ground.contours().stream().filter(c -> c.height() == 4 * M).findFirst().orElseThrow();
        final Spot deepest = hollow.loop().pole();
        assertTrue(ground.height(deepest.x(), deepest.z()) < 3 * M, "the hollow under the pond's level");
        assertEquals(Optional.of("the pond"), at(deepest.x(), deepest.z()));
    }

    @Test
    void theChurchyardLevelTheMoorsAtTheEdge() {
        final Contour yard = ground.contours().stream().filter(Contour::level).filter(c -> c.height() == 12 * M)
                .findFirst().orElseThrow();
        final Spot middle = yard.loop().pole();
        assertEquals(12 * M, ground.height(middle.x(), middle.z()), 1);
        assertEquals(100 * M, ground.height(0, ground.extent().z1()), 1);
        assertEquals(Optional.of("the moors"), at(0, ground.extent().z1() - 50 * M));
    }

    @Test
    void noStepAnywhere() {
        double steepest = 0;
        for (double x = ground.extent().x0() + 10 * M; x < ground.extent().x1() - 20 * M; x += 197 * M) {
            for (double z = ground.extent().z0() + 10 * M; z < ground.extent().z1() - 20 * M; z += 101 * M) {
                final double here = ground.height(x, z);
                steepest = Math.max(steepest, Math.abs(ground.height(x + 10 * M, z) - here) / (10 * M));
                steepest = Math.max(steepest, Math.abs(ground.height(x, z + 10 * M) - here) / (10 * M));
            }
        }
        assertTrue(steepest < 2, "steepest " + steepest);
    }
}
