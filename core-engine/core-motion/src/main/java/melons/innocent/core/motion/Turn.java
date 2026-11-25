package melons.innocent.core.motion;

/**
 * How a thing is turned from how it was made, or a turning from one way of standing to another: about any line
 * through its middle, not only about the upright. Four numbers kept one long together, as such turnings are kept so
 * that one after another stays a turning. A turn about a line is the way a screw goes in looking along the line.
 *
 * @param w how little it is turned: one, not at all
 * @param x its turning about the way across
 * @param y its turning about the way up
 * @param z its turning about the way along
 */
public record Turn(double w, double x, double y, double z) {

    public Turn {
        final double length = Math.sqrt(w * w + x * x + y * y + z * z);
        if (!(length > 0) || !Double.isFinite(length)) {
            throw new IllegalArgumentException("a turning of some kind: " + w + ", " + x + ", " + y + ", " + z);
        }
        w /= length;
        x /= length;
        y /= length;
        z /= length;
    }

    /** Not turned at all. */
    public Turn() {
        this(1, 0, 0, 0);
    }

    /** Turned about the line {@code about} lies along by so many {@code radians}. */
    public Turn(final Spot about, final double radians) {
        this(Math.cos(radians / 2), about.unit().across() * Math.sin(radians / 2),
                about.unit().up() * Math.sin(radians / 2), about.unit().along() * Math.sin(radians / 2));
    }

    /** {@code way} as it lies once turned so. */
    public Spot turned(final Spot way) {
        final Spot axis = new Spot(x, y, z);
        final Spot twice = axis.cross(way).times(2);
        return way.plus(twice.times(w)).plus(axis.cross(twice));
    }

    /** This turning and then {@code next}, as one. */
    public Turn then(final Turn next) {
        return new Turn(next.w * w - next.x * x - next.y * y - next.z * z,
                next.w * x + next.x * w + next.y * z - next.z * y,
                next.w * y - next.x * z + next.y * w + next.z * x,
                next.w * z + next.x * y - next.y * x + next.z * w);
    }

    /** The turning that undoes this one. */
    public Turn back() {
        return new Turn(w, -x, -y, -z);
    }
}
