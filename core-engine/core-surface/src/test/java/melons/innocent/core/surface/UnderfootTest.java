package melons.innocent.core.surface;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.Slab;
import melons.innocent.core.rest.World;
import melons.innocent.physics.existences.Conditions;
import melons.innocent.physics.existences.Movers;
import melons.innocent.physics.existences.Shapes;
import melons.innocent.physics.existences.Surfaces;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What a mover is on, asked of a world at rest: the ground, a crate standing on it, a slab that leans, and a wagon
 * that goes. Each answers in its own frame; a provider that moves answers the same to one who moves with it, and
 * says nothing of its going, which only the frame the world keeps beside the answer tells.
 */
class UnderfootTest {

    private static final double TICK = 1.0 / 120;
    private static final double TILT = 12;
    /** Stand-ins for leaves of the materials' tree, which is not the core engine's: any node will do. */
    private static final String TURF = "the ground";

    private final Ground ground = new Ground();
    /** Nothing in this world is let go, so nothing is ever stepped. */
    private final Simulator still = (rigid, now, around, seconds) -> new Simulator.Stepped(now, Optional.empty());
    private final World world = new World(ground, still)
            .stood(new Rigid("the crate", new Box(100, 60, 100), 600), new Placed(new Spot(500, 30, 0)))
            .stood(new Rigid("the melon", new Ellipsoid(36, 28, 26), 960), new Placed(new Spot(900, 14, 0)))
            .stood(new Rigid("the ramp", new Slab(300, 20, 500), 2_400), new Placed(new Spot(0, 200, 1_000),
                    new Turn(new Spot(1, 0, 0), -Math.toRadians(TILT))))
            .driven(new Rigid("the wagon", new Box(1_200, 100, 300), 2_400), new Placed(new Spot(-2_000, 50, 600)),
                    new Moved(new Spot(1_000, 0, 0), new Spot(0, 0, 0)));
    /** Each provider's surface is of a kind of its own, and wet beyond a line across the ground. */
    private final Facts facts = new Facts() {
        @Override
        public Node material(final String provider, final Spot spot) {
            return provider.equals(TURF) ? new Shapes.Land() : provider.equals("the ramp") ? new Shapes.Slope()
                    : new Shapes.Box();
        }

        @Override
        public Node condition(final String provider, final Spot spot) {
            return provider.equals(TURF) && spot.along() < -500 ? new Conditions.Wet() : new Conditions.Dry();
        }
    };
    private final Underfoot underfoot = new Underfoot(world, ground, facts);

    @Test
    void overOpenGroundTheGroundAnswersLevelAndWithoutAnEdge() {
        final Patch patch = underfoot.under(new Spot(100, 160, -100), 400).orElseThrow().patch();
        assertEquals(TURF, patch.provider());
        assertEquals(new Surfaces.Level(), patch.form());
        assertEquals(new Spot(100, 0, -100), patch.at());
        assertEquals(160, patch.below(), 1e-9);
        assertEquals(0, patch.steep(), 1e-9);
        assertEquals(Double.POSITIVE_INFINITY, patch.within(), 0);
        assertEquals(new Shapes.Land(), patch.material());
        assertEquals(new Conditions.Dry(), patch.condition());
        assertEquals(new Spot(0, 0, 0), patch.falls(), "level, it falls no way");
        assertTrue(underfoot.under(new Spot(100, 600, -100), 400).isEmpty(), "too far under to be what it is on");
    }

    @Test
    void conditionIsAFactOfTheSpotAndNotOfTheWholeSurface() {
        assertEquals(new Conditions.Wet(), underfoot.under(new Spot(0, 10, -600), 50).orElseThrow().patch()
                .condition());
        assertEquals(new Conditions.Dry(), underfoot.under(new Spot(0, 10, -400), 50).orElseThrow().patch()
                .condition());
    }

