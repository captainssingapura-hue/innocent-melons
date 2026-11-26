package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Point;

/**
 * A {@code course} placed elsewhere: turned round its nought by {@code turn}, in radians from along toward across, and
 * moved {@code across} and {@code along} from there; as high as it was. So pieces worked out each about its own nought
 * are set end to end, one's end where the next begins, heading the same way ({@link Joint}).
 *
 * @param course what is placed
 * @param turn   how far it is turned
 * @param across how far it is moved across
 * @param along  how far it is moved along
 * @param <T>    what kind of course it is
 */
public record Placed<T extends Course>(T course, double turn, double across, double along) implements Course {

    public Placed {
        if (course == null) {
            throw new IllegalArgumentException("some course placed");
        }
    }

    /** {@code course} where it is. */
    public Placed(final T course) {
        this(course, 0, 0, 0);
    }

    @Override
    public double length() {
        return course.length();
    }

    @Override
    public Point at(final double s) {
        final Point p = new Turn(turn).of(course.at(s));
        return new Point(p.across() + across, p.along() + along, p.up());
    }

    @Override
    public double heading(final double s) {
        return course.heading(s) + turn;
    }

    @Override
    public double piece() {
        return course.piece();
    }
}
