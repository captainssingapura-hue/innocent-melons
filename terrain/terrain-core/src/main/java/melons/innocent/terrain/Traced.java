package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A path traced on the map: its lines followed from its first spot to its last, each straight or bowed as its course
 * says, a bowed one taken a point every {@link #STEP} along it; so many points in order, and how far along the path
 * each is. What is asked of it: where on it a spot is nearest, how far that is along it, and on which side.
 */
final class Traced {

    /** How far apart the points taken along a bowed line are, in centimetres. */
    static final double STEP = 1_000;

    private final double[] xs;
    private final double[] zs;
    private final double[] stations;
    private final double[] box;

    /** {@code path} traced along {@code map}'s lines. */
    Traced(final Path path, final Graph<Spot, Course> map) {
        final List<double[]> points = new ArrayList<>(List.of(new double[] {path.spots().getFirst().x(),
                path.spots().getFirst().z()}));
        for (int i = 1; i < path.spots().size(); i++) {
            followed(path.spots().get(i - 1), path.spots().get(i), map, points);
        }
        xs = new double[points.size()];
        zs = new double[points.size()];
        stations = new double[points.size()];
        box = new double[] {Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        for (int i = 0; i < points.size(); i++) {
            xs[i] = points.get(i)[0];
            zs[i] = points.get(i)[1];
            stations[i] = i == 0 ? 0 : stations[i - 1] + Math.hypot(xs[i] - xs[i - 1], zs[i] - zs[i - 1]);
            box[0] = Math.min(box[0], xs[i]);
            box[1] = Math.min(box[1], zs[i]);
            box[2] = Math.max(box[2], xs[i]);
            box[3] = Math.max(box[3], zs[i]);
        }
    }

    /** Its points, in order along it. */
    List<Spot> points() {
        final List<Spot> points = new ArrayList<>();
        for (int i = 0; i < xs.length; i++) {
            points.add(new Spot(xs[i], zs[i]));
        }
        return points;
    }

    /** How long it is. */
    double length() {
        return stations[stations.length - 1];
    }

    /** How far ({@code x}, {@code z}) is from the nearest point of the box round it, nothing inside it. */
    double outside(final double x, final double z) {
        return Math.hypot(Math.max(0, Math.max(box[0] - x, x - box[2])), Math.max(0, Math.max(box[1] - z, z - box[3])));
    }

    /**
     * The least {@code measure} makes of ({@code x}, {@code z}) from any piece of it no further off than
     * {@code within}: from how far off the piece's nearest point is and how far along the path that point is.
     * Infinity if no piece is so near. Taken over every near piece, not the nearest only, so where two stretches of
     * the path are as near as each other the least is still the one that is least, and nothing jumps.
     */
    double least(final double x, final double z, final double within, final Measure measure) {
        double least = Double.POSITIVE_INFINITY;
        for (int i = 0; i + 1 < xs.length; i++) {
            final double dx = xs[i + 1] - xs[i];
            final double dz = zs[i + 1] - zs[i];
            final double t = Math.clamp(((x - xs[i]) * dx + (z - zs[i]) * dz) / (dx * dx + dz * dz), 0, 1);
            final double distance = Math.hypot(x - xs[i] - t * dx, z - zs[i] - t * dz);
            if (distance <= within) {
                least = Math.min(least, measure.of(distance, stations[i] + t * (stations[i + 1] - stations[i])));
            }
        }
        return least;
    }

    /** What is made of a spot from how far it is from a path and how far along the path its nearest point is. */
    @FunctionalInterface
    interface Measure {

        /** What is made of a spot {@code distance} from the path, its nearest point {@code station} along it. */
        double of(double distance, double station);
    }

    /**
     * Whether ({@code x}, {@code z}) lies within it, it being closed round: whether a line from the spot out toward
     * more across crosses it an odd number of times.
     */
    boolean inside(final double x, final double z) {
        if (outside(x, z) > 0) {
            return false;
        }
        boolean inside = false;
        for (int i = 0; i + 1 < xs.length; i++) {
            if (zs[i] > z != zs[i + 1] > z
                    && x < xs[i] + (z - zs[i]) * (xs[i + 1] - xs[i]) / (zs[i + 1] - zs[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    /** Where on it ({@code x}, {@code z}) is nearest: how far off, how far along, and on which side. */
    Near near(final double x, final double z) {
        int best = 0;
        double bestT = 0;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i + 1 < xs.length; i++) {
            final double dx = xs[i + 1] - xs[i];
            final double dz = zs[i + 1] - zs[i];
            final double t = Math.clamp(((x - xs[i]) * dx + (z - zs[i]) * dz) / (dx * dx + dz * dz), 0, 1);
            final double distance = Math.hypot(x - xs[i] - t * dx, z - zs[i] - t * dz);
            if (distance < bestDistance) {
                best = i;
                bestT = t;
                bestDistance = distance;
            }
        }
        final double station = stations[best] + bestT * (stations[best + 1] - stations[best]);
        return new Near(bestDistance, station, side(best, bestT, x, z), xs[best] + bestT * (xs[best + 1] - xs[best]),
                zs[best] + bestT * (zs[best + 1] - zs[best]));
    }

    /**
     * Which side of it ({@code x}, {@code z}) is, its nearest point on piece {@code i} at {@code t} along it: its left
     * more than nothing. Where that point is where two pieces meet, told by the two pieces' left ways taken together,
     * so a spot round a corner from both is on the side it is round.
     */
    private double side(final int i, final double t, final double x, final double z) {
        final int at = t == 0 && i > 0 ? i : t == 1 && i + 2 < xs.length ? i + 1 : -1;
        if (at < 0) {
            return Math.signum(left(i, x - xs[i], z - zs[i]));
        }
        return Math.signum(left(at - 1, x - xs[at], z - zs[at]) + left(at, x - xs[at], z - zs[at]));
    }

    /** How far ({@code dx}, {@code dz}) lies to the left of piece {@code i}'s way, for each unit along it. */
    private double left(final int i, final double dx, final double dz) {
        final double ux = xs[i + 1] - xs[i];
        final double uz = zs[i + 1] - zs[i];
        return (ux * dz - uz * dx) / Math.hypot(ux, uz);
    }

    /** The line from {@code from} to {@code to} on {@code map}, followed, its points after {@code from} added. */
    private void followed(final Spot from, final Spot to, final Graph<Spot, Course> map, final List<double[]> points) {
        final Edge<Spot, Course> edge = map.edges().leaving(from).stream().filter(e -> e.beyond(from).equals(to))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no line of the map goes from " + from
                        + " to " + to));
        final boolean forward = edge.one().equals(from);
        final Spot a = edge.one();
        final Spot b = edge.other();
        final double length = a.to(b);
        final double ux = (b.x() - a.x()) / length;
        final double uz = (b.z() - a.z()) / length;
        final List<double[]> along = new ArrayList<>();
        if (edge.label() instanceof Course.Bowed(final var offset)) {
            final int steps = (int) Math.ceil(length / STEP);
            for (int s = 1; s < steps; s++) {
                final double d = length * s / steps;
                final double off = offset.held(d);
                along.add(new double[] {a.x() + ux * d - uz * off, a.z() + uz * d + ux * off});
            }
        }
        if (!forward) {
            points.addAll(along.reversed());
        } else {
            points.addAll(along);
        }
        points.add(new double[] {to.x(), to.z()});
    }

    /**
     * Where on a traced path a spot is nearest.
     *
     * @param distance how far off it is
     * @param station  how far along the path its nearest point is
     * @param side     which side it is on: one to the path's left, minus one to its right
     * @param x        how far across that nearest point is
     * @param z        how far along
     */
    record Near(double distance, double station, double side, double x, double z) {
    }
}
