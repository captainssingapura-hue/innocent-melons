package melons.innocent.core.walk;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Globe;
import melons.innocent.core.rest.Gravity;
import melons.innocent.core.rest.Landing;
import melons.innocent.core.step.Explained;
import melons.innocent.core.step.Explaining;
import melons.innocent.core.step.Said;
import melons.innocent.core.tick.Ticker;
import melons.innocent.physics.existences.Conditions;
import melons.innocent.physics.existences.Surfaces;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The walking engine as a function, with no world round it: a walker, what is under it, the tick and what it is
 * asked go in, and the walker a tick on comes out. On the level it comes to the pace asked and stops when let go;
 * it goes the way it faces; its feet rise with a slope; too steep a slope, and too high a ledge, refuse it; a low
 * ledge it steps up; off an edge it falls and comes down; it jumps as high as its world's pull lets it; on what
 * is going it is carried; on ice it is slow to start and to stop. Handed the same it gives the same, and the same
 * whether it explains or not.
 */
class WalksTest {

    private final Walks walks = new Walks();
    private final Walker walker = new Walker("someone");
    private final Ticker ticker = new Ticker();
    private final Spot nowhere = new Spot(0, 0, 0);
    private final Wish ahead = new Wish(1, 0, 0, false, false);

    /** A surface as high over each spot as {@code high} says, sloping by {@code grade} along, in a condition. */
    private Under surface(final ToDoubleFunction<Spot> high, final double grade, final Node condition,
                          final Spot going) {
        return (spot, reach) -> spot.up() >= high.applyAsDouble(spot) && spot.up() - high.applyAsDouble(spot) <= reach
                ? Optional.of(new Stood("the ground", high.applyAsDouble(spot), new Spot(0, 1, -grade).unit(), going,
                        grade == 0 ? new Surfaces.Level() : new Surfaces.Slope(), condition)) : Optional.empty();
    }

    private Under ground() {
        return surface(spot -> 0, 0, new Conditions.Dry(), nowhere);
    }

    /** A walker standing at {@code feet} on what is under it, facing {@code yaw}. */
    private Walking standing(final Under under, final Spot feet, final double yaw) {
        return new Walking(walker, new Standing(feet, yaw, nowhere, Optional.of("the ground"), 0), under,
                new Gravity());
    }

    /** {@code from} so many ticks on, asked the same at each; with all that was said on the way, where wanted. */
    private Walking after(final Walking from, final int ticks, final Wish wish, final List<Said> said) {
        Walking walking = from;
        for (int tick = 0; tick < ticks; tick++) {
            final Explained<Walker, Walking, Stride> stepped = walks.simulated(walking, ticker.at(tick),
                    List.of(wish));
            said.addAll(stepped.said());
            walking = stepped.state();
        }
        return walking;
    }

    private Walking after(final Walking from, final int ticks, final Wish wish) {
        return after(from, ticks, wish, new ArrayList<>());
    }

    @Test
    void setDownJustOverTheGroundItComesDownOnItAndStands() {
        final Walking set = new Walking(walker, new Standing(new Spot(0, 0, 0), 0), ground(), new Gravity());
        final Explained<Walker, Walking, Stride> stepped = walks.simulated(set, ticker.first(), List.of());
        assertTrue(stepped.state().standing().afoot());
        assertEquals(Optional.of("the ground"), stepped.state().standing().on());
        assertEquals(0, stepped.state().standing().feet().up(), 0);
        assertEquals(List.of(new Landing("the ground")), stepped.said());
        assertEquals(nowhere, after(stepped.state(), 120, new Wish()).standing().going(), "and stays, asked nothing");
    }

    @Test
    void askedAheadItComesToItsWalkingPaceAndLetGoItStops() {
        final Walking walking = after(standing(ground(), nowhere, 0), 120, ahead);
        assertEquals(320, walking.standing().going().along(), 0.1, "a second on, at its pace");
        assertEquals(0, walking.standing().going().across(), 0);
        assertEquals(walking.standing().feet().along(), walking.standing().stride(), 1e-9,
                "its stride is how far it has gone on its feet");
        assertTrue(walking.standing().feet().along() > 280 && walking.standing().feet().along() < 320);
        assertTrue(after(walking, 120, new Wish()).standing().going().length() < 0.1, "let go, it stops");
    }

