package melons.innocent.road.surface;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;

import java.util.List;

/**
 * The solids a road's surface is made of, each a run of something along the road's {@code course}, square across it:
 * its foot from one side to the other, its top from one side to the other above it, each side so high over what the
 * road is laid on, the same all the way along it, following the course from one end of the run to the other. In
 * centimetres: across the road, along it, up.
 *
 * @param course what the road is laid along
 */
record Shapes(Course course) {

    /** A run along the road, from so far along its course to so far. */
    record Run(double s0, double s1) {
    }

    /**
     * A solid called {@code name}, of {@code material}, along {@code run}: its foot from {@code x0}, {@code y0} high,
     * to {@code x1}, {@code y1} high; its top from {@code tx0}, {@code t0} high, to {@code tx1}, {@code t1} high.
     */
    Solid solid(final String name, final Role role, final Material material, final Run run, final double x0,
                final double y0, final double x1, final double y1, final double tx0, final double t0,
                final double tx1, final double t1) {
        return new Solid(name, role, material, List.of(at(x0, run.s0(), y0), at(x1, run.s0(), y1),
                at(x0, run.s1(), y0), at(x1, run.s1(), y1), at(tx0, run.s0(), t0), at(tx1, run.s0(), t1),
                at(tx0, run.s1(), t0), at(tx1, run.s1(), t1)));
    }

    /** The same, its top as wide as its foot. */
    Solid solid(final String name, final Role role, final Material material, final Run run, final double x0,
                final double y0, final double x1, final double y1, final double t0, final double t1) {
        return solid(name, role, material, run, x0, y0, x1, y1, x0, t0, x1, t1);
    }

    private Point at(final double x, final double s, final double y) {
        return course.beside(s, x, y);
    }
}
