package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;

import java.util.List;

/**
 * The solids a road's surface is made of, each a stretch of something along the road, square across it: its foot from
 * one side to the other, its top from one side to the other above it, each side as high as it is, the same all the
 * way along it. In centimetres: across the road, along it, up.
 */
record Shapes() {

    /** A run along the road: its foot and its top, each across it from one side to the other. */
    record Run(double z0, double z1) {
    }

    /**
     * A solid called {@code name}, of {@code material}, along {@code run}: its foot from {@code x0}, {@code y0} high,
     * to {@code x1}, {@code y1} high; its top from {@code tx0}, {@code t0} high, to {@code tx1}, {@code t1} high.
     */
    Solid solid(final String name, final Role role, final Material material, final Run run, final double x0,
                final double y0, final double x1, final double y1, final double tx0, final double t0,
                final double tx1, final double t1) {
        return new Solid(name, role, material, List.of(new Point(x0, run.z0(), y0), new Point(x1, run.z0(), y1),
                new Point(x0, run.z1(), y0), new Point(x1, run.z1(), y1), new Point(tx0, run.z0(), t0),
                new Point(tx1, run.z0(), t1), new Point(tx0, run.z1(), t0), new Point(tx1, run.z1(), t1)));
    }

    /** The same, its top as wide as its foot. */
    Solid solid(final String name, final Role role, final Material material, final Run run, final double x0,
                final double y0, final double x1, final double y1, final double t0, final double t1) {
        return solid(name, role, material, run, x0, y0, x1, y1, x0, t0, x1, t1);
    }
}
