package melons.innocent.terrain.melon;

import melons.innocent.terrain.Terrain;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Melon country: the village on the level plain, its lake and car park with it; the Melon Range to the north-east,
 * Mount Melon its highest peak at the main ridge's end, a saddle lower on it; the river coming in from beyond the
 * north edge down its valley, its bed the lowest of the ground across it, below the plain beside the village and
 * deepening to its mouth; the bay deepening out to the open sea; the
 * river's mouth a spot the shore and the river share.
 */
class MelonCountryTest {

    private static final double M = 100;
    private final Terrain country = MelonCountry.INSTANCE.terrain();

    private double at(final double across, final double along) {
        return country.height(across * M, along * M) / M;
    }

    @Test
    void theVillageOnTheLevelPlainItsLakeAndCarParkWithIt() {
        // the lane, the yards either side of it, the lake behind where the avatar starts, the car park off its end
        for (final double[] spot : List.of(new double[] {0, 0}, new double[] {0, 180}, new double[] {-60, 100},
                new double[] {60, 100}, new double[] {0, -34}, new double[] {-60, -65}, new double[] {120, -34})) {
            assertEquals(12, at(spot[0], spot[1]), 1e-9, "level at " + spot[0] + ", " + spot[1]);
        }
    }

    @Test
    void mountMelonHighInTheRangeTheRiverDownItsValleyBelowThePlainDeepeningToItsMouth() {
        assertEquals(760, at(2_100, 2_600), 1, "Mount Melon, at the main ridge's end, over the river's valley");
        assertTrue(at(2_250, 1_800) > 500 && at(850, 3_050) > 500, "its spur and the west ridge, high");
        assertTrue(at(2_570, 2_755) < at(2_100, 2_600) - 100, "a saddle on the main ridge, lower than the peak");
        // across its valley, from the west ridge to Mount Melon's flank, the river's bed lowest, sides high both ways
        for (final double along : new double[] {3_400, 2_600, 1_800, 1_400}) {
            double floor = Double.MAX_VALUE;
            double floorAt = 0;
            for (double across = 450; across <= 2_400; across += 5) {
                if (at(across, along) < floor) {
                    floor = at(across, along);
                    floorAt = across;
                }
            }
            assertTrue(at(floorAt - 400, along) > floor + 50 && at(floorAt + 400, along) > floor + 50,
                    "a valley " + along + " m along, its floor " + floor + " m at " + floorAt);
        }
        assertTrue(at(1_470, 3_400) > 150, "the river comes in high from beyond the north edge");
        // across the plain east of the village, its lowest where the river's bed is
        double beside = Double.MAX_VALUE;
        for (double across = 400; across <= 800; across++) {
            beside = Math.min(beside, at(across, 125));
        }
        assertTrue(beside > 7 && beside < 10, "the river's bed beside the village, below the plain: " + beside);
        assertTrue(at(300, -450) < 0, "its bed at its mouth, under the sea's level");
    }

    @Test
    void theBayDeepeningOutToTheOpenSea() {
        // west of the harbour, out from the bay's head
        final double near = at(-200, -800);
        final double out = at(-200, -1_300);
        final double open = at(-200, -2_500);
        assertTrue(near < 0 && out < near && open < out, "deepening: " + near + ", " + out + ", " + open);
        assertTrue(at(-1_050, -1_650) > 0, "Melon Point stands out of the sea");
        assertTrue(out > -16, "the bay shelving gently west of the harbour, not yet as deep as it: " + out);
    }

    @Test
    void theHarbourDeepCloseInItsSideToTheShoreSteepItsWayOutAsDeep() {
        // off the middle of the harbour's shore, the bay's head bowed in to (645, -508): 0 at the shore, 16 m 45 m out
        final double[] seaward = {-0.333, -0.942};
        final double shore = at(645, -508);
        final double fortyFive = at(645 + 45 * seaward[0], -508 + 45 * seaward[1]);
        final double eighty = at(645 + 80 * seaward[0], -508 + 80 * seaward[1]);
        assertTrue(shore > -1 && shore < 1, "the shore at the sea's level: " + shore);
        assertTrue(fortyFive < -12, "steep: " + fortyFive + " m, 45 m out");
        assertEquals(-16, eighty, 0.01, "the harbour's floor, 80 m out");
        assertEquals(-16, at(720, -1_300), 0.01, "its way out, as deep");
        assertTrue(at(645 - 60 * seaward[0], -508 - 60 * seaward[1]) > 1, "the land behind its shore untouched");
        assertTrue(country.map().vertices().has(MelonCountry.INSTANCE.river().get(8)));
        assertEquals(MelonCountry.INSTANCE.river().get(8), MelonCountry.INSTANCE.shore().get(6),
                "the river's mouth, a spot it shares with the shore");
    }
}
