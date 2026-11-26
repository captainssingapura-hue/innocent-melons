package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Core;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Next;
import melons.innocent.core.rest.Outsider;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.State;
import melons.innocent.core.tick.Ticker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A thing with corners finds its path barred by what stands in it, whatever that is: thrown at a pillar it stops
 * against the pillar's side and comes down beside it; thrown at someone standing it stops against them; thrown at
 * a melon at rest it strikes it, and the melon is set rolling. With nothing in its way it flies on as far as the
 * throw carries it. Each is the core's one function, a state and what is asked giving the next state.
 */
class BarredTest {

    private final Core<Why> core = new Core<>(new Loose());
    private final Rigid crate = new Rigid("crate", new Box(50, 50, 50), 600);
    /** The crate thrown ahead at six metres a second and upward at three; and low, at four and at one. */
    private final Moved high = new Moved(new Spot(0, 300, 600), new Spot(0, 0, 0));
    private final Moved low = new Moved(new Spot(0, 100, 400), new Spot(0, 0, 0));
    private final State open = new State(new Ground(), new Ticker()).stood(crate, new Placed(new Spot(0, 25, 0)));

    /** The states of so many ticks from {@code from}, the crate thrown so at the first, and all that happened. */
    private List<Next> run(final State from, final int ticks, final Map<String, Outsider> outsiders,
                           final Moved thrown) {
        final List<Next> all = new ArrayList<>();
        State state = from;
        for (int tick = 0; tick < ticks; tick++) {
            final Asked asked = new Asked(List.of(), List.of(), tick == 0 ? Map.of("crate", thrown) : Map.of(), Map.of(),
                    outsiders);
            all.add(core.ticked(state, asked));
            state = all.getLast().state();
        }
        return all;
    }

    private double furthest(final List<Next> run) {
        return run.stream().mapToDouble(next -> next.state().placed("crate").orElseThrow().at().along()).max()
                .orElseThrow();
    }

    @Test
    void withNothingInItsWayItFliesAsFarAsTheThrowCarriesIt() {
        final List<Next> run = run(open, 240, Map.of(), high);
        final State last = run.getLast().state();
        assertTrue(last.resting().containsKey("crate"));
        assertEquals(600 * 2 * 300 / 981.0, last.placed("crate").orElseThrow().at().along(), 6,
                "where a throw of that speed and rise comes down, without air");
    }

    @Test
    void thrownAtAPillarItStopsAgainstItsSideAndComesDownBesideIt() {
        final List<Next> run = run(open.stood(new Rigid("pillar", new Box(40, 200, 40), 2_400),
                new Placed(new Spot(0, 100, 300))), 240, Map.of(), high);
        // the pillar's near side is at 280, and the crate reaches 25 before its middle
        assertTrue(furthest(run) <= 255.03, "never into the pillar: " + furthest(run));
        final State last = run.getLast().state();
        assertTrue(last.resting().containsKey("crate"), "come to rest");
        assertEquals(255, last.placed("crate").orElseThrow().at().along(), 0.1, "against the pillar's side");
        assertEquals(25, last.placed("crate").orElseThrow().at().up(), 0.02, "on the ground");
        assertTrue(last.under("crate").contains(new Ground().name()));
        assertEquals(new Placed(new Spot(0, 100, 300)), last.placed("pillar").orElseThrow(), "which has not stirred");
    }

    @Test
    void thrownAtSomeoneStandingItStopsAgainstThem() {
        final Outsider someone = new Outsider(new Box(64, 200, 64), new Placed(new Spot(0, 100, 300)),
                new Spot(0, 0, 0));
        final List<Next> run = run(open, 240, Map.of("someone", someone), high);
        assertTrue(furthest(run) <= 300 - 32 - 25 + 0.03, "never into them: " + furthest(run));
        assertTrue(run.getLast().state().resting().containsKey("crate"));
    }

    @Test
    void thrownAtAMelonAtRestItStrikesItAndTheMelonIsSetRolling() {
        final List<Next> run = run(open.stood(new Rigid("melon", new Ellipsoid(36, 28, 26), 960),
                new Placed(new Spot(0, 14, 110))), 1_200, Map.of(), low);
        assertTrue(run.stream().flatMap(next -> next.stamped().happened().stream())
                .anyMatch(new Happened.Struck("melon", "crate")::equals), "the melon was struck by the crate");
        final State last = run.getLast().state();
        final double melon = last.placed("melon").orElseThrow().at().along();
        final double came = last.placed("crate").orElseThrow().at().along();
        assertTrue(melon > 400, "and rolled on ahead: " + melon);
        assertTrue(came < melon - 25 - 12.9, "the crate behind it, not through it: " + came + " and " + melon);
        assertTrue(last.resting().containsKey("crate") && last.resting().containsKey("melon"), "both at rest");
    }
}
