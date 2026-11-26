package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Asked;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Core;
import melons.innocent.core.rest.Ellipsoid;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Moved;
import melons.innocent.core.rest.Next;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.State;
import melons.innocent.core.rest.World;
import melons.innocent.core.step.Explaining;
import melons.innocent.core.tick.Ticker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The core's one function: a state and what is asked give the state a tick on. A state is a value: a tick leaves
 * the state it was given as it was, the same state and the same things asked give the same state after, a state
 * kept can be gone on from later and comes to the very same, and nothing of a state can be changed from outside.
 * And the state after is the very same whether the engine explains or not; only the explanations differ.
 */
class CoreTest {

    private final Core<Why> core = new Core<>(new Loose());
    /** A crate in the air over a pillar, a melon set rolling beside them, and a wagon going. */
    private final State begun = new State(new Ground(), new Ticker())
            .stood(new Rigid("pillar", new Box(40, 100, 40), 2_400), new Placed(new Spot(0, 50, 0)))
            .stood(new Rigid("crate", new Box(50, 50, 50), 600), new Placed(new Spot(0, 180, 0)))
            .stood(new Rigid("melon", new Ellipsoid(36, 28, 26), 960), new Placed(new Spot(300, 14, 0)))
            .driven(new Rigid("wagon", new Box(400, 100, 200), 2_400), new Placed(new Spot(-900, 50, 600)),
                    new Moved(new Spot(500, 0, 0), new Spot(0, 0, 0)));
    private final Asked rolled = new Asked(List.of(), Map.of("melon",
            new Moved(new Spot(0, 0, 250), new Spot(0, 0, 0))), Map.of());

    /** The state after so many ticks from {@code from}, the melon set rolling at the first of them. */
    private State after(final Core<Why> by, final State from, final int ticks) {
        State state = by.ticked(from, from.tick().count() == 0 ? rolled : new Asked()).state();
        for (int tick = 1; tick < ticks; tick++) {
            state = by.ticked(state, new Asked()).state();
        }
        return state;
    }

    @Test
    void aTickGivesTheStateATickOnAndLeavesTheOneItWasGivenAsItWas() {
        final State kept = begun;
        final Next next = core.ticked(begun, rolled);
        assertEquals(kept, begun, "the state before is what it was");
        assertEquals(0, begun.tick().count());
        assertEquals(1, next.state().tick().count());
        assertEquals(begun.tick(), next.stamped().tick(), "what happened is stamped with the tick taken");
        assertNotEquals(begun, next.state());
        assertTrue(begun.resting().containsKey("crate") && next.state().moving().containsKey("crate"),
                "the crate, over nothing, at rest as described and in motion a tick on");
    }

    @Test
    void theSameStateAndTheSameThingsAskedGiveTheSameStateAfterEveryTime() {
        assertEquals(core.ticked(begun, rolled), core.ticked(begun, rolled));
        assertEquals(after(core, begun, 400), after(core, begun, 400));
    }

    @Test
    void aStateKeptCanBeGoneOnFromLaterAndComesToTheVerySame() {
        final State kept = after(core, begun, 150);
        final State straight = after(core, begun, 400);
        assertEquals(straight, after(core, kept, 250), "from the kept state, the rest of the ticks");
        final World resumed = new World(core, kept);
        for (int tick = 0; tick < 250; tick++) {
            resumed.ticked(new Asked());
        }
        assertEquals(straight, resumed.state(), "and a world taken up from it");
        assertEquals(400, resumed.tick().count());
    }

    @Test
    void theStateAfterIsTheVerySameWhetherTheEngineExplainsOrNot() {
        final Core<Why> explaining = new Core<>(new Loose(new Explaining(true)));
        State silent = begun;
        State told = begun;
        for (int tick = 0; tick < 400; tick++) {
            final Asked asked = tick == 0 ? rolled : new Asked();
            final Next one = core.ticked(silent, asked);
            final Next other = explaining.ticked(told, asked);
            assertEquals(one.state(), other.state(), "at tick " + tick);
            assertEquals(one.stamped().happened(), other.stamped().happened());
            assertTrue(one.stamped().why().values().stream().allMatch(Why.Unsaid.UNSAID::equals));
            assertTrue(other.stamped().why().values().stream().noneMatch(Why.Unsaid.UNSAID::equals));
            assertEquals(one.state().moving().keySet(), one.stamped().why().keySet().stream()
                    .filter(one.state().moving()::containsKey).collect(Collectors.toSet()));
            silent = one.state();
            told = other.state();
        }
        assertTrue(silent.resting().containsKey("crate"), "by then the crate has come down on the pillar");
        assertEquals(Set.of("pillar"), silent.under("crate"));
    }

    @Test
    void nothingOfAStateCanBeChangedFromOutside() {
        assertThrows(UnsupportedOperationException.class, () -> begun.things().remove("crate"));
        assertThrows(UnsupportedOperationException.class,
                () -> begun.resting().put("crate", new Placed(new Spot(0, 0, 0))));
        assertThrows(UnsupportedOperationException.class, () -> begun.dirty().clear());
        final State stood = after(core, begun, 400);
        assertThrows(UnsupportedOperationException.class, () -> stood.on().get("crate").clear());
    }
}
