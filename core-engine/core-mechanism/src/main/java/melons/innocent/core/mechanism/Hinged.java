package melons.innocent.core.mechanism;

/**
 * A thing that turns on a hinge between shut and as wide as it opens, as a door's leaf does. It moves only as it is
 * made to: each tick it is told whether it is wanted open or shut, and whether something is in its way. Wanted
 * elsewhere than it is, it gathers speed toward there, never turning faster than it may, and slows in time to stop
 * just where it is wanted; so it starts and stops gently, and wanted the other way while it is turning it slows,
 * stops and comes back. With something in its way it stops where it is and waits. Told the same it does the same.
 *
 * @param widest   how far it opens, in degrees from shut
 * @param quickest how fast it turns at most, in degrees a second
 * @param gathers  how quickly it gathers speed and loses it, in degrees a second each second
 */
public record Hinged(double widest, double quickest, double gathers) {

    /** How near where it is wanted it counts as there, in degrees. */
    private static final double THERE = 0.02;
    /** How much of the slowing it could do it plans on, so that it comes to its stop nearly still. */
    private static final double EASY = 0.7;

    public Hinged {
        if (!(widest > 0 && widest <= 180) || !(quickest > 0) || !(gathers > 0)) {
            throw new IllegalArgumentException("a hinge that opens some way, at some speed: " + widest + ", "
                    + quickest + ", " + gathers);
        }
    }

    /** A hinge that opens so wide, at a door's pace: a quarter turn in about a second. */
    public Hinged(final double widest) {
        this(widest, 120, 400);
    }

    /**
     * How it stands a tick of so many {@code seconds} on from {@code now}, wanted {@code open} or shut, and
     * {@code held} where something is in its way.
     */
    public Swung stepped(final Swung now, final boolean open, final boolean held, final double seconds) {
        if (held) {
            return new Swung(now.degrees(), 0);
        }
        final double wanted = open ? widest : 0;
        final double left = wanted - now.degrees();
        if (Math.abs(left) <= THERE && Math.abs(now.turning()) <= gathers * seconds) {
            return new Swung(wanted, 0);
        }
        // as fast as it may turn and still stop where it is wanted, slowing a little more gently than it could
        final double aimed = Math.signum(left) * Math.min(quickest, Math.sqrt(2 * EASY * gathers * Math.abs(left)));
        final double change = gathers * seconds;
        final double turning = now.turning() + Math.max(-change, Math.min(change, aimed - now.turning()));
        final double degrees = now.degrees() + turning * seconds;
        // gone as far as it goes either way, it is stopped there
        if (degrees <= 0 || degrees >= widest) {
            return new Swung(Math.max(0, Math.min(widest, degrees)), 0);
        }
        // come to where it is wanted, or just past it, it is there
        return (wanted - degrees) * left <= 0 ? new Swung(wanted, 0) : new Swung(degrees, turning);
    }

    /** How far open it is, of all the way: nothing shut, one as wide as it opens. */
    public double share(final Swung now) {
        return now.degrees() / widest;
    }
}
