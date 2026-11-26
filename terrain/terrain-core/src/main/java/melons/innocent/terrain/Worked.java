package melons.innocent.terrain;

import java.util.List;

/**
 * A ground and the works made over it, in order, each over the ground the ones before it left: the natural ground of
 * a topographic map, say, and what people dug, dredged, built up and levelled on it. Its extent is the ground's.
 *
 * @param base  the ground as it was found
 * @param works what was made over it, in the order made
 */
public record Worked(Ground base, List<Work> works) implements Ground {

    public Worked {
        works = List.copyOf(works);
    }

    @Override
    public Extent extent() {
        return base.extent();
    }

    @Override
    public double height(final double x, final double z) {
        double height = base.height(x, z);
        for (final Work work : works) {
            height = work.made(height, x, z);
        }
        return height;
    }
}