    @Test
    void overACrateTheCrateAnswersInItsOwnFrameAndARoundThingProvidesNothing() {
        final Underfoot.Found found = underfoot.under(new Spot(530, 200, -20), 400).orElseThrow();
        assertEquals("the crate", found.patch().provider());
        assertEquals(new Spot(30, 30, -20), found.patch().at(), "from the crate's own middle");
        assertEquals(new Spot(530, 60, -20), found.at(), "and put back into the world by its frame");
        assertEquals(20, found.patch().within(), 1e-9, "twenty centimetres from its nearest edge");
        assertEquals(140, found.patch().below(), 1e-9);
        assertEquals(new Surfaces.Level(), found.patch().form());
        assertEquals(TURF, underfoot.under(new Spot(560, 200, 0), 400).orElseThrow().patch().provider(),
                "past its edge, the ground");
        assertEquals(TURF, underfoot.under(new Spot(900, 200, 0), 400).orElseThrow().patch().provider(),
                "over the melon, the ground: nothing stands on a round thing");
    }

    @Test
    void aSlabThatLeansAnswersASlopeAsSteepAsItLeansFallingTheWayItRunsDown() {
        final Underfoot.Found found = underfoot.under(new Spot(0, 400, 1_000), 400).orElseThrow();
        final Patch patch = found.patch();
        assertEquals("the ramp", patch.provider());
        assertEquals(new Surfaces.Slope(), patch.form());
        assertEquals(TILT, patch.steep(), 1e-9);
        assertEquals(new Spot(0, 1, 0), patch.faces(), "in its own frame its top faces straight up");
        assertEquals(10, patch.at().up(), 1e-9, "and lies half its thickness over its middle");
        // in the world the surface there is higher than the slab's middle, and it falls back toward the start
        assertEquals(200 + 10 / Math.cos(Math.toRadians(TILT)), found.at().up(), 1e-9);
        final Spot falls = found.frame().turned().turned(patch.falls());
        assertTrue(falls.along() < -0.9 && falls.up() < -0.1, "down the slope, toward the walk's beginning: " + falls);
    }

    @Test
    void aProviderThatGoesAnswersTheSameToOneWhoGoesWithItAndSaysNothingOfItsGoing() {
        final Spot onDeck = new Spot(100, 60, 50);
        final Underfoot.Found before = underfoot.under(new Spot(-2_000, 50, 600).plus(onDeck), 100).orElseThrow();
        assertEquals("the wagon", before.patch().provider());
        for (int tick = 0; tick < 120; tick++) {
            world.ticked(new Asked(List.of(), Map.of(), Map.of()), TICK);
        }
        final Spot wagon = world.placed("the wagon").orElseThrow().at();
        assertEquals(-1_000, wagon.across(), 1e-6, "a second on, the wagon has gone ten metres");
        final Underfoot.Found after = underfoot.under(wagon.plus(onDeck), 100).orElseThrow();
        assertEquals(before.patch(), after.patch(), "the surface is as it was, to one who went with it");
        assertEquals(new Spot(100, 50, 50), after.patch().at());
        assertEquals(1_000, after.at().across() - before.at().across(), 1e-6, "only its frame says where it is now");
    }

    @Test
    void whichMoverCanUseWhichFormIsSaidOnceForEveryEngine() {
        assertEquals(Passage.Use.FREELY, new Passage(new Movers.Walker(), new Surfaces.Stepped()).use());
        assertEquals(Passage.Use.NOT, new Passage(new Movers.Car(), new Surfaces.Stepped()).use());
        assertEquals("to a grade", new Passage(new Movers.Motorcycle(), new Surfaces.Slope()).how());
        assertEquals("rolls down", new Passage(new Movers.Roller(), new Surfaces.Slope()).how());
        assertEquals(Passage.Use.TO_A_LIMIT, new Passage(new Movers.Dragged(), new Surfaces.Slope()).use());
        assertEquals(Passage.Use.FREELY, new Passage(new Movers.Boat(), new Surfaces.Liquid()).use());
        assertEquals(Passage.Use.NOT, new Passage(new Movers.Boat(), new Surfaces.Level()).use());
        assertEquals(Passage.Use.FREELY, new Passage(new Movers.Stepping(), new Surfaces.Level()).use(),
                "asked of a way of meeting a surface as of a mover of that way");
        assertThrows(IllegalArgumentException.class, () -> new Passage(new Surfaces.Level(), new Surfaces.Level()));
    }
}