    @Test
    void itRunsFasterBacksUpAtHalfItsPaceAndStepsAsideAtThreeQuarters() {
        final Walking set = standing(ground(), nowhere, 0);
        assertEquals(760, after(set, 240, new Wish(1, 0, 0, true, false)).standing().going().along(), 0.1);
        assertEquals(-160, after(set, 240, new Wish(-1, 0, 0, false, false)).standing().going().along(), 0.1);
        assertEquals(240, after(set, 240, new Wish(0, 1, 0, false, false)).standing().going().across(), 0.1,
                "to its right, which is across, facing ahead");
    }

    @Test
    void turnedItGoesTheWayItFaces() {
        final Walking turned = after(standing(ground(), nowhere, 0), 90, new Wish(0, 0, 1, false, false));
        assertEquals(90, turned.standing().yaw(), 1e-9, "a degree a tick for ninety ticks");
        final Walking walking = after(turned, 240, ahead);
        assertEquals(320, walking.standing().going().across(), 0.1, "ahead is now to the right of where it began");
        assertEquals(0, walking.standing().going().along(), 0.1);
    }

    @Test
    void onASlopeItsFeetRiseAndFallWithItAtEverySpot() {
        final Under slope = surface(spot -> 0.2 * spot.along(), 0.2, new Conditions.Dry(), nowhere);
        Walking walking = standing(slope, nowhere, 0);
        for (int tick = 0; tick < 240; tick++) {
            walking = walks.simulated(walking, ticker.at(tick), List.of(ahead)).state();
            assertEquals(0.2 * walking.standing().feet().along(), walking.standing().feet().up(), 1e-9);
        }
        assertTrue(walking.standing().feet().up() > 100, "up over a metre by then");
        final Walking back = after(walking, 240, new Wish(-1, 0, 0, false, false));
        assertEquals(0.2 * back.standing().feet().along(), back.standing().feet().up(), 1e-9, "and down again");
    }

    @Test
    void aSlopeTooSteepRefusesItGoingUpAndLetsItComeDown() {
        final Under steep = surface(spot -> Math.max(0, 1.5 * (spot.along() - 100)), 1.5, new Conditions.Dry(),
                nowhere);
        final Explaining told = new Explaining(true);
        Walking walking = standing(steep, nowhere, 0);
        Stride last = Stride.Unsaid.UNSAID;
        for (int tick = 0; tick < 240; tick++) {
            final Explained<Walker, Walking, Stride> stepped = new Walks(told).simulated(walking, ticker.at(tick),
                    List.of(ahead));
            walking = stepped.state();
            last = stepped.why();
        }
        assertTrue(walking.standing().feet().along() <= 100.01, "no further than its foot");
        assertEquals(new Stride.Refused("the ground", "too steep to climb"), last);
        final Walking down = after(standing(steep, new Spot(0, 75, 150), 180), 240, ahead);
        assertTrue(down.standing().feet().along() < 100, "from up on it, facing back, it comes down");
    }

    @Test
    void itStepsUpALowLedgeAndIsRefusedByOneTooHigh() {
        final Under low = surface(spot -> spot.along() > 100 ? 15 : 0, 0, new Conditions.Dry(), nowhere);
        final Walking up = after(standing(low, nowhere, 0), 240, ahead);
        assertEquals(15, up.standing().feet().up(), 0, "up onto it");
        assertTrue(up.standing().feet().along() > 300);
        final Under high = surface(spot -> spot.along() > 100 ? 40 : 0, 0, new Conditions.Dry(), nowhere);
        final Walking stopped = after(standing(high, nowhere, 0), 240, ahead);
        assertEquals(0, stopped.standing().feet().up(), 0);
        assertTrue(stopped.standing().feet().along() <= 100, "and stopped at the foot of one twice its step");
        assertTrue(stopped.standing().afoot());
    }

    @Test
    void walkingOffAnEdgeItFallsAndComesDownOnWhatIsBelow() {
        final Under table = surface(spot -> spot.along() < 200 ? 100 : 0, 0, new Conditions.Dry(), nowhere);
        final List<Said> said = new ArrayList<>();
        final Walking walking = after(standing(table, new Spot(0, 100, 0), 0), 360, ahead, said);
        assertEquals(0, walking.standing().feet().up(), 0, "down on the ground");
        assertTrue(walking.standing().afoot());
        assertEquals(List.of(new Landing("the ground")), said, "having come down once");
        assertTrue(walking.standing().feet().along() > 300, "and walked on");
    }

