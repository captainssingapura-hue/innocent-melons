package melons.innocent.terrain.melon;

import melons.innocent.terrain.Feature;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.LandText;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Melon country written as a country's text and read back: as high everywhere, and the same feature lying at
 * every spot, but within a metre or two of a line its map bows, now run straight between the points it was traced
 * through.
 */
class MelonTextTest {

    private static final double M = 100;

    @Test
    void asHighEverywhereTheSameFeaturesLying() {
        final LandFeatures melon = MelonFeatures.INSTANCE.features();
        final LandFeatures read = LandText.INSTANCE.parse(LandText.INSTANCE.text(melon));
        int differ = 0;
        int count = 0;
        for (double x = -2_900 * M; x < 2_900 * M; x += 173 * M) {
            for (double z = -2_500 * M; z < 3_300 * M; z += 181 * M) {
                assertEquals(melon.ground().height(x, z), read.ground().height(x, z), 1e-6);
                if (!melon.at(x, z).map(Feature::label).equals(read.at(x, z).map(Feature::label))) {
                    differ++;
                }
                count++;
            }
        }
        assertEquals(0, differ, differ + " of " + count + " spots lie in another feature");
    }
}
