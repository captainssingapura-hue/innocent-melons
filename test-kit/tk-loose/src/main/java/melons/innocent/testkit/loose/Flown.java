package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.World;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A throw, followed: what was thrown, from where, how fast, how far up from level and which way round; how it was
 * already going when it was thrown, carried by a wagon; and, as the trial is ticked, how high it has risen and where
 * it first came down. It says those beside what the reckoning without air gives for a throw over level ground: as
 * high as its speed upward squared over twice gravity's pull, and as far the way it was thrown as its speed squared
 * times the sine of twice its angle over gravity's pull. How far aside of that way it came down is what it was
 * carried while it flew: nothing, thrown from rest.
 */
public final class Flown {

    private static final double METRE = 100;

    private final String thing;
    private final Spot from;
    private final double speed;
    private final double upward;
    private final double round;
    private final Spot carrying;
    private final double pull;
    private double highest;
    private Optional<Spot> down = Optional.empty();

    /**
     * {@code thing} thrown from {@code from} at so many centimetres a second, so many degrees {@code upward} and
     * {@code round} from ahead, while it was going as {@code carrying} says, in a world that pulls down so
     * many centimetres a second each second.
     */
    public Flown(final String thing, final Spot from, final double speed, final double upward, final double round,
                 final Spot carrying, final double pull) {
        this.thing = thing;
        this.from = from;
        this.speed = speed;
        this.upward = upward;
        this.round = round;
        this.carrying = carrying;
        this.pull = pull;
        this.highest = from.up();
    }

    /** A tick of the {@code world} it is in, in which {@code happened}: its height taken, and its coming down. */
    public void followed(final World world, final List<Happened> happened) {
        if (down.isPresent() || world.placed(thing).isEmpty()) {
            return;
        }
        final Spot at = world.placed(thing).orElseThrow().at();
        highest = Math.max(highest, at.up());
        if (happened.stream().anyMatch(one -> one instanceof Happened.Landed && one.thing().equals(thing))) {
            down = Optional.of(at);
        }
    }

    /** How high its middle has risen over where it was thrown from, in centimetres. */
    public double rose() {
        return highest - from.up();
    }

    /** How far the way it was thrown it first came down, in centimetres; none, while it is still in the air. */
    public Optional<Double> carried() {
        return down.map(at -> (at.across() - from.across()) * Math.sin(Math.toRadians(round))
                + (at.along() - from.along()) * Math.cos(Math.toRadians(round)));
    }

    /** How far aside of the way it was thrown it came down, in centimetres: what it was carried as it flew. */
    public Optional<Double> aside() {
        return down.map(at -> Math.abs((at.across() - from.across()) * Math.cos(Math.toRadians(round))
                - (at.along() - from.along()) * Math.sin(Math.toRadians(round))));
    }

    /** How high the reckoning without air says it rises, in centimetres. */
    public double reckonedRise() {
        final double rising = speed * Math.sin(Math.toRadians(upward));
        return upward > 0 ? rising * rising / (2 * pull) : 0;
    }

    /** How far the reckoning without air says it carries over level ground, in centimetres. */
    public double reckonedCarry() {
        return upward > 0 ? speed * speed * Math.sin(Math.toRadians(2 * upward)) / pull : 0;
    }

    /** What it has to say of itself, in metres. */
    public String said() {
        final String aside = carrying.length() == 0 ? "" : String.format(Locale.ENGLISH,
                " and %.2f m aside, carried on at %.1f m/s", aside().orElse(0.0) / METRE, carrying.length() / METRE);
        final String came = carried().map(far -> String.format(Locale.ENGLISH,
                "came down %.2f m the way it was thrown (%.2f reckoned on the level)%s", far / METRE,
                reckonedCarry() / METRE, aside)).orElse("still in the air");
        return String.format(Locale.ENGLISH,
                "Thrown: %s at %.1f m/s, %.0f degrees up; rose %.2f m (%.2f reckoned); %s", thing, speed / METRE,
                upward, rose() / METRE, reckonedRise() / METRE, came);
    }
}
