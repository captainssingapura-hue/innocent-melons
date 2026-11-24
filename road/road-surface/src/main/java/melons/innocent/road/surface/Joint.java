package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Point;

/**
 * Where one course meets another: {@code at}, heading {@code heading} there. A course placed so its end, or its
 * start, is at it heading that way, meets there, without a gap, whatever is laid along the other.
 *
 * @param at      where they meet, as high as both are there
 * @param heading which way both head there
 */
public record Joint(Point at, double heading) {

    /** Where {@code course} is {@code s} along it, and which way it heads there. */
    public Joint(final Course course, final double s) {
        this(course.at(s), course.heading(s));
    }

    /** {@code course} placed so it is here {@code s} along it, heading this way. */
    public <T extends Course> Placed<T> placing(final T course, final double s) {
        final Turn turn = new Turn(heading - course.heading(s));
        final Point p = turn.of(course.at(s));
        return new Placed<>(course, turn.by(), at.across() - p.across(), at.along() - p.along());
    }
}
