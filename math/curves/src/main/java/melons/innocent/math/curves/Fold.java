package melons.innocent.math.curves;

import com.example.fxsuite.math.existences.Curves;

/**
 * A folded curve: through its knots, straight from each to the next, turning at each. A roof's slope folded at its
 * purlins; a bed falling in reaches.
 *
 * @param knots what it passes through, in order
 */
public record Fold(Knots knots) implements CurveImpl<Curves.Folded> {

    @Override
    public Curves.Folded kind() {
        return new Curves.Folded();
    }

    @Override
    public double start() {
        return knots.start();
    }

    @Override
    public double end() {
        return knots.end();
    }

    @Override
    public double at(final double x) {
        final int i = knots.piece(within(x));
        return knots.get(i).y() + (x - knots.get(i).x()) * knots.rise(i);
    }

    @Override
    public double slope(final double x) {
        return knots.rise(knots.piece(within(x)));
    }
}
