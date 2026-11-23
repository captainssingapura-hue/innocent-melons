package melons.innocent.terrain;

import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Terrains;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Line;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Made works over the ground: a basin dredged, its bed level, its bank inside its edge, so the ground as it was meets
 * the edge itself; land built up, level within, its bank falling outside; what lies outside a region's loops a region
 * too. And a whole country written as text and read back the same: its ground, its works and its features.
 */
class WorkTest {

    private static final double M = 100;
    private static final Extent EXTENT = new Extent(-1_000 * M, -1_000 * M, 1_000 * M, 1_000 * M);

    private Loop circle(final double metres) {
        final List<Spot> round = new ArrayList<>();
        for (int k = 0; k < 90; k++) {
            final double a = 2 * Math.PI * k / 90;
            round.add(new Spot(metres * M * Math.cos(a), metres * M * Math.sin(a)));
        }
        return new Loop(round);
    }

    private final Topography flat = new Topography("flat", EXTENT, 0, List.of(), List.of(), List.of());

    @Test
    void aBasinDredgedItsBankInsideItsEdge() {
        final Work basin = new Work("a basin", new Region(List.of(circle(100)), false), -8 * M, 12 * M, 8.0 / 12,
                Double.POSITIVE_INFINITY);
        final Ground ground = new Worked(flat, List.of(basin));
        assertEquals(-8 * M, ground.height(0, 0), 1e-6, "its bed");
        assertEquals(-4 * M, ground.height(94 * M, 0), 1, "halfway down its bank");
        assertEquals(0, ground.height(100 * M, 0), 1, "the ground as it was at its edge");
        assertEquals(0, ground.height(120 * M, 0), 1e-9, "outside, as it was");
    }

    @Test
    void landBuiltUpItsBankOutsideWhatLiesOutsideARegion() {
        final Topography sea = new Topography("the sea", EXTENT, -8 * M, List.of(), List.of(), List.of());
        final Work land = new Work("land", new Region(List.of(circle(100)), false), 0, 0, Double.POSITIVE_INFINITY,
                8.0 / 12);
        final Ground ground = new Worked(sea, List.of(land));
        assertEquals(0, ground.height(50 * M, 0), 1e-9);
        assertEquals(-4 * M, ground.height(106 * M, 0), 1);
        assertEquals(-8 * M, ground.height(130 * M, 0), 1e-9);
        final Region round = new Region(List.of(circle(100)), true);
        assertTrue(round.holds(200 * M, 0) && !round.holds(0, 0));
        assertTrue(round.signed(200 * M, 0) < 0 && round.signed(0, 0) > 0);
    }

    @Test
    void aCountryWrittenAndReadBackTheSame() {
        final Topography hill = new Topography("a country", EXTENT, 0,
                List.of(new Contour(10 * M, circle(400)), new Contour(20 * M, circle(200), true)), List.of(),
                List.of());
        final List<Spot> shore = new ArrayList<>(circle(100).spots().stream().map(s -> new Spot(s.x() + 600 * M,
                s.z())).toList());
        shore.add(shore.getFirst());
        final List<Spot> course = List.of(new Spot(-900 * M, -900 * M), new Spot(0, -800 * M),
                new Spot(900 * M, -900 * M));
        final LandText text = LandText.INSTANCE;
        final LandFeatures country = text.parse(text.text(new LandFeatures("a country", new Worked(hill, List.of(
                new Work("a pond, dug", new Region(List.of(new Loop(shore.subList(0, shore.size() - 1))), false),
                        -2 * M, 6 * M, 2.0 / 6, Double.POSITIVE_INFINITY))), map(shore, course), List.of(
                new Band("grass", new Terrains.Meadow(), new Hue("#80b050"), -100 * M, 100 * M),
                new Watercourse("a stream", new Features.Stream(), new Hue("#3060a0"), new Path(course),
                        new Line(new Knot(0, 50), new Knot(2_000 * M, 50)), Double.NEGATIVE_INFINITY, 3 * M),
                new Lake("a pond", new Features.Pond(), new Hue("#4080c0"), new Path(shore), -50)))));
        assertEquals(text.text(country), text.text(text.parse(text.text(country))));
        assertEquals(20 * M, country.ground().height(0, 0), 1, "the hill's level top");
        assertEquals(-2 * M, country.ground().height(600 * M, 0), 1, "the pond's dug bed");
        assertEquals("a pond", country.at(600 * M, 0).orElseThrow().label());
        assertEquals(3, country.laid().size());
    }

    private com.example.fxsuite.math.graph.Graph<Spot, Course> map(final List<Spot> shore, final List<Spot> course) {
        final List<com.example.fxsuite.math.graph.Edge<Spot, Course>> lines = new ArrayList<>();
        final java.util.Set<Spot> spots = new java.util.LinkedHashSet<>(shore);
        spots.addAll(course);
        for (final List<Spot> way : List.of(shore, course)) {
            for (int i = 1; i < way.size(); i++) {
                lines.add(new com.example.fxsuite.math.graph.Directed<>(way.get(i - 1), way.get(i),
                        new Course.Straight()));
            }
        }
        return new com.example.fxsuite.math.graph.Graph<>(List.copyOf(spots), lines);
    }
}
