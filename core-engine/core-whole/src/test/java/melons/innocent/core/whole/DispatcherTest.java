package melons.innocent.core.whole;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.loose.Loose;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Core;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.State;
import melons.innocent.core.surface.Facts;
import melons.innocent.core.tick.Ticker;
import melons.innocent.core.walk.Standing;
import melons.innocent.core.walk.Walker;
import melons.innocent.core.walk.Walks;
import melons.innocent.core.walk.Wish;
import melons.innocent.physics.existences.Conditions;
import melons.innocent.physics.existences.Shapes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A walker in a world, ticked with its things by the one dispatcher. It stands on the ground and on the tops of
 * things; a pillar in its way stops it, and asked a little aside it goes round; a kerb it steps up and down; a box
 * too high bars it; off the edge of a table it falls to the ground; on a wagon it is carried; and a melon rolled at
 * it stops against it. A whole and what is asked give the same every time, and the whole it was given is as it was.
 */
class DispatcherTest {

    private static final String SOMEONE = "someone";
    private final String ground = new Ground().name();
    private final Facts dry = new Facts() {
        @Override
        public Node material(final String provider, final Spot spot) {
            return new Shapes.Box();
        }

        @Override
        public Node condition(final String provider, final Spot spot) {
            return new Conditions.Dry();
        }
    };
    private final Dispatcher dispatcher = new Dispatcher(new Core<>(new Loose()), new Walks(), dry);
    private final Walker someone = new Walker(SOMEONE);
    private final Wish ahead = new Wish(1, 0, 0, false, false);
    private final Spot nowhere = new Spot(0, 0, 0);

    /** A world with {@code things} in it and the walker standing at {@code feet}, facing ahead. */
    private Whole world(final State things, final Spot feet) {
        return new Whole(things).walked(someone, new Standing(feet, 0));
    }

    private State open() {
        return new State(new Ground(), new Ticker());
    }

    /** The wholes of so many ticks from {@code from}, the walker asked the same at each. */
    private List<Given> run(final Whole from, final int ticks, final Wish wish) {
        final List<Given> all = new ArrayList<>();
        Whole whole = from;
        for (int tick = 0; tick < ticks; tick++) {
            all.add(dispatcher.ticked(whole, new Asking(new Asked(), Map.of(SOMEONE, wish))));
            whole = all.getLast().whole();
        }
        return all;
    }

    private Standing last(final List<Given> run) {
        return run.getLast().whole().standings().get(SOMEONE);
    }

    private double furthest(final List<Given> run) {
        return run.stream().mapToDouble(given -> given.whole().standings().get(SOMEONE).feet().along()).max()
                .orElseThrow();
    }

    @Test
    void setDownItStandsOnTheGroundAndWalksAsItIsAsked() {
        final List<Given> run = run(world(open(), nowhere), 120, ahead);
        assertEquals(Optional.of(ground), run.getFirst().whole().standings().get(SOMEONE).on());
        assertTrue(run.getFirst().stamped().happened().contains(new Happened.Landed(SOMEONE, ground)));
        assertEquals(320, last(run).going().along(), 0.1);
        assertEquals(0, last(run).feet().up(), 0);
    }

    @Test
    void aPillarInItsWayStopsItAndAskedALittleAsideItGoesRound() {
        final State things = open().stood(new Rigid("pillar", new Box(40, 200, 40), 2_400),
                new Placed(new Spot(0, 100, 300)));
        final List<Given> stopped = run(world(things, nowhere), 360, ahead);
        // the pillar's near side is at 280, and the walker reaches 32 before its middle
        assertTrue(furthest(stopped) <= 248.03, "never into the pillar: " + furthest(stopped));
        assertEquals(248, last(stopped).feet().along(), 0.1, "against it");
        assertTrue(last(stopped).afoot());
        final List<Given> round = run(world(things, nowhere), 600, new Wish(1, 0.5, 0, false, false));
        assertTrue(last(round).feet().along() > 400, "past it: " + last(round).feet());
        assertEquals(new Placed(new Spot(0, 100, 300)), round.getLast().whole().things().placed("pillar")
                .orElseThrow(), "which has not stirred");
    }

