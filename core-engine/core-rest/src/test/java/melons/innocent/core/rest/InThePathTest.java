package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What stands in a thing's path, and where two round things meet. Two balls meet on the line between their middles,
 * as deep as they overlap, each pushed straight away from the other; a melon reaches as far as it does the way it
 * is turned. A path is barred where the thing going along it first touches what stands in it, found to a small
 * part of the path; not at all, by what it passes beside; at its very start, by what it touched already; and
 * against a thing that is itself moving, where the two were at the same moment.
 */
class InThePathTest {

    private final Ellipsoid ball = new Ellipsoid(30);
    private final Ellipsoid melon = new Ellipsoid(36, 28, 26);
    private final InThePath inThePath = new InThePath(new Touching());

    /** A blocker standing where it is, going nowhere. */
    private Blocker standing(final ShapeImpl<?> shape, final Spot at) {
        return new Blocker(shape, new Placed(at), new Placed(at), new Spot(0, 0, 0));
    }

    @Test
    void anEllipsoidReachesAsFarAsItIsThroughItsMiddleEachWay() {
        assertEquals(18, melon.out(new Spot(1, 0, 0)), 1e-12);
        assertEquals(14, melon.out(new Spot(0, 1, 0)), 1e-12);
        assertEquals(13, melon.out(new Spot(0, 0, -1)), 1e-12);
        final double slantwise = melon.out(new Spot(1, 0, 1).unit());
        assertTrue(slantwise > 13 && slantwise < 18, "between its shortest and its longest: " + slantwise);
        assertEquals(15, ball.out(new Spot(0.6, 0, 0.8)), 1e-12);
    }

    @Test
    void twoBallsMeetOnTheLineBetweenTheirMiddlesAsDeepAsTheyOverlap() {
        final List<Touch> touches = new RoundTouch(0.01, 20).rounds(ball, new Placed(new Spot(0, 15, 0)), "other",
                ball, new Placed(new Spot(0, 15, 29)));
        assertEquals(1, touches.size());
        final Touch touch = touches.getFirst();
        assertEquals(1, touch.deep(), 1e-12);
        assertEquals(0, touch.pushes().minus(new Spot(0, 0, -1)).length(), 1e-12,
                "pushed straight away from the other");
        assertEquals(0, touch.at().minus(new Spot(0, 15, 14.5)).length(), 1e-12);
        assertEquals("other", touch.on());
        assertTrue(new RoundTouch(0.01, 20).rounds(ball, new Placed(new Spot(0, 15, 0)), "other", ball,
                new Placed(new Spot(0, 15, 31))).isEmpty(), "a centimetre apart they do not meet");
    }

    @Test
    void aMelonMeetsAsFarOutAsItReachesTheWayItIsTurned() {
        final Touching touching = new Touching();
        // lying across, it is thirteen out toward a ball ahead of it; turned a quarter round, eighteen
        assertEquals(0.5, touching.met(melon, new Placed(new Spot(0, 14, 0)), "ball", ball,
                new Placed(new Spot(0, 14, 27.5))).getFirst().deep(), 1e-9);
        final Placed turned = new Placed(new Spot(0, 14, 0), new Turn(new Spot(0, 1, 0), Math.PI / 2));
        assertEquals(0.5, touching.met(melon, turned, "ball", ball, new Placed(new Spot(0, 14, 32.5))).getFirst()
                .deep(), 1e-9);
        assertTrue(touching.held(melon, turned, "ball", ball, new Placed(new Spot(0, 14, 32.5))).isEmpty(),
                "a round thing holds nothing up: only a path meets it");
    }

    @Test
    void aPathIsBarredWhereTheThingFirstTouchesWhatStandsInIt() {
        // a ball going six centimetres toward a box whose near face it touches after three
        final List<Collision> collisions = inThePath.of("ball", ball, new Placed(new Spot(0, 15, 0)),
                new Placed(new Spot(0, 15, -6)), Map.of("box", standing(new Box(64, 200, 64),
                        new Spot(0, 100, -50))));
        assertEquals(1, collisions.size());
        final Collision collision = collisions.getFirst();
        assertEquals("ball", collision.thing());
        assertEquals("box", collision.with());
        assertEquals(0.5, collision.share(), 0.01);
        assertEquals(-3, collision.there().at().along(), 0.06);
        assertTrue(collision.there().at().along() >= -3.02, "set no further than where it first touched");
        assertEquals(0, collision.touch().pushes().minus(new Spot(0, 0, 1)).length(), 1e-12,
                "pushed back the way it came");
    }

    @Test
    void whatAThingPassesBesideDoesNotBarItsPath() {
        assertEquals(List.of(), inThePath.of("ball", ball, new Placed(new Spot(0, 15, 0)),
                new Placed(new Spot(0, 15, -6)), Map.of("box", standing(new Box(64, 200, 64),
                        new Spot(60, 100, -50)))));
        assertEquals(List.of(), inThePath.of("ball", ball, new Placed(new Spot(0, 15, 0)),
                new Placed(new Spot(0, 15, -2)), Map.of("box", standing(new Box(64, 200, 64),
                        new Spot(0, 100, -50)))), "nor what it has not yet come to");
    }

    @Test
    void whatAThingTouchedAlreadyIsMetAtItsPathsStart() {
        final Collision collision = inThePath.of("ball", ball, new Placed(new Spot(0, 15, -3)),
                new Placed(new Spot(0, 15, -5)), Map.of("box", standing(new Box(64, 200, 64),
                        new Spot(0, 100, -50)))).getFirst();
        assertEquals(0, collision.share(), 0);
        assertEquals(0, collision.there().at().minus(new Spot(0, 15, -3)).length(), 1e-12);
    }

    @Test
    void aThingThatIsItselfMovingIsMetWhereBothWereAtTheSameMoment() {
        // a ball going two centimetres toward another coming ten at it: thirty apart five sixths of the way on
        final Spot together = new Spot(0, 0, 40);
        final Collision collision = inThePath.of("ball", ball, new Placed(new Spot(0, 15, 0)),
                new Placed(new Spot(0, 15, -2)), Map.of("other", new Blocker(ball, new Placed(new Spot(0, 15, -40)),
                        new Placed(new Spot(0, 15, -30)), together))).getFirst();
        assertEquals(5.0 / 6, collision.share(), 0.01);
        assertEquals(together, collision.touch().going(), "carrying how the two are to go against each other");
    }

    @Test
    void oneCollisionToEachThingMetInTheOrderOfTheirNames() {
        final List<Collision> collisions = inThePath.of("ball", ball, new Placed(new Spot(0, 15, 0)),
                new Placed(new Spot(0, 15, -6)), Map.of(
                        "the wall", standing(new Box(64, 200, 64), new Spot(0, 100, -50)),
                        "a post", standing(new Box(10, 200, 10), new Spot(0, 100, -22))));
        assertEquals(List.of("a post", "the wall"), collisions.stream().map(Collision::with).toList());
    }
}
