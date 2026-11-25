package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.physics.existences.Rolls;
import melons.innocent.physics.existences.Shapes;
import melons.innocent.physics.existences.Tips;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The check, on one thing. Touched by nothing it falls, its velocity changed by gravity's pull for the tick and its
 * turning not at all. Standing on the ground, or with its middle anywhere over the ground it is held on, it is held
 * and nothing changes. Held only under one end, it tips about the edge of what holds it nearest its middle, as fast
 * as its weight's turning there over how hard it is to turn about that line, and the way that lowers its middle. A
 * box's corners are where its size says, turned as it is turned; it weighs what its size and its stuff make it, and
 * is of the kind physics calls a box; and
 * the outline round where a thing is touched holds what is over it and says the nearest of its edges.
 */
class ReckoningTest {

    private static final double TICK = 1.0 / 120;
    private final Box girder = new Box(400, 20, 40);
    private final Rigid beam = new Rigid("beam", girder, 7_850);
    private final Reckoning reckoning = new Reckoning();
    private final Touching touching = new Touching();

    @Test
    void touchedByNothingItFallsByGravitysPullForTheTick() {
        final Reckoned reckoned = reckoning.of(beam, new Placed(new Spot(0, 500, 0)), List.of(), TICK);
        assertFalse(reckoned.held());
        assertEquals(-981 * TICK, reckoned.velocity().up(), 1e-12);
        assertEquals(0, reckoned.velocity().across() + reckoned.velocity().along() + reckoned.spin().length(), 0);
        assertTrue(reckoned.hinge().isEmpty(), "falling, it is held along no line");
    }

    @Test
    void standingOnTheGroundOrWithItsMiddleOverWhatHoldsItItIsHeld() {
        final Placed lying = new Placed(new Spot(0, 10, 0));
        final List<Touch> onTheGround = touching.grounded(girder, lying, new Ground());
        assertEquals(4, onTheGround.size(), "its four lower corners, and not its upper");
        assertTrue(reckoning.of(beam, lying, onTheGround, TICK).held());
        final Box pillar = new Box(40, 100, 40);
        final Placed raised = new Placed(new Spot(0, 110, 0));
        final List<Touch> onOne = touching.held(girder, raised, "pillar", pillar, new Placed(new Spot(15, 50, 0)));
        assertEquals(4, onOne.size(), "the pillar's four upper corners, in the beam's underside");
        assertTrue(onOne.stream().allMatch(touch -> touch.on().equals("pillar") && Math.abs(touch.deep()) < 1e-9));
        assertTrue(reckoning.of(beam, raised, onOne, TICK).held(), "its middle is over the pillar's top");
        assertTrue(touching.held(girder, raised, "pillar", pillar, new Placed(new Spot(0, 49, 0))).isEmpty(),
                "a pillar a centimetre short of it does not touch it");
    }

    @Test
    void heldOnlyUnderOneEndItTipsAboutTheNearestEdgeOfWhatHoldsIt() {
        final Placed raised = new Placed(new Spot(0, 110, 0));
        final List<Touch> underOneEnd = touching.held(girder, raised, "pillar", new Box(40, 100, 40),
                new Placed(new Spot(-180, 50, 0)));
        final Reckoned reckoned = reckoning.of(beam, raised, underOneEnd, TICK);
        assertFalse(reckoned.held());
        final Reckoned.Hinge hinge = reckoned.hinge().orElseThrow();
        assertEquals(-160, hinge.through().across(), 1e-9, "the pillar's edge nearest the beam's middle");
        assertEquals(100, hinge.through().up(), 1e-9);
        assertEquals(1, Math.abs(hinge.along().along()), 1e-9, "a line lying along that edge");
        // its weight a hundred and sixty centimetres out, over its own turning and its middle's distance from the line
        final double quickening = 981 * 160 / ((400 * 400 + 20 * 20) / 12.0 + 160 * 160 + 10 * 10);
        assertEquals(quickening * TICK, reckoned.spin().length(), 1e-9);
        assertEquals(0, reckoned.velocity().length(), 0, "tipping, its velocity is its turning's doing");
        final Spot going = reckoned.spin().cross(raised.at().minus(hinge.through()));
        assertTrue(going.up() < 0, "the way it turns lowers its middle");
    }

    @Test
    void aBoxWeighsWhatItsSizeAndStuffMakeItAndItsCornersAreWhereItsSizeSays() {
        assertEquals(new Shapes.Box(), girder.kind(), "of the kind of shape physics calls a box");
        assertTrue(girder.kind().answer(Tips.class).isPresent()
                && girder.kind().answer(Rolls.class).isEmpty(), "which tips, and never rolls");
        assertEquals(7_850 * 4 * 0.2 * 0.4, beam.kilograms(), 1e-9);
        assertEquals(beam.kilograms() / 12 * (20 * 20 + 40 * 40), beam.turnsHard(new Spot(1, 0, 0)), 1e-6);
        final Placed onEnd = new Placed(new Spot(0, 200, 0), new Turn(new Spot(0, 0, 1), Math.PI / 2));
        final double lowest = onEnd.corners(girder).stream().mapToDouble(Spot::up).min().orElseThrow();
        final double widest = onEnd.corners(girder).stream().mapToDouble(Spot::across).max().orElseThrow();
        assertEquals(0, lowest, 1e-9, "stood on its end, it reaches the ground");
        assertEquals(10, widest, 1e-9, "and is as wide as it was high");
        assertEquals(1, onEnd.lying(new Spot(0, 1, 0)).across(), 1e-9, "what is up lies along it");
    }

    @Test
    void theOutlineRoundWhereAThingIsTouchedHoldsWhatIsOverItAndSaysItsNearestEdge() {
        final Hull square = new Hull(List.of(new Spot(-20, 100, -20), new Spot(20, 100, -20), new Spot(20, 100, 20),
                new Spot(-20, 100, 20), new Spot(0, 100, 0), new Spot(20, 100, 20)));
        assertEquals(4, square.round().size(), "a spot inside it and one said twice are no corners");
        assertTrue(square.holds(new Spot(19, 500, -19), 0.01));
        assertTrue(square.holds(new Spot(20, 0, 0), 0.01), "over its very edge, it is held");
        assertFalse(square.holds(new Spot(20.5, 0, 0), 0.01));
        final Hull.Edge edge = square.nearest(new Spot(60, 0, 5));
        assertFalse(edge.corner());
        assertEquals(20, edge.from().across(), 0);
        assertEquals(20, edge.to().across(), 0);
        assertTrue(square.nearest(new Spot(60, 0, 60)).corner(), "beyond a corner, the corner alone is nearest");
        final Hull line = new Hull(List.of(new Spot(0, 0, 0), new Spot(10, 0, 0), new Spot(5, 0, 0)));
        assertEquals(2, line.round().size());
        assertTrue(line.holds(new Spot(7, 0, 0), 0.01));
        assertFalse(line.holds(new Spot(7, 0, 1), 0.01));
        assertTrue(new Hull(List.of(new Spot(3, 0, 3))).holds(new Spot(3, 9, 3), 0.01));
    }
}
