package melons.innocent.plants.tree;

import java.util.List;

/**
 * How much of a view some wood fills, on the whole from every way, in square centimetres. A bar of length {@code l}
 * and thickness {@code r} fills {@code pi r l / 2} of a view on the whole, a quarter of its surface, as any convex body
 * does (Cauchy); the wood's is the sum of its bars', as though none hid another.
 *
 * @param wood the boughs
 */
public record SideOn(List<Bough> wood) {

    public SideOn {
        wood = List.copyOf(wood);
    }

    /** What of a view the wood fills, on the whole. */
    public double area() {
        double filled = 0;
        for (final Bough bough : wood) {
            for (int point = 1; point < bough.points().size(); point++) {
                final double length = bough.points().get(point).minus(bough.points().get(point - 1)).length();
                filled += Math.PI * length * (bough.thick().get(point) + bough.thick().get(point - 1)) / 4;
            }
        }
        return filled;
    }

    /**
     * How much of what is behind it a body that fills {@code body} of a view on the whole hides, with this wood
     * lying at random within it: as a haze of bars does, {@code 1 - e^-(wood / body)}.
     */
    public double hidesWithin(final double body) {
        return 1 - Math.exp(-area() / body);
    }
}
