package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Vector;

import java.util.List;

/**
 * A loop on the map: spots in order round it, straight from each to the next and from the last back to the first,
 * either way round, three at least and going round some ground. What is asked of it: whether a spot is inside it, how
 * far a spot is from its line, how much ground it goes round, how long it is, and its pole, the point inside it
 * furthest from its line. Equal by its spots, in their order.
 */
public final class Loop implements Vector<Spot> {

    private final List<Spot> spots;
    private final double[] xs;
    private final double[] zs;
    private final double[] box;
    private final double area;
    private final double length;
    private final Segments segments;

    /** Round {@code spots}, in order. */
    public Loop(final List<Spot> spots) {
        this.spots = List.copyOf(spots);
        final int n = this.spots.size();
        xs = new double[n];
        zs = new double[n];
        box = new double[] {Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        double twice = 0;
        double around = 0;
        for (int i = 0; i < n; i++) {
            xs[i] = this.spots.get(i).x();
            zs[i] = this.spots.get(i).z();
            box[0] = Math.min(box[0], xs[i]);
            box[1] = Math.min(box[1], zs[i]);
            box[2] = Math.max(box[2], xs[i]);
            box[3] = Math.max(box[3], zs[i]);
        }
        for (int i = 0; i < n; i++) {
            final int j = (i + 1) % n;
            twice += xs[i] * zs[j] - xs[j] * zs[i];
            around += Math.hypot(xs[j] - xs[i], zs[j] - zs[i]);
        }
        area = Math.abs(twice) / 2;
        length = around;
        segments = new Segments(xs, zs, true);
        if (n < 3 || !(area > 0)) {
            throw new IllegalArgumentException("a loop goes round some ground, through three spots at least: "
                    + this.spots);
        }
    }

    /** Its spots, in order round it. */
    public List<Spot> spots() {
        return spots;
    }

    /** How much ground it goes round, in square centimetres. */
    public double area() {
        return area;
    }

    /** How long its line is, all the way round. */
    public double length() {
        return length;
    }

    /** Whether ({@code x}, {@code z}) is within the box round it, its edges included. */
    public boolean boxed(final double x, final double z) {
        return x >= box[0] && x <= box[2] && z >= box[1] && z <= box[3];
    }

    /** Whether the box round it lies wholly within the box round {@code other}. */
    public boolean boxedIn(final Loop other) {
        return box[0] >= other.box[0] && box[1] >= other.box[1] && box[2] <= other.box[2] && box[3] <= other.box[3];
    }

    /**
     * Whether ({@code x}, {@code z}) is inside it: whether a line from it on across crosses it an odd number of
     * times.
     */
    public boolean inside(final double x, final double z) {
        return boxed(x, z) && segments.inside(x, z);
    }

    /** How far ({@code x}, {@code z}) is from its line. */
    public double distance(final double x, final double z) {
        final double[] near = new double[3];
        segments.nearest(x, z, near);
        return near[0];
    }

    /**
     * Its pole: the point inside it furthest from its line, where a top nobody marked rises to or a hollow sinks to.
     * Found by sampling across it, then closer and closer round the best so far; its middle, if no sample is inside.
     */
    public Spot pole() {
        final int samples = 24;
        double stepX = (box[2] - box[0]) / samples;
        double stepZ = (box[3] - box[1]) / samples;
        double bestX = (box[0] + box[2]) / 2;
        double bestZ = (box[1] + box[3]) / 2;
        double far = -1;
        for (int i = 0; i <= samples; i++) {
            for (int j = 0; j <= samples; j++) {
                final double x = box[0] + i * stepX;
                final double z = box[1] + j * stepZ;
                if (inside(x, z) && distance(x, z) > far) {
                    far = distance(x, z);
                    bestX = x;
                    bestZ = z;
                }
            }
        }
        for (int round = 0; round < 10; round++) {
            stepX /= 2;
            stepZ /= 2;
            final double aroundX = bestX;
            final double aroundZ = bestZ;
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    final double x = aroundX + i * stepX;
                    final double z = aroundZ + j * stepZ;
                    if (inside(x, z) && distance(x, z) > far) {
                        far = distance(x, z);
                        bestX = x;
                        bestZ = z;
                    }
                }
            }
        }
        return new Spot(bestX, bestZ);
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Loop loop && spots.equals(loop.spots);
    }

    @Override
    public int hashCode() {
        return spots.hashCode();
    }

    @Override
    public String toString() {
        return "Loop" + spots;
    }
}
