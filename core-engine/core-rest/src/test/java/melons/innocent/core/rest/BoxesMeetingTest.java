package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Things with corners meeting, when what is asked is whether a path is barred. Two boxes apart do not meet, nor two
 * that only touch, face to face, as one stands on another or beside it. Run into each other they meet whichever way
 * they are turned and whichever way they came, and the one is pushed straight back out the way the two are least
 * run in. A box meets a round thing where the round thing meets it, pushed the other way. And a path through a
 * pillar is barred where the thing first runs into it.
 */
class BoxesMeetingTest {

    private final Touching touching = new Touching();
    private final Box crate = new Box(50, 50, 50);
    private final Box pillar = new Box(40, 200, 40);
    private final Placed standing = new Placed(new Spot(0, 25, 0));

    /** Where the crate meets the pillar, the pillar standing so far along. */
    private List<Touch> met(final Placed placed, final double along) {
        return touching.met(crate, placed, "pillar", pillar, new Placed(new Spot(0, 100, along)));
    }

    @Test
    void twoBoxesApartOrOnlyTouchingDoNotBarEachOther() {
        assertTrue(met(standing, 60).isEmpty(), "fifteen centimetres apart");
        assertTrue(met(standing, 45).isEmpty(), "face to face, only touching");
        assertTrue(touching.met(crate, new Placed(new Spot(0, 75, 0)), "under", crate, standing).isEmpty(),
                "one standing on another");
        assertTrue(met(standing, 44.99).isEmpty(), "run in by no more than counts as touching");
    }

    @Test
    void aBoxRunSidewaysIntoAnotherIsPushedStraightBackOutTheWayTheyAreLeastRunIn() {
        final List<Touch> touches = met(standing, 44);
        assertEquals(1, touches.size());
        final Touch touch = touches.getFirst();
        assertEquals(1, touch.deep(), 1e-9);
        assertEquals(0, touch.pushes().minus(new Spot(0, 0, -1)).length(), 1e-12, "back the way it came");
        assertEquals(0, touch.at().minus(new Spot(0, 25, 25)).length(), 1e-9, "at the middle of its face there");
        assertEquals("pillar", touch.on());
        // from the other side it is pushed the other way, and from above upward
        final Touch behind = touching.met(crate, new Placed(new Spot(0, 25, 88)), "pillar", pillar,
                new Placed(new Spot(0, 100, 44))).getFirst();
        assertEquals(0, behind.pushes().minus(new Spot(0, 0, 1)).length(), 1e-12);
        final Touch above = touching.met(crate, new Placed(new Spot(0, 224, 44)), "pillar", pillar,
                new Placed(new Spot(0, 100, 44))).getFirst();
        assertEquals(0, above.pushes().minus(new Spot(0, 1, 0)).length(), 1e-12);
        assertEquals(1, above.deep(), 1e-9);
    }

    @Test
    void aBoxTurnedCornerOnMeetsAFaceWithItsCorner() {
        final Placed cornerOn = new Placed(new Spot(0, 25, 0), new Turn(new Spot(0, 1, 0), Math.PI / 4));
        final double reaches = 25 * Math.sqrt(2);
        assertTrue(met(cornerOn, reaches + 20 + 0.5).isEmpty(), "half a centimetre short of it");
        final Touch touch = met(cornerOn, reaches + 20 - 1).getFirst();
        assertEquals(1, touch.deep(), 1e-9);
        assertEquals(0, touch.pushes().minus(new Spot(0, 0, -1)).length(), 1e-9, "straight off the face");
        assertEquals(0, touch.at().minus(new Spot(0, 25, reaches)).length(), 1e-9, "at the edge it leads with");
    }

    @Test
    void aBoxMeetsARoundThingWhereTheRoundThingMeetsItAndIsPushedTheOtherWay() {
        final Ellipsoid melon = new Ellipsoid(36, 28, 26);
        final List<Touch> touches = touching.met(crate, standing, "melon", melon, new Placed(new Spot(0, 14, 37)));
        assertEquals(1, touches.size());
        assertEquals(1, touches.getFirst().deep(), 1e-9);
        assertEquals(0, touches.getFirst().pushes().minus(new Spot(0, 0, -1)).length(), 1e-12);
        assertTrue(touching.met(crate, standing, "melon", melon, new Placed(new Spot(0, 14, 39))).isEmpty());
    }

    @Test
    void thingsAreSaidToBeRunIntoEachOtherOnlyWhereTheyAreWellIn() {
        assertFalse(touching.overlapping(crate, standing, pillar, new Placed(new Spot(0, 100, 44.5))));
        assertTrue(touching.overlapping(crate, standing, pillar, new Placed(new Spot(0, 100, 43))));
    }

    @Test
    void aCratesPathThroughAPillarIsBarredWhereItFirstRunsIntoIt() {
        final Placed pillarAt = new Placed(new Spot(0, 100, 60));
        final List<Collision> collisions = new InThePath(touching).of("crate", crate, standing,
                new Placed(new Spot(0, 25, 30)), Map.of("pillar",
                        new Blocker(pillar, pillarAt, pillarAt, new Spot(0, 0, 0))));
        assertEquals(1, collisions.size());
        final Collision collision = collisions.getFirst();
        assertEquals("pillar", collision.with());
        assertEquals(0.5, collision.share(), 0.01, "half way along, its face has come to the pillar's");
        assertEquals(15, collision.there().at().along(), 0.05, "and there it stood, just clear");
        assertTrue(collision.there().at().along() <= 15.02);
        assertEquals(0, collision.touch().pushes().minus(new Spot(0, 0, -1)).length(), 1e-12);
        assertTrue(new InThePath(touching).of("crate", crate, standing, new Placed(new Spot(0, 25, 14)),
                Map.of("pillar", new Blocker(pillar, pillarAt, pillarAt, new Spot(0, 0, 0)))).isEmpty(),
                "a path that stops short of it is not barred");
    }
}
