package melons.innocent.building.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Cover between pillars: along a side, the runs of it the pillars on that side leave, each pillar standing where it
 * stands and the cover filling between, a run too short ({@link #SHORTEST}) left out. In centimetres.
 *
 * @param pillars the pillars it fills between
 */
record Infill(List<Box> pillars) {

    /** How short a run of cover between two pillars is let be. */
    static final double SHORTEST = 20;

    Infill {
        pillars = List.copyOf(pillars);
    }

    /**
     * A side to cover, from one point to another, of its kind.
     *
     * @param x0   where it begins, across
     * @param z0   where it begins, along
     * @param x1   where it ends, across
     * @param z1   where it ends, along
     * @param kind what its cover is
     * @param name what its cover is called
     */
    record Side(double x0, double z0, double x1, double z1, FloorPlan.Kind kind, String name) {
    }

    /**
     * {@code side}'s cover, {@code thick}, its middle {@code off} to the left of the side as it runs: between the
     * pillars that stand where it would.
     */
    List<FloorPlan.Cover> along(final Side side, final double off, final double thick) {
        final double length = Math.hypot(side.x1() - side.x0(), side.z1() - side.z0());
        final double ux = (side.x1() - side.x0()) / length;
        final double uz = (side.z1() - side.z0()) / length;
        final List<double[]> spans = new ArrayList<>();
        for (final Box p : pillars) {
            double t0 = Double.MAX_VALUE;
            double t1 = -Double.MAX_VALUE;
            double s0 = Double.MAX_VALUE;
            double s1 = -Double.MAX_VALUE;
            for (final double x : new double[] {p.x0(), p.x1()}) {
                for (final double z : new double[] {p.z0(), p.z1()}) {
                    final double t = (x - side.x0()) * ux + (z - side.z0()) * uz;
                    final double s = -(x - side.x0()) * uz + (z - side.z0()) * ux;
                    t0 = Math.min(t0, t);
                    t1 = Math.max(t1, t);
                    s0 = Math.min(s0, s);
                    s1 = Math.max(s1, s);
                }
            }
            if (s0 < off + thick / 2 && s1 > off - thick / 2 && t1 > 0 && t0 < length) {
                spans.add(new double[] {Math.max(0, t0), Math.min(length, t1)});
            }
        }
        spans.sort((a, b) -> Double.compare(a[0], b[0]));
        final List<FloorPlan.Cover> covers = new ArrayList<>();
        final double ox = side.x0() - uz * off;
        final double oz = side.z0() + ux * off;
        double from = 0;
        for (final double[] span : spans) {
            run(covers, side, ox, oz, ux, uz, thick, from, span[0]);
            from = Math.max(from, span[1]);
        }
        run(covers, side, ox, oz, ux, uz, thick, from, length);
        return covers;
    }

    /** A run of {@code side}'s cover from {@code from} to {@code to} along it: if it is long enough. */
    private void run(final List<FloorPlan.Cover> covers, final Side side, final double ox, final double oz,
                     final double ux, final double uz, final double thick, final double from, final double to) {
        if (to - from >= SHORTEST) {
            covers.add(new FloorPlan.Cover(new FloorPlan.Wall(ox + ux * from, oz + uz * from, ox + ux * to,
                    oz + uz * to, thick, side.name()), side.kind()));
        }
    }
}
