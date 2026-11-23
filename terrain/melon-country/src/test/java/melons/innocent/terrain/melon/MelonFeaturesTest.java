package melons.innocent.terrain.melon;

import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Terrains;
import melons.innocent.terrain.Feature;
import melons.innocent.terrain.LandFeatures;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Melon country's land features: the village in its meadows, its fields either side; the bay over the sea, where
 * the harbour is to be dredged too; the river in its bed beside the village; the woods on the range's flanks; moss on
 * Mount Melon; every feature of a kind, in a colour, no two labelled the same.
 */
class MelonFeaturesTest {

    private static final double M = 100;
    private final LandFeatures features = MelonFeatures.INSTANCE.features();

    private Optional<String> at(final double across, final double along) {
        return features.at(across * M, along * M).map(Feature::label);
    }

    @Test
    void theVillageInItsMeadowsItsFieldsEitherSide() {
        assertEquals(Optional.of("the Melon meadows"), at(0, 100));
        assertEquals(Optional.of("the village's west fields"), at(-450, 150));
        assertEquals(Optional.of("the village's east fields"), at(350, 200));
    }

    @Test
    void theBayOverTheSeaTheRiverInItsBedTheWoodsTheMoss() {
        assertEquals(Optional.of("Melon Bay"), at(0, -1_500));
        assertEquals(Optional.of("Melon Bay"), at(593, -673), "where the harbour is to be dredged, under the bay");
        // across the plain east of the village, where the ground is lowest, the river
        double lowest = Double.MAX_VALUE;
        double across = 0;
        for (double x = 400; x <= 800; x++) {
            final double height = features.ground().height(x * M, 125 * M);
            if (height < lowest) {
                lowest = height;
                across = x;
            }
        }
        assertEquals(Optional.of("the Melon River"), at(across, 125));
        assertEquals(Optional.of("the Melon meadows"), at(across + 60, 125), "its banks not flooded");
        assertEquals(Optional.of("the Melon Wood"), at(2_400, 1_200));
        assertEquals(Optional.of("the high moss"), at(2_100, 2_600), "Mount Melon's top");
    }

    @Test
    void everyFeatureOfAKindInAColourNoTwoLabelledTheSame() {
        assertEquals(11, features.laid().size());
        assertEquals(new Features.Bay(), features.laid().getLast().kind());
        assertEquals(new Terrains.Forest(), features.laid().get(5).kind());
        assertTrue(features.laid().stream().allMatch(feature -> feature.hue() != null));
    }
}
