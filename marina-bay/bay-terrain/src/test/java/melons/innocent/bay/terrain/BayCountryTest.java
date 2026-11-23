package melons.innocent.bay.terrain;

import melons.innocent.terrain.Feature;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.LandText;
import melons.innocent.terrain.Loop;
import melons.innocent.terrain.Spot;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Marina Bay on the terrain: the towers' ground level at 0 m, land; the bay dredged 8 m down under its water; the open
 * sea as deep; a garden lake dug 2 m down under its own, by its name; and the whole of it written as a country's text
 * and read back the same.
 */
class BayCountryTest {

    private static final double M = 100;

    private final BayLines lines = new BayLines();
    private final LandFeatures bay = new BayCountry(lines).features();

    private Optional<String> at(final LandFeatures country, final Spot spot) {
        return country.at(spot.x(), spot.z()).map(Feature::label);
    }

    @Test
    void theTowersGroundTheBayTheSeaALake() {
        assertEquals(0, bay.ground().height(0, 0), 1e-6, "the middle tower's ground");
        assertEquals(Optional.of("the land"), at(bay, new Spot(0, 0)));
        final Spot bayMiddle = new Loop(lines.basin()).pole();
        assertEquals(-8 * M, bay.ground().height(bayMiddle.x(), bayMiddle.z()), 1e-6);
        assertEquals(Optional.of("the sea and the bay"), at(bay, bayMiddle));
        assertEquals(-8 * M, bay.ground().height(2_300 * M, -1_900 * M), 1e-6, "the open sea, south-east");
        final Spot dragonfly = new Loop(lines.lakes().get("Dragonfly Lake")).pole();
        assertEquals(-2 * M, bay.ground().height(dragonfly.x(), dragonfly.z()), 1e-6);
        assertEquals(Optional.of("Dragonfly Lake"), at(bay, dragonfly));
        assertTrue(lines.lakes().size() >= 9);
    }

    @Test
    void writtenAndReadBackTheSame() {
        final LandFeatures read = LandText.INSTANCE.parse(LandText.INSTANCE.text(bay));
        for (double x = -2_350 * M; x < 2_350 * M; x += 97 * M) {
            for (double z = -1_950 * M; z < 1_750 * M; z += 89 * M) {
                assertEquals(bay.ground().height(x, z), read.ground().height(x, z), 1e-6);
                assertEquals(at(bay, new Spot(x, z)), at(read, new Spot(x, z)));
            }
        }
    }
}
