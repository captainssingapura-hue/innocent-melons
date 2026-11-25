package melons.innocent.core.interaction;

import com.example.fxsuite.existence.Things;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The menu of what else can be done, the action that leads into a state, and power gathering as a trigger is held. */
class MenuTest {

    private final Bindings bindings = new Handled().bindings();

    @Test
    void theMenuListsWhatIsOfferedBesidesThePlainAskingAndItsMarkGoesRound() {
        final Menu held = new Menu(bindings.offers(new Things.Watermelon(), Relation.HELD)
                .others(action -> Optional.empty()));
        assertEquals(List.of("Crush", "Throw"), held.offers().stream().map(Offer::label).toList());
        assertEquals(Action.CRUSH, held.chosen().orElseThrow().action());
        assertEquals(Action.THROW, held.moved(1).chosen().orElseThrow().action());
        assertEquals(Action.CRUSH, held.moved(1).moved(1).chosen().orElseThrow().action(), "round to the first");
        assertEquals(Action.THROW, held.moved(-1).chosen().orElseThrow().action(), "and back to the last");
        assertEquals(held, held.moved(0));
        final Menu near = new Menu(bindings.offers(new Things.Watermelon(), Relation.NEAR)
                .others(action -> Optional.empty()));
        assertEquals(List.of("Cut", "Crush"), near.offers().stream().map(Offer::label).toList());
    }

    @Test
    void theMarkNeverRestsOnWhatCannotBeDoneAndAMenuMayListNothing() {
        final Strength strength = new Strength();
        final Menu heavy = new Menu(bindings.offers(new Things.Watermelon(), Relation.HELD)
                .others(action -> strength.barred(action, 30)));
        assertEquals(List.of("Crush", "Throw (too heavy at 30 kg)"), heavy.offers().stream().map(Offer::said).toList());
        assertEquals(Action.CRUSH, heavy.moved(1).chosen().orElseThrow().action(), "past the one that is barred");
        final Menu none = new Menu(bindings.offers(new Things.Crate(), Relation.NEAR)
                .others(action -> Optional.empty()));
        assertTrue(none.empty());
        assertTrue(none.chosen().isEmpty());
        assertEquals(none, none.moved(1));
        final Menu barred = new Menu(List.of(new Offer(Action.THROW, "Throw", false, Optional.of("too heavy"))));
        assertTrue(barred.chosen().isEmpty());
        assertEquals(barred, barred.moved(1));
    }

    @Test
    void throwingLeadsIntoAStateAndEveryOtherActionIsDoneAtOnce() {
        assertEquals(Optional.of(Mode.THROWING), Action.THROW.leads());
        assertEquals("Throwing", Mode.THROWING.label());
        for (final Action action : Action.values()) {
            assertEquals(action == Action.THROW, action.leads().isPresent(), action.id());
        }
    }

    @Test
    void powerGathersEvenlyToAllThereIsAndNoFurther() {
        final Gathering gathering = new Gathering();
        assertEquals(0, gathering.share(0), 0);
        assertEquals(0.5, gathering.share(0.75), 1e-12);
        assertEquals(1, gathering.share(1.5), 0);
        assertEquals(1, gathering.share(10), 0);
        assertEquals(0.25, new Gathering(2).share(0.5), 1e-12);
    }
}
