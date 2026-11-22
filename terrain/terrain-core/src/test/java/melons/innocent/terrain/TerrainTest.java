package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Directed;
import com.example.fxsuite.math.graph.Graph;
import melons.innocent.math.curves.Fold;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Line;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A terrain made from its map's lines and its landforms, laid in turn: a plain levelling the ground, a mount raised on
 * it, a channel cut into it along its course, its bed falling as its curve says, a coast sinking what lies to the
 * right of its shore under the sea's level; a bowed line following its curve; a path only along the map's lines.
 */
class TerrainTest {

    private static final double CLOSE = 1e-6;

    private final Spot west = new Spot(-10_000, 0);
    private final Spot east = new Spot(10_000, 0);
    private final Spot head = new Spot(0, 8_000);
    private final Spot foot = new Spot(0, 2_000);
    private final Extent extent = new Extent(-10_000, -10_000, 10_000, 10_000);

    /** The shore straight from west to east, the sea on its right, to the south; a river from north to south. */
    private Graph<Spot, Course> map(final Course shore) {
        return new Graph<>(List.of(west, east, head, foot), List.of(new Directed<>(west, east, shore),
                new Directed<>(head, foot, new Course.Straight())));
    }

    private Fold fold(final double... xy) {
        final List<Knot> knots = new java.util.ArrayList<>();
        for (int i = 0; i < xy.length; i += 2) {
            knots.add(new Knot(xy[i], xy[i + 1]));
        }
        return new Fold(new Knots(knots));
    }

    @Test
    void aPlainLevellingTheGroundACoastSinkingWhatLiesSeaward() {
        final Terrain terrain = new Terrain("shore", extent, map(new Course.Straight()), List.of(
                new Plain("plain", 500),
                new Coast("coast", new Path(List.of(west, east)), fold(0, 0, 1_000, -300, 5_000, -900),
                        fold(0, 0, 1_000, 600))));
        assertEquals(500, terrain.height(0, 5_000), CLOSE, "far inland, the plain");
        assertEquals(300, terrain.height(0, 500), CLOSE, "halfway up the beach");
        assertEquals(-300, terrain.height(0, -1_000), CLOSE, "a thousand out to sea");
        assertEquals(-900, terrain.height(4_000, -9_000), CLOSE, "the open sea, as deep as the curve goes");
    }

    @Test
    void aMountRaisedAChannelCutAlongItsCourseItsBedFalling() {
        final Terrain terrain = new Terrain("valley", extent, map(new Course.Straight()), List.of(
                new Plain("plain", 500),
                new Mount("mount", new Spot(0, 8_000), fold(0, 2_000, 4_000, 0), 0, fold(0, 0, 1, 0)),
                new Channel("river", new Path(List.of(head, foot)), fold(0, 1_000, 6_000, 100),
                        fold(0, 200, 6_000, 200), fold(0, 0, 500, 1_000))));
        assertEquals(1_500, terrain.height(-2_000, 8_000), CLOSE, "halfway down the mount, away from the river");
        // on the river's line at its head, its bed; halfway along, its bed has fallen half its fall
        assertEquals(1_000, terrain.height(0, 8_000), CLOSE);
        assertEquals(550, terrain.height(0, 5_000), CLOSE);
        // a metre beyond the bed's edge, its bank has risen two; far off, the plain is as it was
        assertEquals(750, terrain.height(200, 5_000), CLOSE);
        assertEquals(500, terrain.height(-9_000, -5_000), CLOSE);
    }

    @Test
    void aRidgeRaisedAlongItsCrestItsFlanksFallingNeverLoweringTheGround() {
        // the river's line taken as a crest, rising from its head (2,000 high) to its foot (4,000)
        final Terrain terrain = new Terrain("ridge", extent, map(new Course.Straight()), List.of(
                new Plain("plain", 500),
                new Ridge("ridge", new Path(List.of(head, foot)), fold(0, 2_000, 6_000, 4_000),
                        fold(0, 0, 1_000, 1_000, 5_000, 3_000))));
        assertEquals(2_000, terrain.height(0, 8_000), CLOSE, "its crest at its head");
        assertEquals(3_000, terrain.height(0, 5_000), CLOSE, "halfway along, halfway up");
        assertEquals(2_000, terrain.height(1_000, 5_000), CLOSE, "a thousand off its crest, fallen a thousand");
        assertEquals(500, terrain.height(-9_000, -9_000), CLOSE, "far off, the plain, never lowered");
    }

    @Test
    void aBowedLineFollowingItsCurveAPathOnlyAlongTheMapsLines() {
        final Course bowed = new Course.Bowed(new Line(new Knot(0, 0), new Knot(20_000, 0)));
        assertTrue(new Terrain("bowed", extent, map(bowed), List.of(new Plain("plain", 0))).height(0, 0) == 0);
        final Course off = new Course.Bowed(fold(0, 0, 10_000, 3_000, 15_000, 0));
        assertThrows(IllegalArgumentException.class, () -> new Terrain("short", extent, map(off),
                List.of(new Plain("plain", 0))));
        // the shore bowed 3 km north at its middle: a spot 2 km north of the straight line is now at sea
        final Course north = new Course.Bowed(fold(0, 0, 10_000, 3_000, 20_000, 0));
        final Terrain bay = new Terrain("bay", extent, map(north), List.of(new Plain("plain", 500),
                new Coast("coast", new Path(List.of(west, east)), fold(0, 0, 5_000, -500), fold(0, 0, 1_000, 600))));
        assertTrue(bay.height(0, 2_000) < 0, "inside the bay");
        assertThrows(IllegalArgumentException.class, () -> new Terrain("against", extent, map(new Course.Straight()),
                List.of(new Coast("coast", new Path(List.of(east, west)), fold(0, 0, 1, 0), fold(0, 0, 1, 0)))));
    }
}
