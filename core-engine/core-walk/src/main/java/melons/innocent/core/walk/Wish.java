package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.step.Event;

/**
 * What a walker is asked at a tick, as one value: the same whoever asks, keys, a script, a kept run or another
 * player. Keys are turned into it outside the core; no engine ever sees a key.
 *
 * @param ahead how much ahead, from all the way back at -1 to all the way ahead at 1
 * @param aside how much aside, from all the way left at -1 to all the way right at 1
 * @param turn  how far to turn in this tick, in degrees, toward the right
 * @param run   whether it is to run
 * @param jump  whether it jumps
 */
public record Wish(double ahead, double aside, double turn, boolean run, boolean jump) implements Event<Walker> {

    /** How much slower backing up is than going ahead, and stepping aside. */
    private static final double BACK = 0.5;
    private static final double ASIDE = 0.75;

    /** Nothing asked. */
    public Wish() {
        this(0, 0, 0, false, false);
    }

    /** Whether it asks to go anywhere. */
    public boolean going() {
        return ahead != 0 || aside != 0;
    }

    /**
     * How {@code walker}, facing {@code yaw} degrees, is asked to go over what it stands on: ahead at its pace,
     * backing up at half of it, aside at three quarters.
     */
    public Spot wanted(final Walker walker, final double yaw) {
        final double top = run ? walker.runs() : walker.walks();
        final double way = Math.toRadians(yaw);
        final Spot forward = new Spot(Math.sin(way), 0, Math.cos(way));
        final Spot right = new Spot(Math.cos(way), 0, -Math.sin(way));
        return forward.times(ahead * top * (ahead < 0 ? BACK : 1)).plus(right.times(aside * top * ASIDE));
    }
}