    @Test
    void itJumpsAsHighAsItsWorldsPullLetsItAndComesDownAgain() {
        for (final Globe globe : Globe.values()) {
            Walking walking = new Walking(walker, new Standing(nowhere, 0, nowhere, Optional.of("the ground"), 0),
                    ground(), new Gravity(globe));
            double highest = 0;
            final List<Said> said = new ArrayList<>();
            for (int tick = 0; tick < 600; tick++) {
                final Explained<Walker, Walking, Stride> stepped = walks.simulated(walking, ticker.at(tick),
                        List.of(new Wish(0, 0, 0, false, tick == 0)));
                walking = stepped.state();
                said.addAll(stepped.said());
                highest = Math.max(highest, walking.standing().feet().up());
            }
            assertEquals(300.0 * 300 / (2 * globe.pull()), highest, 0.5, globe + ": its speed squared over twice");
            assertTrue(walking.standing().afoot(), globe + ": down again");
            assertEquals(List.of(new Landing("the ground")), said);
        }
    }

    @Test
    void onWhatIsGoingItIsCarriedAndWalksAgainstThat() {
        final Spot wagon = new Spot(500, 0, 0);
        final Under deck = surface(spot -> 100, 0, new Conditions.Dry(), wagon);
        final Walking riding = new Walking(walker, new Standing(new Spot(0, 100, 0), 0, wagon,
                Optional.of("the ground"), 0), deck, new Gravity());
        final Walking carried = after(riding, 120, new Wish());
        assertEquals(500, carried.standing().feet().across(), 1e-6, "a second on, five metres along with it");
        assertEquals(0, carried.standing().stride(), 0, "having taken no step");
        final Walking walking = after(riding, 240, ahead);
        assertEquals(0, walking.standing().going().minus(new Spot(500, 0, 320)).length(), 0.1,
                "its own pace across the deck, on top of the wagon's");
    }

    @Test
    void onIceItIsSlowToGatherSpeedAndSlowToStop() {
        final Under ice = surface(spot -> 0, 0, new Conditions.Icy(), nowhere);
        final double dry = after(standing(ground(), nowhere, 0), 30, ahead).standing().going().along();
        final double icy = after(standing(ice, nowhere, 0), 30, ahead).standing().going().along();
        assertTrue(icy < dry / 3, "a quarter of a second on: " + icy + " against " + dry);
        final Walking sliding = after(after(standing(ice, nowhere, 0), 600, ahead), 60, new Wish());
        final Walking stopped = after(after(standing(ground(), nowhere, 0), 600, ahead), 60, new Wish());
        assertTrue(sliding.standing().going().along() > 100 && stopped.standing().going().along() < 3,
                "half a second after letting go");
    }

    @Test
    void whatItGivesIsTheSameHandedTheSameAndWhetherItExplainsOrNot() {
        final Walking set = standing(surface(spot -> 0.2 * spot.along(), 0.2, new Conditions.Wet(), nowhere),
                nowhere, 30);
        final Wish wish = new Wish(1, -0.5, 2, true, false);
        final Explained<Walker, Walking, Stride> silent = walks.simulated(set, ticker.first(), List.of(wish));
        assertEquals(silent, walks.simulated(set, ticker.at(9_000), List.of(wish)), "whichever tick it is");
        assertSame(Stride.Unsaid.UNSAID, silent.why());
        final Explained<Walker, Walking, Stride> told = new Walks(new Explaining(true)).simulated(set,
                ticker.first(), List.of(wish));
        assertEquals(silent.state(), told.state());
        final Stride.Afoot afoot = assertInstanceOf(Stride.Afoot.class, told.why());
        assertEquals(0.2, afoot.grade(), 1e-12);
        assertEquals(wish.wanted(walker, 32), afoot.asked(), "asked against the way it faces once turned");
        assertFalse(afoot.came().length() >= afoot.asked().length(), "and not yet come to it");
    }

    @Test
    void ofSeveralThingsAskedAtOneTickTheLastStands() {
        final Walking set = standing(ground(), nowhere, 0);
        assertEquals(walks.simulated(set, ticker.first(), List.of(ahead)),
                walks.simulated(set, ticker.first(), List.of(new Wish(-1, 1, 5, true, false), ahead)));
    }
}
