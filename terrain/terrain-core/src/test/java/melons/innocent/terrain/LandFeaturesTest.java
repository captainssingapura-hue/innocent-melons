package melons.innocent.terrain;

import com.example.fxsuite.existence.Earthworks;
import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Terrains;
import com.example.fxsuite.math.graph.Directed;
import com.example.fxsuite.math.graph.Graph;
import melons.innocent.math.curves.Fold;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Land features laid over the ground, the later over the earlier: a band by how high the ground is, a steep slope's
 * rock, an area within its boundary, the sea to the right of its shore where the ground is under its level, a river
 * filling its bed to its banks and no further; each labelled, of a kind, in its colour; no two labelled the same.
 */
class LandFeaturesTest {

    private final Spot west = new Spot(-10_000, 0);
    private final Spot east = new Spot(10_000, 0);
    private final Spot head = new Spot(0, 8_000);
    private final Spot foot = new Spot(0, -2_000);
    private final List<Spot> field = List.of(new Spot(-9_000, 2_000), new Spot(-6_000, 2_000), new Spot(-6_000, 5_000),
            new Spot(-9_000, 2_000));
    private final Hue blue = new Hue("#3060a0");

    private Fold fold(final double... xy) {
        final List<Knot> knots = new ArrayList<>();
        for (int i = 0; i < xy.length; i += 2) {
            knots.add(new Knot(xy[i], xy[i + 1]));
        }
        return new Fold(new Knots(knots));
    }

    /** A plain 5 m up, a coast along z = 0, the sea to the south; a river from north to south, its banks steep. */
    private Terrain terrain() {
        final List<Spot> spots = new ArrayList<>(List.of(west, east, head, foot));
        spots.addAll(field.subList(0, 3));
        return new Terrain("land", new Extent(-10_000, -10_000, 10_000, 10_000), new Graph<>(spots, List.of(
                new Directed<>(west, east, new Course.Straight()), new Directed<>(head, foot, new Course.Straight()),
                new Directed<>(field.get(0), field.get(1), new Course.Straight()),
                new Directed<>(field.get(1), field.get(2), new Course.Straight()),
                new Directed<>(field.get(2), field.get(3), new Course.Straight()))), List.of(
                new Plain("plain", 500),
                new Channel("river", new Path(List.of(head, foot)), fold(0, 300, 10_000, -200),
                        fold(0, 400, 10_000, 400), fold(0, 0, 100, 400)),
                new Coast("coast", new Path(List.of(west, east)), fold(0, 0, 2_000, -500), fold(0, 0, 1_000, 600))));
    }

    private LandFeatures features() {
        return new LandFeatures("land", terrain(), terrain().map(), List.of(
                new Band("meadow", new Terrains.Meadow(), new Hue("#80b050"), 0, 1_000),
                new Steep("rock", new Terrains.Rock(), new Hue("#808080"), 0.5),
                new Area("field", new Earthworks.Field(), new Hue("#c0b060"), new Path(field)),
                new Watercourse("river", new Features.River(), blue, new Path(List.of(head, foot)),
                        fold(0, 150, 10_000, 200), 0, 400),
                new Sea("sea", new Features.Sea(), blue, new Path(List.of(west, east)), 0)));
    }

    private Optional<String> at(final double x, final double z) {
        return features().at(x, z).map(Feature::label);
    }

    @Test
    void laidOverTheGroundTheLaterOverTheEarlier() {
        assertEquals(Optional.of("meadow"), at(5_000, 5_000), "the plain, meadow");
        assertEquals(Optional.of("field"), at(-7_000, 2_500), "within the field's boundary");
        assertEquals(Optional.of("meadow"), at(-8_500, 4_500), "outside it, across its slanting side");
        assertEquals(Optional.of("sea"), at(5_000, -3_000), "to the right of the shore, under the sea's level");
        assertEquals(Optional.of("river"), at(0, 5_000), "in the river's bed");
        assertEquals(Optional.of("rock"), at(250, 5_000), "its bank, above the water, rising steeply, bare");
        assertEquals(Optional.of("meadow"), at(1_000, 5_000), "beyond its banks, the meadow: nothing flooded");
        assertEquals(Optional.empty(), at(5_000, -9_000).filter(label -> !label.equals("sea")),
                "deep under the sea, nothing but the sea");
    }

    @Test
    void theWatersTopLevelAcrossItsReachWhetherTheGroundIsUnderItOrNot() {
        final LandFeatures features = features();
        assertEquals(0, features.water(5_000, -3_000), 1e-9, "the sea's top, out at sea");
        assertEquals(0, features.water(5_000, -10), 1e-9, "at the shore, its ground just under");
        assertTrue(Double.isNaN(features.water(5_000, 3_000)), "inland, beyond the sea's reach and the river's");
        // along the river, its top so deep over its bed as its curve says; across it, level, banks and all, as far as
        // it reaches
        assertEquals(315, features.water(0, 5_000), 1e-9);
        assertEquals(315, features.water(300, 5_000), 1e-9);
        assertTrue(Double.isNaN(features.water(500, 5_000)), "past its spread");
    }

    @Test
    void eachLabelledOfAKindInItsColourNoTwoLabelledTheSame() {
        final Feature river = features().laid().get(3);
        assertEquals(new Features.River(), river.kind());
        assertEquals(new Hue(0x30 / 255.0, 0x60 / 255.0, 0xa0 / 255.0), river.hue());
        assertThrows(IllegalArgumentException.class, () -> new Hue("3060a0"));
        assertThrows(IllegalArgumentException.class, () -> new LandFeatures("land", terrain(), terrain().map(), List.of(
                new Band("same", new Terrains.Meadow(), blue, 0, 1),
                new Band("same", new Terrains.Sand(), blue, 1, 2))));
        assertThrows(IllegalArgumentException.class, () -> new Area("open", new Earthworks.Field(), blue,
                new Path(field.subList(0, 3))));
    }
}
