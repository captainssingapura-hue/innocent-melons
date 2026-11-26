package melons.innocent.core.tick;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The world's one ticker and its ticks. A ticker says how many ticks there are to a second, once; its first tick is
 * counted from nothing and each after is the next count. Every tick is as long as every other. How long the world
 * has gone on is the count over the rate, exact at any count, so a moment is named by a count alone.
 */
class TickerTest {

    private final Ticker ticker = new Ticker();

    @Test
    void aTickerGoesAsTheGameEngineStepsUnlessItIsSaidOtherwise() {
        assertEquals(120, ticker.rate());
        assertEquals(60, new Ticker(60).rate());
    }

    @Test
    void theFirstTickIsCountedFromNothingAndEachAfterIsTheNextCount() {
        final Tick first = ticker.first();
        assertEquals(new Tick(0, 120), first);
        assertEquals(new Tick(1, 120), first.next());
        assertEquals(new Tick(3, 120), first.next().next().next());
    }

    @Test
    void everyTickOfAWorldIsAsLongAsEveryOther() {
        assertEquals(1.0 / 120, ticker.first().seconds(), 0);
        assertEquals(1.0 / 120, ticker.at(1_000_000).seconds(), 0);
        assertEquals(1.0 / 60, new Ticker(60).first().seconds(), 0);
    }

    @Test
    void howLongTheWorldHasGoneOnIsItsCountOverItsRateExactlyHoweverLongThatIs() {
        assertEquals(0, ticker.first().elapsed(), 0);
        assertEquals(1, ticker.at(120).elapsed(), 0);
        assertEquals(3_600, ticker.at(432_000).elapsed(), 0, "an hour of ticks is an hour, to the last bit");
        assertEquals(86_400 * 365.0, ticker.at(120L * 86_400 * 365).elapsed(), 0, "and a year a year");
    }

    @Test
    void aMomentOfAWorldIsNamedByItsCountAloneHoweverTheWorldCameThere() {
        Tick counted = ticker.first();
        for (int taken = 0; taken < 500; taken++) {
            counted = counted.next();
        }
        assertEquals(ticker.at(500), counted);
        assertEquals(ticker.at(500).elapsed(), counted.elapsed(), 0);
    }

    @Test
    void soManySecondsAreSoManyTicksToTheNearestWholeOne() {
        assertEquals(180, ticker.ticks(1.5));
        assertEquals(0, ticker.ticks(0.004));
        assertEquals(1, ticker.ticks(0.005));
        assertEquals(90, new Ticker(60).ticks(1.5));
    }

    @Test
    void aTickerOrATickThatCouldNotBeIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new Ticker(0));
        assertThrows(IllegalArgumentException.class, () -> new Tick(-1, 120));
        assertThrows(IllegalArgumentException.class, () -> new Tick(0, 0));
    }
}
