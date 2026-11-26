package melons.innocent.core.interaction;

import melons.innocent.core.motion.Spot;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** What is at hand: the nearest thing within reach before the one acting. */
class HandsTest {

    @Test
    void theNearestThingWithinReachBeforeThemIsAtHand() {
        final Hands hands = new Hands(new Spot(0, 100, 0), 0);
        assertEquals(Optional.of("the near melon"), hands.atHand(Map.of("the near melon", new Spot(20, 15, 80),
                "the far melon", new Spot(0, 15, 120), "the melon out of reach", new Spot(0, 15, 400))));
    }

    @Test
    void whatLiesBehindOrWellAsideIsNotAtHand() {
        final Hands hands = new Hands(new Spot(0, 100, 0), 0);
        assertFalse(hands.reaches(new Spot(0, 100, -60)));
        assertFalse(hands.reaches(new Spot(100, 100, 20)));
        assertTrue(hands.reaches(new Spot(40, 100, 60)));
        assertEquals(Optional.empty(), hands.atHand(Map.of("behind", new Spot(0, 100, -60))));
    }

    @Test
    void theHandsFaceRoundAsACompassDoesFromAheadTowardTheRight() {
        final Hands right = new Hands(new Spot(0, 100, 0), 90);
        assertEquals(1, right.ahead().across(), 1e-12);
        assertEquals(0, right.ahead().along(), 1e-12);
        assertTrue(right.reaches(new Spot(90, 100, 0)));
        assertFalse(right.reaches(new Spot(0, 100, 90)));
        final Spot before = right.before(50, -20);
        assertEquals(50, before.across(), 1e-9);
        assertEquals(80, before.up(), 1e-9);
    }

    @Test
    void whatIsFarOverheadIsOutOfReachThoughItIsCloseOverTheGround() {
        final Hands hands = new Hands(new Spot(0, 100, 0), 0);
        assertFalse(hands.reaches(new Spot(0, 300, 40)));
        assertTrue(hands.reaches(new Spot(0, 200, 40)));
    }

    @Test
    void ofTwoThingsAsNearTheOneFirstByNameIsAtHand() {
        final Hands hands = new Hands(new Spot(0, 100, 0), 0);
        assertEquals(Optional.of("a"), hands.atHand(Map.of("b", new Spot(30, 100, 60), "a", new Spot(-30, 100, 60))));
    }
}
