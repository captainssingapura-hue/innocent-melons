package melons.innocent.terrain.bench;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * What the bench's controls ask of it, said on JavaFX's thread and read on the card's: the first view back, how fast
 * the eye flies, where over the map it is placed and how high, and an end. Each is read once: asked, it holds until
 * the card's thread takes it.
 */
public final class Wanted {

    private final AtomicBoolean first = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();
    private final AtomicReference<Double> speed = new AtomicReference<>(1.0);
    private final AtomicReference<double[]> spot = new AtomicReference<>();
    private final AtomicReference<Double> height = new AtomicReference<>();

    /** The view looked from first, wanted back. */
    public void first() {
        first.set(true);
    }

    /** Whether the first view has been wanted back since this was last asked. */
    public boolean firstWanted() {
        return first.getAndSet(false);
    }

    /** The eye flying {@code times} as fast as at first, wanted. */
    public void speed(final double times) {
        speed.set(times);
    }

    /** How many times as fast as at first the eye is wanted to fly. */
    public double speed() {
        return speed.get();
    }

    /** The eye wanted over ({@code x}, {@code z}) on the map, at its height, turned as it is. */
    public void placeAt(final double x, final double z) {
        spot.set(new double[] {x, z});
    }

    /** Where over the map the eye has been wanted since this was last asked, across and along, if anywhere. */
    public Optional<double[]> placedAt() {
        return Optional.ofNullable(spot.getAndSet(null));
    }

    /** The eye wanted {@code up} high, as drawn, where it is, turned as it is. */
    public void raise(final double up) {
        height.set(up);
    }

    /** How high the eye has been wanted since this was last asked, if at all. */
    public Optional<Double> raised() {
        return Optional.ofNullable(height.getAndSet(null));
    }

    /** An end, wanted. */
    public void stop() {
        stopped.set(true);
    }

    /** Whether an end has been wanted. */
    public boolean stopped() {
        return stopped.get();
    }
}
