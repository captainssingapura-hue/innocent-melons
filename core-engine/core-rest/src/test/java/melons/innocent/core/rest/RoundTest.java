package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.physics.existences.RollsAnyWay;
import melons.innocent.physics.existences.Shapes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round things, and what never moves. An ellipsoid is of the kind physics says rolls any way; it weighs what its
 * size and stuff make it and is easier to turn about its long way than across it. It touches the ground at its one
 * lowest spot, and a face of a box or a slab at the spot of it nearest that face, a slope under it and a chock
 * beside it alike, and nothing past the face's edge. On level ground it is held; on a slope it is not, unless
 * something it has come up against holds it.
 */
class RoundTest {

    private static final double TICK = 1.0 / 120;
    private static final double TILT = Math.toRadians(15);
    private final Ellipsoid melon = new Ellipsoid(36, 28, 26);
    private final Rigid fruit = new Rigid("melon", melon, 960);
    private final Touching touching = new Touching();
    private final Reckoning reckoning = new Reckoning();
    /** A slab five metres along, tilted so its far end is the higher, its top's middle where height is a metre. */
    private final Slab slab = new Slab(300, 20, 500);
    private final Turn tilted = new Turn(new Spot(1, 0, 0), -TILT);
    private final Placed ramp = new Placed(new Spot(0, 100, 0).minus(tilted.turned(new Spot(0, 10, 0))), tilted);

    /** Where a thing stands whose middle is so far over the slab's top, so far up the slope from its middle. */
    private Placed onTheRamp(final double over, final double upSlope) {
        return new Placed(ramp.at().plus(tilted.turned(new Spot(0, 10 + over, upSlope))), tilted);
    }

    @Test
    void anEllipsoidRollsAnyWayAndIsEasierToTurnAboutItsLongWay() {
        assertEquals(new Shapes.Ellipsoid(), melon.kind());
        assertTrue(fruit.rolls() && melon.kind().answer(RollsAnyWay.class).isPresent());
        assertFalse(fruit.unmoving());
        assertEquals(960 * Math.PI * 0.36 * 0.28 * 0.26 / 6, fruit.kilograms(), 1e-9);
        assertEquals(fruit.kilograms() / 20 * (28 * 28 + 26 * 26), fruit.turnsHard(new Spot(1, 0, 0)), 1e-6);
        assertTrue(fruit.turnsHard(new Spot(1, 0, 0)) < fruit.turnsHard(new Spot(0, 1, 0)));
        assertEquals(18, melon.reaches(), 0);
        assertEquals(18, melon.furthest(new Spot(1, 0, 0)).across(), 1e-12);
        assertEquals(-14, melon.furthest(new Spot(0, -1, 0)).up(), 1e-12);
        final Ellipsoid ball = new Ellipsoid(40);
        assertEquals(20, ball.furthest(new Spot(3, 4, 0)).length(), 1e-12, "a ball reaches as far every way");
    }

    @Test
    void itTouchesTheGroundAtItsLowestSpotAndIsHeldThereOnTheLevel() {
        final Placed lying = new Placed(new Spot(0, 14, 0));
        final List<Touch> touches = touching.grounded(melon, lying, new Ground());
        assertEquals(1, touches.size());
        assertEquals(0, touches.getFirst().at().up(), 1e-12);
        assertEquals(0, touches.getFirst().deep(), 1e-12);
        assertTrue(reckoning.of(fruit, lying, touches, TICK).held());
        assertTrue(touching.grounded(melon, new Placed(new Spot(0, 14.5, 0)), new Ground()).isEmpty(),
                "half a centimetre up, it touches nothing");
    }

    @Test
    void onASlopeItTouchesAtTheSpotNearestTheFaceAndIsNotHeldUnlessSomethingBesideItHoldsIt() {
        final Placed up = onTheRamp(14, 100);
        final List<Touch> touches = touching.held(melon, up, "ramp", slab, ramp);
        assertEquals(1, touches.size(), "the slab's top, and no other face of it");
        final Touch touch = touches.getFirst();
        assertEquals(Math.cos(TILT), touch.pushes().up(), 1e-9, "pushed square to the slope");
        assertEquals(0, touch.deep(), 1e-9);
        assertTrue(touch.at().along() > up.at().along(), "the spot it touches at is up the slope from its middle");
        final Reckoned reckoned = reckoning.of(fruit, up, touches, TICK);
        assertFalse(reckoned.held(), "so its weight turns it down the slope");
        assertTrue(reckoned.spin().cross(up.at().minus(touch.at())).along() < 0);
        // a chock lying on the slope just below it, higher than the melon's middle, its upper face against the melon
        final Box chock = new Box(280, 20, 10);
        final Placed chocked = onTheRamp(10, 100 - 13 - 5);
        final List<Touch> both = touching.held(melon, up, "chock", chock, chocked);
        assertEquals(1, both.size(), "the chock's face toward it");
        assertTrue(both.getFirst().pushes().along() > 0.9, "which pushes it back up the slope");
        assertTrue(reckoning.of(fruit, up, List.of(touch, both.getFirst()), TICK).held(), "and so it is held");
        assertTrue(touching.held(melon, onTheRamp(14, 270), "ramp", slab, ramp).isEmpty(),
                "past the slab's end there is no face under it");
        assertTrue(touching.held(new Box(50, 50, 50), new Placed(new Spot(0, 39, 0)), "melon", melon,
                new Placed(new Spot(0, 14, 0))).isEmpty(), "and nothing rests on a round thing yet");
    }
}