    @Test
    void aKerbItStepsUpOntoAndDownFromAndABoxTooHighBarsIt() {
        final State kerbed = open().stood(new Rigid("kerb", new Box(200, 15, 100), 2_400),
                new Placed(new Spot(0, 7.5, 200)));
        final List<Given> over = run(world(kerbed, nowhere), 360, ahead);
        assertEquals(15, over.stream().mapToDouble(given -> given.whole().standings().get(SOMEONE).feet().up())
                .max().orElseThrow(), 0, "up on the kerb as it crossed it");
        assertTrue(over.stream().anyMatch(given -> given.whole().standings().get(SOMEONE).on()
                .equals(Optional.of("kerb"))));
        assertEquals(0, last(over).feet().up(), 0, "and down again beyond it");
        assertTrue(last(over).feet().along() > 300 && last(over).afoot());
        final State boxed = open().stood(new Rigid("box", new Box(100, 60, 100), 600),
                new Placed(new Spot(0, 30, 200)));
        final List<Given> barred = run(world(boxed, nowhere), 360, ahead);
        assertTrue(furthest(barred) <= 118.03, "stopped by its side: " + furthest(barred));
        assertEquals(0, last(barred).feet().up(), 0);
    }

    @Test
    void offTheEdgeOfATableItFallsToTheGround() {
        final State things = open().stood(new Rigid("table", new Box(200, 100, 200), 600),
                new Placed(new Spot(0, 50, 0)));
        final List<Given> run = run(world(things, new Spot(0, 100, 0)), 360, ahead);
        assertEquals(Optional.of("table"), run.get(1).whole().standings().get(SOMEONE).on(), "standing on it");
        assertEquals(0, last(run).feet().up(), 0, "down on the ground");
        assertEquals(Optional.of(ground), last(run).on());
        assertTrue(last(run).feet().along() > 300, "and walked on: " + last(run).feet());
        assertEquals(1, run.stream().flatMap(given -> given.stamped().happened().stream())
                .filter(new Happened.Landed(SOMEONE, ground)::equals).count(), "having come down on it once");
    }

    @Test
    void onAWagonItIsCarriedAndWalksAcrossItsDeck() {
        final Spot going = new Spot(500, 0, 0);
        final State things = open().driven(new Rigid("wagon", new Box(1_200, 100, 300), 2_400),
                new Placed(new Spot(0, 50, 0)), new Moved(going, nowhere));
        final Whole riding = new Whole(things).walked(someone,
                new Standing(new Spot(0, 100, 0), 0, going, Optional.of("wagon"), 0));
        final List<Given> carried = run(riding, 120, new Wish());
        assertEquals(500, last(carried).feet().across(), 1e-6, "a second on, five metres along with it");
        assertEquals(Optional.of("wagon"), last(carried).on());
        assertEquals(100, last(carried).feet().up(), 0);
        final List<Given> walking = run(riding, 30, ahead);
        assertTrue(last(walking).feet().along() > 20, "and across its deck, asked ahead");
        assertEquals(500, last(walking).going().across(), 1e-6);
    }

    @Test
    void aMelonRolledAtItStopsAgainstItAndItIsNotMoved() {
        final State things = open().stood(new Rigid("melon", new Ellipsoid(36, 28, 26), 960),
                new Placed(new Spot(0, 14, -300)));
        Whole whole = world(things, nowhere);
        for (int tick = 0; tick < 1_200; tick++) {
            final Asked asked = tick == 0 ? new Asked(List.of(), Map.of("melon",
                    new Moved(new Spot(0, 0, 250), nowhere)), Map.of()) : new Asked();
            whole = dispatcher.ticked(whole, new Asking(asked, Map.of())).whole();
        }
        final double melon = whole.things().placed("melon").orElseThrow().at().along();
        assertTrue(melon < -44.9 && melon > -50.1, "against the walker's back and not in it: " + melon);
        assertTrue(whole.things().resting().containsKey("melon"), "come to rest there");
        assertEquals(0, whole.standings().get(SOMEONE).feet().along(), 0.5, "who has not been moved");
    }

    @Test
    void aMelonAtRestBarsItsWayThoughItIsLowerThanAStepAndAHalf() {
        final State things = open().stood(new Rigid("melon", new Ellipsoid(36, 28, 26), 960),
                new Placed(new Spot(0, 14, 200)));
        final List<Given> run = run(world(things, nowhere), 360, ahead);
        assertTrue(furthest(run) <= 200 - 13 - 32 + 0.03, "not over it or through it: " + furthest(run));
        assertTrue(run.getLast().whole().things().resting().containsKey("melon"), "and the melon is not moved");
    }

    @Test
    void theSameWholeAndTheSameThingsAskedGiveTheSameAndLeaveTheWholeAsItWas() {
        final Whole begun = world(open().stood(new Rigid("pillar", new Box(40, 200, 40), 2_400),
                new Placed(new Spot(0, 100, 300))), nowhere);
        final Whole kept = begun;
        final Asking asking = new Asking(new Asked(), Map.of(SOMEONE, ahead));
        assertEquals(dispatcher.ticked(begun, asking), dispatcher.ticked(begun, asking));
        assertEquals(kept, begun);
        assertEquals(run(begun, 300, ahead).getLast(), run(begun, 300, ahead).getLast());
    }
}
