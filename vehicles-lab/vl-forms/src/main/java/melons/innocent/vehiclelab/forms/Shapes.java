package melons.innocent.vehiclelab.forms;

import com.example.fxsuite.vulkan.form.Ball;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Ellipsoid;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Lofting;
import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Tube;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The shapes a vehicle's parts are made of, each placed where it is in the vehicle's frame (across as x, up as y,
 * along as z), every round one {@code round} flat sides round, lit as round: a round body, longer one way than
 * another; a drum, a short round bar closed at its ends, its ends rounded if asked; a pipe bent along a path, closed
 * at each bend by a ball; a ring round a wheel's axle, as a tyre is, round or oval in section; a band bent round a
 * wheel's axle, as a fender is or a rim; and a box. A wheel's axle runs across.
 *
 * @param round how many flat sides a round shape is round in
 */
public record Shapes(int round) {

    /** A round body at {@code middle}, {@code across} wide, {@code up} high and {@code along} long. */
    public Mesh body(final Vec3 middle, final double across, final double up, final double along) {
        return placed(new Ellipsoid(across, up, along).formed(round, round / 2), new Mat4(middle));
    }

    /** A box at {@code middle}, {@code across} wide, {@code up} high and {@code along} long. */
    public Mesh box(final Vec3 middle, final double across, final double up, final double along) {
        return placed(new Cuboid(across, up, along).formed(), new Mat4(middle));
    }

    /** A drum from {@code from} to {@code to}, {@code radius} round, its ends flat. */
    public Mesh drum(final Vec3 from, final Vec3 to, final double radius) {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        new Tube(from, to, radius, round).formed(forming);
        return forming.formed();
    }

    /** A drum from {@code from} to {@code to}, {@code radius} round, its ends rounded as a ball is. */
    public Mesh capsule(final Vec3 from, final Vec3 to, final double radius) {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        new Tube(from, to, radius, round).formed(forming);
        new Ball(from, radius, round).formed(forming);
        new Ball(to, radius, round).formed(forming);
        return forming.formed();
    }

    /** A pipe {@code radius} round along {@code path}, from its first point to its last, a ball at each bend. */
    public Mesh pipe(final List<Vec3> path, final double radius) {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        for (int k = 1; k < path.size(); k++) {
            new Tube(path.get(k - 1), path.get(k), radius, round).formed(forming);
            if (k + 1 < path.size()) {
                new Ball(path.get(k), radius, round).formed(forming);
            }
        }
        return forming.formed();
    }

    /**
     * A ring round an axle across at {@code middle}: its section {@code thick} from its middle line to its surface,
     * its middle line {@code reach} out from the axle, as a tyre is.
     */
    public Mesh ring(final Vec3 middle, final double reach, final double thick) {
        return ring(middle, reach, thick, thick);
    }

    /**
     * The same, its section oval: {@code across} from its middle line to its sides, {@code out} to its outside and
     * its inside, as a car's tyre is wider than it is deep.
     */
    public Mesh ring(final Vec3 middle, final double reach, final double across, final double out) {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final int sections = 2 * round;
        for (int a = 0; a < sections; a++) {
            final Vec3 way = outward(2 * Math.PI * a / sections);
            for (int b = 0; b < round; b++) {
                final double turn = 2 * Math.PI * b / round;
                final Vec3 at = way.times(out * Math.cos(turn)).plus(new Vec3(across * Math.sin(turn), 0, 0));
                final Vec3 facing = way.times(Math.cos(turn) / out).plus(new Vec3(Math.sin(turn) / across, 0, 0));
                forming.point(middle.plus(way.times(reach)).plus(at), facing);
            }
        }
        for (int a = 0; a < sections; a++) {
            final int here = a * round;
            final int there = ((a + 1) % sections) * round;
            for (int b = 0; b < round; b++) {
                final int next = (b + 1) % round;
                forming.faced(here + b, there + b, there + next);
                forming.faced(here + b, there + next, here + next);
            }
        }
        return forming.formed();
    }

    /**
     * A band bent round an axle across at {@code middle}, {@code reach} out from it to its inside, {@code thick}
     * thick and {@code wide} across, from {@code from} degrees round to {@code to}: nought ahead of the axle, ninety
     * over it. Round all the way, it is a hoop, as a rim is.
     */
    public Mesh band(final Vec3 middle, final double reach, final double thick, final double wide, final double from,
              final double to) {
        final int steps = Math.max(2, (int) Math.ceil(Math.abs(to - from) / 360 * 2 * round));
        final List<List<Vec3>> rings = new ArrayList<>();
        for (int k = 0; k <= steps; k++) {
            final Vec3 out = outward(Math.toRadians(from + (to - from) * k / steps));
            final Vec3 in = middle.plus(out.times(reach));
            final Vec3 outer = middle.plus(out.times(reach + thick));
            final Vec3 side = new Vec3(wide / 2, 0, 0);
            rings.add(List.of(in.minus(side), outer.minus(side), outer.plus(side), in.plus(side)));
        }
        final Forming forming = new Forming(middle);
        new Lofting(forming).lofted(rings);
        return forming.formed();
    }

    /** A body skinned over {@code rings}, each a loop of as many points as the next, its two ends closed. */
    public Mesh lofted(final List<List<Vec3>> rings, final Vec3 middle) {
        final Forming forming = new Forming(middle);
        new Lofting(forming).lofted(rings);
        return forming.formed();
    }

    /** Several shapes as one. */
    public Mesh joined(final List<Mesh> shapes) {
        return new Merged(shapes.stream().map(s -> new Merged.Placed(s, new Mat4())).toList()).mesh();
    }

    /** The way out from an axle across, {@code turn} round from ahead toward up. */
    private Vec3 outward(final double turn) {
        return new Vec3(0, Math.sin(turn), Math.cos(turn));
    }

    private Mesh placed(final Mesh mesh, final Mat4 at) {
        return new Merged(List.of(new Merged.Placed(mesh, at))).mesh();
    }
}
