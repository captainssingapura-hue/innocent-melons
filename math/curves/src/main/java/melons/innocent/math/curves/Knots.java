package melons.innocent.math.curves;

import hue.captains.singapura.tao.ontology.Vector;

import java.util.List;

/**
 * The points a curve is given, in order: two at least, each further along than the one before, so a curve through
 * them has one value for each x. Equal by them, in their order. The piece that holds an x is found by halving.
 */
public final class Knots implements Vector<Knot> {

    private final List<Knot> listed;

    /** {@code listed}, in order, each further along than the one before. */
    public Knots(final List<Knot> listed) {
        this.listed = List.copyOf(listed);
        if (this.listed.size() < 2) {
            throw new IllegalArgumentException("a curve through two points at least, not " + this.listed.size());
        }
        for (int i = 1; i < this.listed.size(); i++) {
            if (!(this.listed.get(i).x() > this.listed.get(i - 1).x())) {
                throw new IllegalArgumentException("each knot further along than the one before: " + this.listed);
            }
        }
    }

    /** Them, in order. */
    public List<Knot> listed() {
        return listed;
    }

    /** The {@code i}th, from 0. */
    public Knot get(final int i) {
        return listed.get(i);
    }

    /** How many there are. */
    public int size() {
        return listed.size();
    }

    /** The first one's x. */
    public double start() {
        return listed.getFirst().x();
    }

    /** The last one's x. */
    public double end() {
        return listed.getLast().x();
    }

    /** Which piece holds {@code x}: the i whose piece runs from knot i to knot i + 1, the last holding its end. */
    public int piece(final double x) {
        int low = 0;
        int high = listed.size() - 2;
        while (low < high) {
            final int middle = (low + high + 1) / 2;
            if (listed.get(middle).x() <= x) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        return low;
    }

    /** How steeply piece {@code i} rises from its first knot to its second. */
    public double rise(final int i) {
        return (listed.get(i + 1).y() - listed.get(i).y()) / (listed.get(i + 1).x() - listed.get(i).x());
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Knots knots && listed.equals(knots.listed);
    }

    @Override
    public int hashCode() {
        return listed.hashCode();
    }

    @Override
    public String toString() {
        return "Knots" + listed;
    }
}
