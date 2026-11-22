package melons.innocent.math.curves;

import com.example.fxsuite.math.existences.Curves;

/**
 * A linear curve: from {@code from} to {@code to}, changing evenly between them.
 *
 * @param from where it starts, and its value there
 * @param to   where it ends, further along, and its value there
 */
public record Line(Knot from, Knot to) implements CurveImpl<Curves.Linear> {

    public Line {
        if (!(to.x() > from.x())) {
            throw new IllegalArgumentException("a line ending further along than it starts: " + from + " to " + to);
        }
    }

    @Override
    public Curves.Linear kind() {
        return new Curves.Linear();
    }

    @Override
    public double start() {
        return from.x();
    }

    @Override
    public double end() {
        return to.x();
    }

    @Override
    public double at(final double x) {
        return from.y() + (within(x) - from.x()) * slope(x);
    }

    @Override
    public double slope(final double x) {
        return (to.y() - from.y()) / (to.x() - from.x());
    }
}
