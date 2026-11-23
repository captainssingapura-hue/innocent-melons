package melons.innocent.terrain.bench;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * What the route bench's console asks of it, said on JavaFX's thread and read on the card's: the route, and whether
 * the camera is going along it; how fast; how far ahead in time tiles are built, and whether ahead means along the
 * route or straight on the way the camera goes; how far over the ground it keeps, if it keeps clear of it at all; the
 * camera back at the route's start; and an end.
 */
public final class Steering {

    private final AtomicReference<Route> route = new AtomicReference<>();
    private final AtomicBoolean going = new AtomicBoolean();
    private final AtomicReference<Double> speed;
    private final AtomicReference<Double> ahead;
    private final AtomicBoolean along = new AtomicBoolean(true);
    private final AtomicReference<Double> clearance;
    private final AtomicBoolean back = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();

    /** At first {@code speed} centimetres a second, {@code ahead} seconds ahead, {@code clearance} clear. */
    public Steering(final double speed, final double ahead, final double clearance) {
        this.speed = new AtomicReference<>(speed);
        this.ahead = new AtomicReference<>(ahead);
        this.clearance = new AtomicReference<>(clearance);
    }

    /** The route, or none while it has fewer than two waypoints. */
    public void route(final Optional<Route> wanted) {
        route.set(wanted.orElse(null));
    }

    /** The route as wanted now, if there is one. */
    public Optional<Route> route() {
        return Optional.ofNullable(route.get());
    }

    /** The camera going along the route, or held where it is. */
    public void going(final boolean wanted) {
        going.set(wanted);
    }

    /** Whether the camera is wanted going along the route. */
    public boolean going() {
        return going.get();
    }

    /** How fast the camera goes, in centimetres a second. */
    public void speed(final double wanted) {
        speed.set(wanted);
    }

    /** How fast the camera is wanted to go. */
    public double speed() {
        return speed.get();
    }

    /** How many seconds ahead of the camera tiles are built. */
    public void ahead(final double seconds) {
        ahead.set(seconds);
    }

    /** How many seconds ahead tiles are wanted built. */
    public double ahead() {
        return ahead.get();
    }

    /** Whether ahead means along the route, or else straight on the way the camera goes now. */
    public void along(final boolean wanted) {
        along.set(wanted);
    }

    /** Whether ahead is wanted along the route. */
    public boolean along() {
        return along.get();
    }

    /** How far over the ground the camera keeps, in centimetres; not a number for not at all. */
    public void clearance(final double wanted) {
        clearance.set(wanted);
    }

    /** How far over the ground the camera is wanted to keep; not a number for not at all. */
    public double clearance() {
        return clearance.get();
    }

    /** The camera back at the route's start, wanted. */
    public void back() {
        back.set(true);
    }

    /** Whether the camera has been wanted back at the start since this was last asked. */
    public boolean backWanted() {
        return back.getAndSet(false);
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
