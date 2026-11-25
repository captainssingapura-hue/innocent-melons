package melons.innocent.core.mechanism;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A thing on a hinge, wanted open and shut, held, and turned back while it is turning. */
class HingedTest {

    private static final double TICK = 1.0 / 120;
    private final Hinged hinged = new Hinged(90);

    /** Stepped until it is still, or so many ticks have gone: how many it took. */
    private int settled(final Swung[] swung, final boolean open, final int most) {
        for (int tick = 1; tick <= most; tick++) {
            swung[0] = hinged.stepped(swung[0], open, false, TICK);
            if (!swung[0].moving()) {
                return tick;
            }
        }
        return most + 1;
    }

    @Test
    void wantedOpenItOpensAllTheWayInAboutASecondAndStopsThere() {
        final Swung[] swung = {new Swung()};
        final int ticks = settled(swung, true, 600);
        assertEquals(90, swung[0].degrees(), 0, "just as wide as it opens");
        assertTrue(ticks > 100 && ticks < 180, "in a second or so: " + ticks);
        assertEquals(swung[0], hinged.stepped(swung[0], true, false, TICK), "and there it stays");
        assertEquals(1, hinged.share(swung[0]), 0);
    }

    @Test
    void itGathersSpeedGentlyNeverTurnsFasterThanItMayAndSlowsBeforeItStops() {
        Swung swung = new Swung();
        double fastest = 0;
        double last = 0;
        for (int tick = 0; tick < 600; tick++) {
            final Swung next = hinged.stepped(swung, true, false, TICK);
            assertTrue(Math.abs(next.turning() - swung.turning()) <= 400 * TICK + 1e-9 || !next.moving(),
                    "no jolt at tick " + tick);
            assertTrue(next.degrees() >= swung.degrees() - 1e-12 && next.degrees() <= 90);
            fastest = Math.max(fastest, next.turning());
            last = next.moving() ? next.turning() : last;
            swung = next;
        }
        assertEquals(120, fastest, 1e-9, "as fast as it may, and no faster");
        assertTrue(last < 15, "nearly still as it comes to its stop: " + last);
    }

    @Test
    void wantedShutItComesBackAndHeldItWaitsWhereItIs() {
        final Swung[] swung = {new Swung()};
        settled(swung, true, 600);
        for (int tick = 0; tick < 40; tick++) {
            swung[0] = hinged.stepped(swung[0], false, false, TICK);
        }
        final double part = swung[0].degrees();
        assertTrue(part > 20 && part < 85 && swung[0].turning() < 0, "part shut, and shutting: " + part);
        final Swung held = hinged.stepped(swung[0], false, true, TICK);
        assertEquals(new Swung(part, 0), held, "something in its way, it stops where it is");
        assertEquals(held, hinged.stepped(held, false, true, TICK));
        swung[0] = held;
        settled(swung, false, 600);
        assertEquals(new Swung(), swung[0], "let go on, it shuts");
    }

    @Test
    void wantedTheOtherWayWhileItTurnsItSlowsStopsAndComesBack() {
        Swung swung = new Swung();
        for (int tick = 0; tick < 50; tick++) {
            swung = hinged.stepped(swung, true, false, TICK);
        }
        final double was = swung.degrees();
        assertTrue(swung.turning() > 100);
        swung = hinged.stepped(swung, false, false, TICK);
        assertTrue(swung.turning() > 0 && swung.degrees() > was, "it does not turn back all at once");
        final Swung[] going = {swung};
        settled(going, false, 600);
        assertEquals(new Swung(), going[0]);
        assertFalse(going[0].moving());
    }

    @Test
    void aHingeOpensSomeWayAtSomeSpeed() {
        assertThrows(IllegalArgumentException.class, () -> new Hinged(0));
        assertThrows(IllegalArgumentException.class, () -> new Hinged(200));
        assertThrows(IllegalArgumentException.class, () -> new Hinged(90, 0, 400));
    }
}
