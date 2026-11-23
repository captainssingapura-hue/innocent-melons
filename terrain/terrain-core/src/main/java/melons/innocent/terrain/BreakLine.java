package melons.innocent.terrain;

import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Monotone;

import java.util.ArrayList;
import java.util.List;

/**
 * A break line of a topographic map: an open line where the ground's slope turns at once, so sharply that its
 * contours alone would round it off. A crest, along a ridge's spine, the ground falling away both sides; or a
 * thalweg, along a valley's bottom or a river's bed, the ground rising both sides. How high the ground is along it is
 * given at its spots, and between them is a monotone curve over how far along it is, so it never rises past a spot
 * between two lower ones nor sinks past it between two higher.
 */
public final class BreakLine {

    /** Which way the ground turns across a break line. */
    public enum Kind {
        /** A ridge's spine: the ground falls away both sides. */
        CREST,
        /** A valley's bottom: the ground rises both sides. */
        THALWEG
    }

    private final String name;
    private final Kind kind;
    private final List<Spot> spots;
    private final List<Double> heights;
    private final double[] xs;
    private final double[] zs;
    private final double[] stations;
    private final double[] box;
    private final Monotone along;
    private final Segments segments;
    private final double low;
    private final double high;

    /** {@code name}, a {@code kind}, through {@code spots} in order, the ground as high at each as {@code heights}. */
    public BreakLine(final String name, final Kind kind, final List<Spot> spots, final List<Double> heights) {
        this.name = name;
        this.kind = kind;
        this.spots = List.copyOf(spots);
        this.heights = List.copyOf(heights);
        if (this.spots.size() < 2 || this.spots.size() != this.heights.size()) {
            throw new IllegalArgumentException(name + ": a break line through two spots at least, a height at each");
        }
        final int n = this.spots.size();
        xs = new double[n];
        zs = new double[n];
        stations = new double[n];
        box = new double[] {Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        final List<Knot> knots = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            xs[i] = this.spots.get(i).x();
            zs[i] = this.spots.get(i).z();
            stations[i] = i == 0 ? 0 : stations[i - 1] + Math.hypot(xs[i] - xs[i - 1], zs[i] - zs[i - 1]);
            knots.add(new Knot(stations[i], this.heights.get(i)));
            box[0] = Math.min(box[0], xs[i]);
            box[1] = Math.min(box[1], zs[i]);
            box[2] = Math.max(box[2], xs[i]);
            box[3] = Math.max(box[3], zs[i]);
        }
        this.along = new Monotone(new Knots(knots));
        this.segments = new Segments(xs, zs, false);
        this.low = this.heights.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
        this.high = this.heights.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
    }

    /** Its name. */
    public String name() {
        return name;
    }

    /** Which way the ground turns across it. */
    public Kind kind() {
        return kind;
    }

    /** Its spots, in order along it. */
    public List<Spot> spots() {
        return spots;
    }

    /** How high the ground is at each of its spots. */
    public List<Double> heights() {
        return heights;
    }

    /** How low the ground is along it, at the lowest. */
    public double lowest() {
        return low;
    }

    /** How high the ground is along it, at the highest. */
    public double highest() {
        return high;
    }

    /** How far ({@code x}, {@code z}) is from the box round it, nothing inside it. */
    public double outside(final double x, final double z) {
        return Math.hypot(Math.max(0, Math.max(box[0] - x, x - box[2])), Math.max(0, Math.max(box[1] - z, z - box[3])));
    }

    /** Where on it ({@code x}, {@code z}) is nearest: how far off that is, and how high the ground is there. */
    public Reach reach(final double x, final double z) {
        final double[] near = new double[3];
        segments.nearest(x, z, near);
        final int k = (int) near[1];
        return new Reach(near[0], along.held(stations[k] + near[2] * (stations[k + 1] - stations[k])));
    }

    /**
     * Where on a break line a spot is nearest.
     *
     * @param distance how far off it is
     * @param height   how high the ground is there, on the line
     */
    public record Reach(double distance, double height) {
    }
}
