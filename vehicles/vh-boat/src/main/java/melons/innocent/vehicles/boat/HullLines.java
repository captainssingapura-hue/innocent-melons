package melons.innocent.vehicles.boat;

import com.example.fxsuite.math.graph.Directed;
import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Monotone;

import java.util.ArrayList;
import java.util.List;

/**
 * A river boat's hull lines, its right half, as a typed graph: its sheer, its chine and its keel, each a run of named
 * points from the transom to the bow, joined by edges that say which line they are and how they run; and its stem,
 * from the sheer's end at the stem head down past where the chine meets it to the keel's end at its foot. Long, low and
 * beamy, flat-bottomed, its sheer swept up to a raked bow. The left half is the right turned over the middle line.
 * Its sizes are the builder's: about 13.2 m long, 3.2 m in beam, a metre of freeboard and 60 cm of draught.
 */
public record HullLines() {

    /**
     * Each station along the hull, from the transom: how far along; the sheer's half-breadth and height; the chine's
     * half-breadth and height; the keel's height. In centimetres, over the water's line.
     */
    private double[][] stations() {
        return new double[][] {{-650, 135, 100, 125, -5, -35}, {-450, 152, 96, 142, -25, -55},
            {-150, 160, 94, 150, -32, -62}, {150, 160, 95, 148, -30, -60}, {400, 148, 105, 128, -18, -48},
            {560, 112, 122, 80, 0, -30}, {640, 50, 135, 22, 15, -12}};
    }

    /** How far the sheer and the chine bow out between their middle stations, in centimetres. */
    private static final double SWELL = 4;

    /** The lines, its right half. */
    public Graph<HullPoint, HullLine> graph() {
        final double[][] at = stations();
        final List<HullPoint> points = new ArrayList<>();
        final List<Edge<HullPoint, HullLine>> lines = new ArrayList<>();
        final HullPoint head = new HullPoint("stem.head", 0, 142, 668);
        final HullPoint middle = new HullPoint("stem.middle", 0, 62, 664);
        final HullPoint foot = new HullPoint("stem.foot", 0, -8, 650);
        for (final HullLine.Kind kind : List.of(HullLine.Kind.SHEER, HullLine.Kind.CHINE, HullLine.Kind.KEEL)) {
            HullPoint before = null;
            for (int s = 0; s < at.length; s++) {
                final HullPoint here = point(kind, s, at[s]);
                points.add(here);
                if (before != null) {
                    lines.add(new Directed<>(before, here, run(kind, s)));
                }
                before = here;
            }
            final HullPoint end = kind == HullLine.Kind.SHEER ? head : kind == HullLine.Kind.CHINE ? middle : foot;
            lines.add(new Directed<>(before, end, new HullLine.Straight(kind)));
        }
        points.addAll(List.of(head, middle, foot));
        lines.add(new Directed<>(head, middle, new HullLine.Straight(HullLine.Kind.STEM)));
        lines.add(new Directed<>(middle, foot, new HullLine.Straight(HullLine.Kind.STEM)));
        return new Graph<>(points, lines);
    }

    private HullPoint point(final HullLine.Kind kind, final int s, final double[] at) {
        return switch (kind) {
            case SHEER -> new HullPoint("sheer." + s, at[1], at[2], at[0]);
            case CHINE -> new HullPoint("chine." + s, at[3], at[4], at[0]);
            default -> new HullPoint("keel." + s, 0, at[5], at[0]);
        };
    }

    /** How the edge of {@code kind} ending at station {@code s} runs: the sheer and chine swell amidships. */
    private HullLine run(final HullLine.Kind kind, final int s) {
        if (kind != HullLine.Kind.KEEL && s >= 2 && s <= 4) {
            return new HullLine.Bowed(kind, new Monotone(new Knots(List.of(new Knot(0, 0), new Knot(0.5, SWELL),
                    new Knot(1, 0)))));
        }
        return new HullLine.Straight(kind);
    }

    /**
     * Line {@code kind}, followed through the graph from its first point, where no edge of it enters, to its last,
     * {@code samples} points evenly along its length, each as it runs (bowed where its edge bows). Its right half.
     */
    public List<Vec3> line(final HullLine.Kind kind, final int samples) {
        final Graph<HullPoint, HullLine> graph = graph();
        final List<Vec3> fine = new ArrayList<>();
        HullPoint at = graph.vertices().listed().stream()
                .filter(p -> leaving(graph, p, kind) != null && graph.edges().entering(p).stream()
                        .noneMatch(edge -> edge.label().kind() == kind))
                .findFirst().orElseThrow(() -> new IllegalStateException("no line " + kind));
        Edge<HullPoint, HullLine> edge = leaving(graph, at, kind);
        while (edge != null) {
            for (int k = 0; k < 16; k++) {
                fine.add(along(edge, k / 16.0));
            }
            at = edge.other();
            edge = leaving(graph, at, kind);
        }
        fine.add(of(at));
        return resampled(fine, samples);
    }

    private Edge<HullPoint, HullLine> leaving(final Graph<HullPoint, HullLine> graph, final HullPoint p,
                                             final HullLine.Kind kind) {
        return graph.edges().leaving(p).stream().filter(edge -> edge.label().kind() == kind).findFirst()
                .orElse(null);
    }

    /** Where {@code edge} runs, {@code share} of the way along it. */
    private Vec3 along(final Edge<HullPoint, HullLine> edge, final double share) {
        final Vec3 from = of(edge.one());
        final Vec3 straight = from.plus(of(edge.other()).minus(from).times(share));
        if (edge.label() instanceof HullLine.Bowed(final HullLine.Kind kind, final var out)) {
            return straight.plus(new Vec3(out.at(share), 0, 0));
        }
        return straight;
    }

    private Vec3 of(final HullPoint p) {
        return new Vec3(p.across(), p.up(), p.along());
    }

    /** {@code fine} again as {@code samples} points evenly along its length. */
    private List<Vec3> resampled(final List<Vec3> fine, final int samples) {
        final double[] run = new double[fine.size()];
        for (int i = 1; i < fine.size(); i++) {
            run[i] = run[i - 1] + fine.get(i).minus(fine.get(i - 1)).length();
        }
        final List<Vec3> even = new ArrayList<>();
        int i = 1;
        for (int k = 0; k < samples; k++) {
            final double want = run[run.length - 1] * k / (samples - 1);
            while (i < run.length - 1 && run[i] < want) {
                i++;
            }
            final double piece = run[i] - run[i - 1];
            final double share = piece > 0 ? Math.clamp((want - run[i - 1]) / piece, 0, 1) : 0;
            even.add(fine.get(i - 1).plus(fine.get(i).minus(fine.get(i - 1)).times(share)));
        }
        return even;
    }
}
