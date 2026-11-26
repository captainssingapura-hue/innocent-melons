package melons.innocent.core.motion;

/**
 * How an observer is moving at a moment: where it is, its velocity, and how fast it is turning about the upright.
 * From these: how fast it goes, where it will be a little later if it goes on as it is, and how fast a thing
 * standing still somewhere sweeps across its view, which is what tells how much of the thing can be made out.
 *
 * <p>The sweep is the point of it. Going fast, what is beside the observer streaks past while what is straight
 * ahead hardly moves in the view at all, so speed alone says little about what need be drawn finely: the same speed
 * blurs a tree ten metres to one side and leaves one three hundred metres ahead as sharp as if the observer stood
 * still. Centimetres, seconds and degrees.
 *
 * @param at      where it is
 * @param going   its velocity: so far across, up and along each second
 * @param turning how fast it turns about the upright, in degrees a second, either way round
 */
public record Motion(Spot at, Spot going, double turning) {

    /** A kilometre an hour, in centimetres a second. */
    private static final double HOURLY = 100_000.0 / 3_600;
    /** Nearer than this a thing is taken to be this far off, in centimetres: at the eye itself nothing sweeps. */
    private static final double NEAREST = 1;

    /** An observer at {@code at}, standing still. */
    public Motion(final Spot at) {
        this(at, new Spot(0, 0, 0), 0);
    }

    /** How fast it goes, in centimetres a second. */
    public double speed() {
        return going.length();
    }

    /** How fast it goes, in kilometres an hour. */
    public double hourly() {
        return speed() / HOURLY;
    }

    /** Where it will be {@code seconds} from now, going on as it is. */
    public Spot ahead(final double seconds) {
        return at.plus(going.times(seconds));
    }

    /**
     * How fast a thing standing still at {@code thing} sweeps across its view, in degrees a second: by its going,
     * as much of its velocity as is square to the way to the thing, over how far off the thing is; and by its
     * turning, which sweeps everything alike.
     */
    public double sweeps(final Spot thing) {
        final Spot to = thing.minus(at);
        final double far = Math.max(NEAREST, to.length());
        return Math.toDegrees(going.cross(to).length() / (far * far)) + Math.abs(turning);
    }
}
