package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.sim.Land;
import com.example.fxsuite.avatar.sim.Vec2;

import java.util.ArrayList;
import java.util.List;

/**
 * A quarter of the campus: a group of libraries released at one version, the platform's (the facilities, round the
 * main office) or a lab's, set out on the meadow by what its scope does. For now its ground only, no building: each of
 * its plots laid in a cover of its own ({@code ground}), so the quarters are told apart on the land at a glance.
 *
 * @param name   what it is called: its unit's name
 * @param unit   which unit of the organisation it is: the facilities, or a lab, and what it holds
 * @param ground what covers its plots
 * @param plots  where it lies: its plots, the first its main one, any others outlying
 */
public record Quarter(String name, String unit, Land.Cover ground, List<Plot> plots) {

    /** How far in from a plot's edge its cover is all there: thinning out into the meadow's grass across that. */
    static final double EDGE = 150;

    public Quarter {
        plots = List.copyOf(plots);
        if (plots.isEmpty()) {
            throw new IllegalArgumentException(name + ": somewhere to lie");
        }
    }

    /**
     * A plot of a quarter's: an outline that does not cross itself, convex, going round anticlockwise (x east, z
     * north), in centimetres; and what it is for.
     */
    public record Plot(String what, List<Vec2> outline) {
        public Plot {
            outline = List.copyOf(outline);
        }

        /** Whether ({@code x}, {@code z}) is inside it. */
        public boolean inside(final double x, final double z) {
            for (int k = 0; k < outline.size(); k++) {
                final Vec2 a = outline.get(k);
                final Vec2 b = outline.get((k + 1) % outline.size());
                if ((b.x() - a.x()) * (z - a.z()) - (b.z() - a.z()) * (x - a.x()) < 0) {
                    return false;
                }
            }
            return true;
        }

        /** How far ({@code x}, {@code z}) is from its nearest edge, inside it or out. */
        public double fromEdge(final double x, final double z) {
            double nearest = Double.POSITIVE_INFINITY;
            for (int k = 0; k < outline.size(); k++) {
                final Vec2 a = outline.get(k);
                final Vec2 b = outline.get((k + 1) % outline.size());
                final double dx = b.x() - a.x();
                final double dz = b.z() - a.z();
                final double t = Math.clamp(((x - a.x()) * dx + (z - a.z()) * dz) / (dx * dx + dz * dz), 0, 1);
                nearest = Math.min(nearest, Math.hypot(x - a.x() - t * dx, z - a.z() - t * dz));
            }
            return nearest;
        }

        /** Its middle: the mean of its corners. */
        public Vec2 middle() {
            return new Vec2(outline.stream().mapToDouble(Vec2::x).average().orElseThrow(),
                    outline.stream().mapToDouble(Vec2::z).average().orElseThrow());
        }
    }

    /**
     * Its plots as the land has them ({@link Land.Layer}): each lays the quarter's cover over its outline, all of it
     * {@link #EDGE} in from its edge and thinning out to its edge, the land's shape as it was.
     */
    public List<Land.Layer> layers() {
        return plots.stream().map(p -> (Land.Layer) new Ground(ground, p)).toList();
    }

    /** A plot laid on the land: its cover over it, nothing else changed. */
    private record Ground(Land.Cover cover, Plot plot) implements Land.Layer {

        @Override
        public double shape(final double x, final double z, final double under) {
            return under;
        }

        @Override
        public boolean touches(final double minX, final double minZ, final double maxX, final double maxZ) {
            final List<Vec2> o = plot.outline();
            return o.stream().mapToDouble(Vec2::x).min().orElseThrow() <= maxX
                    && o.stream().mapToDouble(Vec2::x).max().orElseThrow() >= minX
                    && o.stream().mapToDouble(Vec2::z).min().orElseThrow() <= maxZ
                    && o.stream().mapToDouble(Vec2::z).max().orElseThrow() >= minZ;
        }

        @Override
        public double deepest() {
            return 0;
        }

        @Override
        public double cover(final Land.Cover c, final double x, final double z) {
            if (c != cover || !plot.inside(x, z)) {
                return 0;
            }
            return Math.min(1, plot.fromEdge(x, z) / EDGE);
        }
    }

    /** A rectangle from ({@code x0}, {@code z0}) to ({@code x1}, {@code z1}), metres, anticlockwise. */
    static List<Vec2> rectangle(final double x0, final double z0, final double x1, final double z1) {
        return metres(new double[][] {{x0, z0}, {x1, z0}, {x1, z1}, {x0, z1}});
    }

    /** A round about ({@code x}, {@code z}), {@code r} out, metres, in so many sides, anticlockwise. */
    static List<Vec2> round(final double x, final double z, final double r, final int sides) {
        final List<Vec2> round = new ArrayList<>(sides);
        for (int k = 0; k < sides; k++) {
            final double a = 2 * Math.PI * k / sides;
            round.add(new Vec2(100 * (x + r * Math.cos(a)), 100 * (z + r * Math.sin(a))));
        }
        return round;
    }

    /** Corners given in metres, as centimetres. */
    static List<Vec2> metres(final double[][] corners) {
        final List<Vec2> outline = new ArrayList<>(corners.length);
        for (final double[] c : corners) {
            outline.add(new Vec2(100 * c[0], 100 * c[1]));
        }
        return outline;
    }
}
