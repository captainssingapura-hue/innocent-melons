package melons.innocent.road.surface;

import java.util.List;

/**
 * A road's surface across it: its strips from its left edge to its right, going along it, the road's line down its
 * middle; its pavement falling away from the line by {@code fall}, rise over run, so water runs off it to its edges;
 * and whether lines are painted on it ({@link Markings}). Laid on level ground, it stands on the ground, its lowest
 * edges {@link #LOWEST} over it, and higher toward the line as it falls away from there. In centimetres.
 *
 * @param strips its strips, left to right
 * @param fall   how steeply its pavement falls away from its line
 * @param marked whether lines are painted on it
 */
public record Section(List<Strip> strips, double fall, boolean marked) {

    /** How high its pavement is at its lowest, over level ground. */
    public static final double LOWEST = 5;

    public Section {
        strips = List.copyOf(strips);
        if (strips.isEmpty() || !(fall >= 0 && fall <= 0.06)) {
            throw new IllegalArgumentException("a road of some strips, falling 0 to 6%: " + strips + ", " + fall);
        }
    }

    /** How wide it is, all told. */
    public double wide() {
        return strips.stream().mapToDouble(Strip::wide).sum();
    }

    /** How far across from its line, left negative, its {@code k}th strip begins; its strips' count, its right edge. */
    public double edge(final int k) {
        double x = -wide() / 2;
        for (int i = 0; i < k; i++) {
            x += strips.get(i).wide();
        }
        return x;
    }

    /** How high its pavement's top is {@code x} across from its line, laid on level ground. */
    public double top(final double x) {
        return LOWEST + fall * (wide() / 2 - Math.abs(x));
    }
}
