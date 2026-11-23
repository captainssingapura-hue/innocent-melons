package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Wood too fine to be drawn one bough at a time, drawn as a haze: an egg-shaped clump round it, along the ways the
 * wood lies, just big enough that every bough of it is inside, and seen through as much as the wood in it lets
 * through ({@link #hides}). In the tree's own frame, centimetres.
 *
 * @param middle where its middle is
 * @param ways   the three ways it lies, each one long, the longest first
 * @param reach  how far it reaches from its middle along each of those ways
 * @param hides  how much of what is behind it the wood in it hides, seen from anywhere, on the whole
 */
public record Clump(Vec3 middle, List<Vec3> ways, List<Double> reach, double hides) {

    /** The fewest centimetres a clump reaches any way, and the least share of its longest reach. */
    private static final double THINNEST = 4;
    private static final double SHARE = 0.2;
    /** Knud Thomsen's power, for an egg's surface to within one part in a hundred. */
    private static final double THOMSEN = 1.6075;

    public Clump {
        ways = List.copyOf(ways);
        reach = List.copyOf(reach);
    }

    /**
     * The clump round {@code wood}: its points, each bough's thickness added round it, enclosed along the ways they
     * lie; how much it hides worked out from how much of a view the wood would fill, against how much the clump does.
     */
    public Clump(final List<Bough> wood) {
        this(new Fitted(wood));
    }

    private Clump(final Fitted fitted) {
        this(fitted.middle, fitted.ways, fitted.reach, fitted.hides());
    }

    /**
     * How a clump is fitted: the wood's points along the ways they lie, the egg as long each way as the points are
     * spread, and then grown evenly until the furthest point, its bough's thickness round it, is inside.
     */
    private static final class Fitted {

        private final List<Bough> wood;
        private final Vec3 middle;
        private final List<Vec3> ways;
        private final List<Double> reach;

        Fitted(final List<Bough> wood) {
            this.wood = wood;
            final List<Vec3> points = new ArrayList<>();
            wood.forEach(bough -> points.addAll(bough.points()));
            final Lying lying = new Lying(points);
            this.ways = lying.ways();
            final double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
            final double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
            for (final Vec3 point : points) {
                for (int way = 0; way < 3; way++) {
                    low[way] = Math.min(low[way], point.dot(ways.get(way)));
                    high[way] = Math.max(high[way], point.dot(ways.get(way)));
                }
            }
            Vec3 centre = new Vec3(0, 0, 0);
            final double[] half = new double[3];
            for (int way = 0; way < 3; way++) {
                centre = centre.plus(ways.get(way).times((low[way] + high[way]) / 2));
                half[way] = (high[way] - low[way]) / 2;
            }
            this.middle = centre;
            final double longest = Math.max(half[0], Math.max(half[1], half[2]));
            for (int way = 0; way < 3; way++) {
                half[way] = Math.max(half[way], Math.max(THINNEST, SHARE * longest));
            }
            this.reach = grown(half);
        }

        /** {@code half} grown evenly until every bough, its thickness round it, is inside. */
        private List<Double> grown(final double[] half) {
            double most = 0;
            for (final Bough bough : wood) {
                for (int point = 0; point < bough.points().size(); point++) {
                    final Vec3 from = bough.points().get(point).minus(middle);
                    final double thick = bough.thick().get(point);
                    double sum = 0;
                    for (int way = 0; way < 3; way++) {
                        final double along = (Math.abs(from.dot(ways.get(way))) + thick) / half[way];
                        sum += along * along;
                    }
                    most = Math.max(most, Math.sqrt(sum));
                }
            }
            final double by = Math.max(most, 1e-9);
            return List.of(half[0] * by, half[1] * by, half[2] * by);
        }

        /**
         * How much it hides: what of a view the wood fills, on the whole from every way, against what the clump
         * fills, the wood taken as lying at random within it. A bar of length {@code l} and thickness {@code r}
         * fills a quarter of its surface on the whole, {@code pi r l / 2}, and so does any convex body (Cauchy); so
         * the share is filled by the bars as a haze of them is, {@code 1 - e^-(wood / clump)}.
         */
        double hides() {
            double filled = 0;
            for (final Bough bough : wood) {
                for (int point = 1; point < bough.points().size(); point++) {
                    final double length = bough.points().get(point).minus(bough.points().get(point - 1)).length();
                    filled += Math.PI * length * (bough.thick().get(point) + bough.thick().get(point - 1)) / 4;
                }
            }
            return 1 - Math.exp(-filled / (surface() / 4));
        }

        /** Its surface, as Thomsen gives an egg's. */
        private double surface() {
            final double a = Math.pow(reach.get(0), THOMSEN);
            final double b = Math.pow(reach.get(1), THOMSEN);
            final double c = Math.pow(reach.get(2), THOMSEN);
            return 4 * Math.PI * Math.pow((a * b + a * c + b * c) / 3, 1 / THOMSEN);
        }
    }

    /** Whether {@code point} is inside it, or on it. */
    public boolean holds(final Vec3 point) {
        final Vec3 from = point.minus(middle);
        double sum = 0;
        for (int way = 0; way < 3; way++) {
            final double along = from.dot(ways.get(way)) / reach.get(way);
            sum += along * along;
        }
        return sum <= 1 + 1e-9;
    }

    /**
     * Its surface added to {@code forming}: {@code round} points round each of {@code rings} rings from end to end
     * along its longest way, each facing out from it, and a point at each end. Where on its surface a point is, is
     * where it is.
     */
    public void formed(final Forming forming, final int round, final int rings) {
        final List<Integer> ring = new ArrayList<>();
        final int first = point(forming, Math.PI, 0);
        for (int r = 1; r <= rings; r++) {
            for (int k = 0; k < round; k++) {
                ring.add(point(forming, Math.PI * (1 - (double) r / (rings + 1)), 2 * Math.PI * k / round));
            }
        }
        final int last = point(forming, 0, 0);
        for (int k = 0; k < round; k++) {
            final int next = (k + 1) % round;
            forming.faced(first, ring.get(k), ring.get(next));
            forming.faced(last, ring.get((rings - 1) * round + next), ring.get((rings - 1) * round + k));
            for (int r = 0; r + 1 < rings; r++) {
                forming.faced(ring.get(r * round + k), ring.get((r + 1) * round + k), ring.get((r + 1) * round + next));
                forming.faced(ring.get(r * round + k), ring.get((r + 1) * round + next), ring.get(r * round + next));
            }
        }
    }

    /** The point {@code polar} from its far end along its longest way and {@code round} about it, facing out. */
    private int point(final Forming forming, final double polar, final double round) {
        final double[] unit = {Math.cos(polar), Math.sin(polar) * Math.cos(round), Math.sin(polar) * Math.sin(round)};
        Vec3 where = middle;
        Vec3 out = new Vec3(0, 0, 0);
        for (int way = 0; way < 3; way++) {
            where = where.plus(ways.get(way).times(unit[way] * reach.get(way)));
            out = out.plus(ways.get(way).times(unit[way] / reach.get(way)));
        }
        return forming.point(where, out.unit(), where);
    }
}
