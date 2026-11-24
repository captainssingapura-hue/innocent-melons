package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Point;

/**
 * A turn round nought, {@code by} radians from along toward across, as a heading turns: what is ahead along is then
 * ahead that much further round. How high a point is, it leaves as it is.
 *
 * @param by how far it turns
 */
public record Turn(double by) {

    /** {@code p} turned. */
    public Point of(final Point p) {
        return new Point(p.across() * Math.cos(by) + p.along() * Math.sin(by),
                -p.across() * Math.sin(by) + p.along() * Math.cos(by), p.up());
    }
}
