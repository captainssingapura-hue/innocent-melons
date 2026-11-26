package melons.innocent.core.tick;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The calendar of a world. The moment of a tick is worked out from its count: at the ticks' own pace an hour of
 * ticks is an hour of the calendar; at another pace, so many times as much; at none, the hour stands still. Set
 * again at a tick, it says the moment it was set to at that tick and goes on from there at its pace.
 */
class CalendarTest {

    private final Ticker ticker = new Ticker();
    private final Instant midsummer = Instant.parse("2026-06-21T07:35:00Z");

    @Test
    void theFirstTickBeginsAtTheMomentTheCalendarBeganAt() {
        assertEquals(midsummer, new Calendar(midsummer).at(ticker.first()));
    }

    @Test
    void atTheTicksOwnPaceAnHourOfTicksIsAnHourOfTheCalendar() {
        final Calendar calendar = new Calendar(midsummer);
        assertEquals(midsummer.plus(Duration.ofHours(1)), calendar.at(ticker.at(120 * 3_600)));
        assertEquals(midsummer.plus(Duration.ofDays(30)), calendar.at(ticker.at(120L * 86_400 * 30)));
        assertEquals(midsummer.plusNanos(8_333_333), calendar.at(ticker.at(1)), "one tick, to the nanosecond");
    }

    @Test
    void aCalendarGoingAMinuteASecondIsSixtyTimesAsFarOn() {
        final Calendar calendar = new Calendar(midsummer, 60);
        assertEquals(midsummer.plus(Duration.ofMinutes(1)), calendar.at(ticker.at(120)));
        assertEquals(midsummer.plus(Duration.ofHours(24)), calendar.at(ticker.at(120 * 60 * 24)));
    }

    @Test
    void aCalendarOfNoPaceKeepsItsHourStandingStill() {
        assertEquals(midsummer, new Calendar(midsummer, 0).at(ticker.at(1_000_000)));
    }

    @Test
    void theSameTickIsTheSameMomentWhateverTheTickersRateWhereAsLongHasGoneBy() {
        final Calendar calendar = new Calendar(midsummer);
        assertEquals(calendar.at(ticker.at(1_200)), calendar.at(new Ticker(60).at(600)));
    }

    @Test
    void setAgainAtATickItSaysThatMomentThereAndGoesOnAtItsPace() {
        final Instant noon = Instant.parse("2026-06-21T12:00:00Z");
        final Calendar behind = new Calendar(midsummer, 60);
        final Calendar set = behind.set(ticker.at(7_200), noon);
        assertEquals(noon, set.at(ticker.at(7_200)));
        assertEquals(noon.plus(Duration.ofMinutes(1)), set.at(ticker.at(7_320)));
        assertEquals(60, set.pace(), 0);
    }
}
