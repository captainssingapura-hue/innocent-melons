package melons.innocent.core.interaction;

import com.example.fxsuite.existence.Things;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** What is bound to a kind of thing, worked out down its lineage, and what is offered of it near and held. */
class BindingsTest {

    private final Bindings bindings = new Handled().bindings();

    @Test
    void aKindWithNothingBoundHasWhatTheKindOverItHas() {
        assertEquals(List.of(Action.CARRY, Action.EAT), bindings.of(new Things.Fruit()));
        assertEquals(List.of(Action.CARRY, Action.EAT), bindings.of(new Things.Apple()));
        assertEquals(List.of(Action.CARRY, Action.EAT), bindings.of(new Things.Pineapple()));
        assertTrue(bindings.own(new Things.Apple()).isEmpty());
    }

    @Test
    void aMelonHasOnlyWhatIsBoundToItAndIsNotEatenAsItIs() {
        final List<Action> melon = List.of(Action.CARRY, Action.CUT, Action.CRUSH, Action.THROW);
        assertEquals(melon, bindings.of(new Things.Melon()));
        assertEquals(melon, bindings.of(new Things.Watermelon()));
        assertFalse(bindings.of(new Things.Watermelon()).contains(Action.EAT));
    }

    @Test
    void aSliceOfMelonIsEatenThoughAMelonIsNot() {
        assertEquals(List.of(Action.CARRY, Action.EAT), bindings.of(new Things.MelonSlice()));
    }

    @Test
    void aDoorIsOpenedAndAWagonHasNothingDoneWithIt() {
        assertEquals(List.of(Action.OPEN), bindings.of(new Things.Door()));
        assertEquals(List.of(), bindings.of(new Things.Wagon()));
        assertEquals(List.of(), bindings.of(new Things.Thing()));
    }

    @Test
    void anAddingComesOnTopOfWhatIsInheritedAndSaysNothingTwice() {
        final Bindings more = bindings.bound(new Things.Apple(), new Binding.Adding(Action.THROW, Action.EAT));
        assertEquals(List.of(Action.CARRY, Action.EAT, Action.THROW), more.of(new Things.Apple()));
        // the table it was made from is as it was
        assertEquals(List.of(Action.CARRY, Action.EAT), bindings.of(new Things.Apple()));
    }

    @Test
    void anOnlyUnderAnOnlyReplacesItAndAnAddingUnderItAddsToIt() {
        final Bindings more = bindings.bound(new Things.Watermelon(), new Binding.Adding(Action.EAT));
        assertEquals(List.of(Action.CARRY, Action.CUT, Action.CRUSH, Action.THROW, Action.EAT),
                more.of(new Things.Watermelon()));
        assertEquals(List.of(Action.CARRY, Action.EAT), more.of(new Things.MelonSlice()));
    }

    @Test
    void whatIsOfferedDependsOnHowOneStandsToTheThingAndTheFirstIsThePlainAsking() {
        final Offers near = bindings.offers(new Things.Watermelon(), Relation.NEAR);
        assertEquals(List.of("Carry", "Cut", "Crush"), near.offered().stream().map(Offer::label).toList());
        assertEquals(Action.CARRY, near.primary().orElseThrow().action());
        assertTrue(near.offered().getFirst().primary());
        assertFalse(near.offered().get(1).primary());
        assertFalse(near.offers(Action.THROW));
        final Offers held = bindings.offers(new Things.Watermelon(), Relation.HELD);
        assertEquals(List.of("Put down", "Crush", "Throw"), held.offered().stream().map(Offer::label).toList());
        assertTrue(held.offers(Action.THROW));
        assertTrue(bindings.offers(new Things.Watermelon(), Relation.INSIDE).offered().isEmpty());
        assertTrue(bindings.offers(new Things.Wagon(), Relation.NEAR).primary().isEmpty());
    }

    @Test
    void aThingTooHeavyIsOfferedAllTheSameWithWhyItCannotBeDone() {
        final Strength strength = new Strength();
        final List<Offer> crate = bindings.offers(new Things.Crate(), Relation.NEAR)
                .offered(action -> strength.barred(action, 130));
        assertEquals(List.of("Carry (too heavy at 130 kg)"), crate.stream().map(Offer::said).toList());
        assertEquals(Optional.empty(), strength.barred(Action.CARRY, 13));
        assertEquals(Optional.empty(), strength.barred(Action.THROW, 13));
        assertTrue(strength.barred(Action.THROW, 30).isPresent());
        assertEquals(Optional.empty(), strength.barred(Action.CARRY, 30));
        assertEquals(Optional.empty(), strength.barred(Action.CUT, 500));
    }

    @Test
    void everyActionHasANameOfItsOwnAndIsCalledSomethingToSomeone() {
        final List<String> ids = List.of(Action.values()).stream().map(Action::id).distinct().toList();
        assertEquals(Action.values().length, ids.size());
        for (final Action action : Action.values()) {
            assertTrue(action.offered(Relation.NEAR).isPresent() || action.offered(Relation.HELD).isPresent());
        }
    }
}
