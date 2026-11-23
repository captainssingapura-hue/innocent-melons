package melons.innocent.terrain;

import java.util.ArrayList;
import java.util.List;

/**
 * How a topographic map's contours nest: each inside the smallest that goes round it, or inside none, the map's
 * outside, its root; each holding the contours directly inside it and the spot heights inside it and in none of
 * those. A contour with nothing inside it has a spot height made for it at its pole, half as far again past its own
 * height as it is past the height round it, so a top nobody marked still rises and a hollow still sinks, and a
 * contour as high as the one round it holds the ground inside it level. And for each contour, what lies beyond its
 * line seen from outside it: how high, on the average, what is directly inside it is, and how far in that is.
 */
final class Nesting {

    /** How the root, the outside of every contour, is told apart from the contours: by this, for its index. */
    static final int ROOT = -1;

    private final List<Contour> contours;
    private final double datum;
    private final int[] parents;
    private final List<List<Integer>> children = new ArrayList<>();
    private final List<List<SpotHeight>> spots = new ArrayList<>();
    private final double[] innerHeights;
    private final double[] innerWidths;

    /** How {@code contours} nest, the ground outside them all {@code datum} high, {@code marked} among them. */
    Nesting(final List<Contour> contours, final double datum, final List<SpotHeight> marked) {
        this.contours = List.copyOf(contours);
        this.datum = datum;
        final int n = this.contours.size();
        this.parents = new int[n];
        for (int i = 0; i <= n; i++) {
            children.add(new ArrayList<>());
            spots.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            parents[i] = smallestRound(i);
            children.get(node(parents[i])).add(i);
        }
        for (final SpotHeight spot : marked) {
            spots.get(node(within(spot.at().x(), spot.at().z()))).add(spot);
        }
        this.innerHeights = new double[n];
        this.innerWidths = new double[n];
        for (int i = 0; i < n; i++) {
            if (children.get(i).isEmpty() && spots.get(i).isEmpty()) {
                final Spot pole = this.contours.get(i).loop().pole();
                spots.get(i).add(new SpotHeight(pole, height(i) + (height(i) - height(parents[i])) / 2));
            }
            inner(i);
        }
    }

    /**
     * The smallest contour going round contour {@code i}, or the root: told by its pole, the point deepest inside it,
     * so two contours running together along a stretch, as those cut by the edge of a survey do, nest as they should.
     */
    private int smallestRound(final int i) {
        final Loop loop = contours.get(i).loop();
        final Spot first = loop.pole();
        int best = ROOT;
        for (int j = 0; j < contours.size(); j++) {
            final Loop other = contours.get(j).loop();
            if (j != i && other.area() > loop.area() && loop.boxedIn(other) && other.inside(first.x(), first.z())
                    && (best == ROOT || other.area() < contours.get(best).loop().area())) {
                best = j;
            }
        }
        return best;
    }

    /** What lies beyond contour {@code i}'s line, seen from outside it: how high, and how far in. */
    private void inner(final int i) {
        final Loop loop = contours.get(i).loop();
        if (!children.get(i).isEmpty()) {
            double area = 0;
            double weighed = 0;
            double length = loop.length();
            for (final int child : children.get(i)) {
                final Loop inside = contours.get(child).loop();
                area += inside.area();
                weighed += inside.area() * height(child);
                length += inside.length();
            }
            innerHeights[i] = weighed / area;
            innerWidths[i] = Math.max(1, 2 * (loop.area() - area) / length);
        } else {
            double sum = 0;
            double far = 0;
            for (final SpotHeight spot : spots.get(i)) {
                sum += spot.height();
                far = Math.max(far, loop.distance(spot.at().x(), spot.at().z()));
            }
            innerHeights[i] = sum / spots.get(i).size();
            innerWidths[i] = Math.max(1, far);
        }
    }

    /** Where in {@code lists} node {@code i} is kept, the root last. */
    private int node(final int i) {
        return i == ROOT ? contours.size() : i;
    }

    /** The innermost contour ({@code x}, {@code z}) is inside, or the root: found from the root down. */
    int within(final double x, final double z) {
        int at = ROOT;
        boolean deeper = true;
        while (deeper) {
            deeper = false;
            for (final int child : children.get(node(at))) {
                if (contours.get(child).loop().inside(x, z)) {
                    at = child;
                    deeper = true;
                    break;
                }
            }
        }
        return at;
    }

    /** Contour {@code i}. */
    Contour contour(final int i) {
        return contours.get(i);
    }

    /** The contour round contour {@code i}, or the root. */
    int parent(final int i) {
        return parents[i];
    }

    /** The contours directly inside contour {@code i}, or inside none, for the root. */
    List<Integer> children(final int i) {
        return children.get(node(i));
    }

    /** The spot heights inside contour {@code i} and in none of its children, its pole's among them if need be. */
    List<SpotHeight> spots(final int i) {
        return spots.get(node(i));
    }

    /** How high contour {@code i} is; the root, the ground outside every contour. */
    double height(final int i) {
        return i == ROOT ? datum : contours.get(i).height();
    }

    /** Whether the ground goes up, or stays level, coming in over contour {@code i}'s line; always, for the root. */
    boolean rises(final int i) {
        return i == ROOT || height(i) >= height(parents[i]);
    }

    /** How high, on the average, what lies directly inside contour {@code i} is. */
    double innerHeight(final int i) {
        return innerHeights[i];
    }

    /** How far in from contour {@code i}'s line what lies directly inside it is, about. */
    double innerWidth(final int i) {
        return innerWidths[i];
    }
}
