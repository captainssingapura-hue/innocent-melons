package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Vector;

import java.util.List;

/**
 * A way along the map's lines: its spots in order, two at least, each joined to the next by a line of the map. It
 * has a way: what lies to its left and what to its right is told going from its first spot to its last.
 */
public final class Path implements Vector<Spot> {

    private final List<Spot> spots;

    /** Through {@code spots}, in order, none the same as the one before. */
    public Path(final List<Spot> spots) {
        this.spots = List.copyOf(spots);
        if (this.spots.size() < 2) {
            throw new IllegalArgumentException("a path through two spots at least, not " + this.spots.size());
        }
        for (int i = 1; i < this.spots.size(); i++) {
            if (this.spots.get(i).equals(this.spots.get(i - 1))) {
                throw new IllegalArgumentException("a path going on from " + this.spots.get(i) + ", not staying");
            }
        }
    }

    /** Its spots, in order. */
    public List<Spot> spots() {
        return spots;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Path path && spots.equals(path.spots);
    }

    @Override
    public int hashCode() {
        return spots.hashCode();
    }

    @Override
    public String toString() {
        return "Path" + spots;
    }
}
