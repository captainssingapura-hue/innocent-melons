package melons.innocent.core.step;

import melons.innocent.core.tick.Tick;
import melons.innocent.core.tick.Ticker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The one shape of an engine, tried with the smallest engine there could be: a counter that goes up by what it is
 * asked each tick. Its state, its events and its explanation are typed to its kind of object; it is handed the
 * state, the tick and the events and gives the next state with its explanation; an engine that corrects nothing
 * gives its stepping back; and explaining is off unless a run says otherwise.
 */
class EngineTest {

    /** A counter: the kind of object. */
    private record Counter(String name) implements Simulated {
    }

    /** How high a counter stands. */
    private record Standing(Counter counter, long at) implements Situated<Counter> {
    }

    /** So much more asked of a counter. */
    private record More(long by) implements Event<Counter> {
    }

    /** What the counting engine worked out. */
    private record Added(long by, long atTick) implements Explanation<Counter> {
    }

    /** A counter passed a hundred. */
    private record Passed() implements Said {
    }

    /** The counting engine, which also corrects nothing. */
    private record Counting() implements Engine<Counter, Standing, Added>,
            Correcting<Counter, Standing, Added, String> {

        @Override
        public Explained<Counter, Standing, Added> simulated(final Standing before, final Tick tick,
                                                             final List<? extends Event<Counter>> events) {
            final long by = events.stream().mapToLong(event -> ((More) event).by()).sum();
            final Standing next = new Standing(before.counter(), before.at() + by);
            return new Explained<>(next, new Added(by, tick.count()),
                    before.at() < 100 && next.at() >= 100 ? List.of(new Passed()) : List.of());
        }
    }

    private final Counting counting = new Counting();
    private final Standing begun = new Standing(new Counter("one"), 90);
    private final Tick seventh = new Ticker().at(7);

    @Test
    void anEngineIsHandedTheStateTheTickAndTheEventsAndGivesTheNextStateWithItsExplanation() {
        final Explained<Counter, Standing, Added> stepped = counting.simulated(begun, seventh,
                List.of(new More(4), new More(3)));
        assertEquals(new Standing(new Counter("one"), 97), stepped.state());
        assertEquals(new Added(7, 7), stepped.why(), "in the engine's own type, and told which tick it was");
        assertEquals(List.of(), stepped.said());
        assertEquals(90, begun.at(), "the state it was handed is as it was");
    }

    @Test
    void handedTheSameItGivesTheSame() {
        assertEquals(counting.simulated(begun, seventh, List.of(new More(5))),
                counting.simulated(begun, seventh, List.of(new More(5))));
    }

    @Test
    void whatItSaysHappenedComesBackWithTheStateAndCannotBeChangedAfter() {
        final Explained<Counter, Standing, Added> stepped = counting.simulated(begun, seventh,
                List.of(new More(10)));
        assertEquals(List.of(new Passed()), stepped.said());
        assertThrows(UnsupportedOperationException.class, () -> stepped.said().clear());
        final List<Said> said = new ArrayList<>(List.of(new Passed()));
        final Explained<Counter, Standing, Added> kept = new Explained<>(begun, new Added(0, 0), said);
        said.clear();
        assertEquals(1, kept.said().size(), "copied when it was made");
    }

    @Test
    void anEngineThatCorrectsNothingGivesItsSteppingBackAsItWas() {
        final Explained<Counter, Standing, Added> stepped = counting.simulated(begun, seventh, List.of());
        assertSame(stepped, counting.correctionS1(begun, seventh, stepped, List.of("something found")));
    }

    @Test
    void explainingIsOffUnlessARunSaysOtherwise() {
        assertFalse(new Explaining().on());
        assertTrue(new Explaining(true).on());
    }
}
