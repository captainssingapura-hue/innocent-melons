package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * A motorcycle's subframe, as the reference bike's runs: a rail each side, aluminium tube, up a down tube from where
 * it is bolted to the mid-structure behind the back head, back level under the seat's front, over the side covers;
 * up a step ahead of the back wheel, under the seat's back; and level again back over the wheel to the tail. The seat
 * rests on the rails all along; their front run as high as the seat's pan sits, as low as the reference bike's rider
 * sits, 70.5 cm off the ground, over the engine where it reaches higher under the seat, as the bigger twin's back head
 * does. Tubes across at its foot, at the rails' front, at the shocks and at the tail; and a tower each side, down and
 * out from the rail to the shock's top. Its places estimates, from the reference bike's pictures.
 *
 * @param figures the bike's figures
 * @param mount   where it is bolted to the mid-structure, in the middle across
 * @param beneath how high the engine reaches under the seat, which the seat's pan clears
 */
record Subframe(BikeFigures figures, Vec3 mount, double beneath) {

    private static final int ROUND = 16;
    /** How far round a rail is; how far out from the middle the rails are. */
    static final double RAIL = 1.3;
    static final double RAILS = 13;
    /**
     * How high the seat's pan is at least, over the ground, the reference bike's rider sitting 70.5 cm off it; how
     * far the pan clears the engine.
     */
    private static final double PAN = 64.5;
    private static final double CLEARS = 1.5;
    /** How far the rails step up; where the step starts and ends, ahead of the back axle; where the tail is, behind. */
    private static final double STEP = 7;
    private static final double STEP_FROM = 37.5;
    private static final double STEP_TO = 21;
    private static final double TAIL = 15;
    /** How far ahead of the back axle the shocks' tops are, and how far under the rails. */
    private static final double SHOCK = 18;
    private static final double TOWER = 11;

    /** The subframe. */
    Part part() {
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>();
        for (final int side : List.of(-1, 1)) {
            made.add(shapes.pipe(path(side), RAIL));
            made.add(shapes.drum(across(mount, side * RAILS), across(head(), side * RAILS), RAIL));
            made.add(shapes.drum(across(shock(), side * RAILS), shockTop(side), RAIL));
        }
        for (final Vec3 at : List.of(mount, head(), shock(), tail())) {
            made.add(shapes.drum(across(at, -RAILS), across(at, RAILS), RAIL * 0.8));
        }
        return new Part("subframe", Group.FRAME, false, List.of(new Point("front", front()), new Point("tail", tail()),
                new Point("shock, left", shockTop(-1)), new Point("shock, right", shockTop(1))),
                shapes.joined(made), new Made(Material.ALUMINIUM, false)).as(new MotorcycleParts.Subframe());
    }

    /** Its front, where it is bolted to the mid-structure, in the middle across. */
    Vec3 front() {
        return mount;
    }

    /** Where its rails begin over where it is bolted, up its down tubes, in the middle across. */
    private Vec3 head() {
        return new Vec3(mount.x(), low(), figures.centre());
    }

    /** The tail, where the rails end behind the back axle, in the middle across. */
    Vec3 tail() {
        return new Vec3(axle().x() - TAIL, high(), figures.centre());
    }

    /** How high the rails' tops are at {@code x} along the bike, where the seat's pan rests on them. */
    double top(final double x) {
        return rail(x) + RAIL;
    }

    /** Where the shock's top on side {@code side} is, -1 the left: under its tower, outside the swingarm's arm. */
    Vec3 shockTop(final int side) {
        return new Vec3(axle().x() + SHOCK, high() - TOWER, figures.centre() + side * (Frames.ARMS + 2));
    }

    /** How high the rails' middles are at {@code x} along the bike: level, up the step, and level again. */
    private double rail(final double x) {
        final double start = axle().x() + STEP_FROM;
        final double end = axle().x() + STEP_TO;
        final double t = Math.clamp((start - x) / (start - end), 0, 1);
        return low() + STEP * t;
    }

    /** The rail on side {@code side}: level from the front, up the step, level to the tail. */
    private List<Vec3> path(final int side) {
        final List<Vec3> path = new ArrayList<>();
        for (final double x : List.of(mount.x(), axle().x() + STEP_FROM, axle().x() + STEP_TO, tail().x())) {
            path.add(new Vec3(x, rail(x), figures.centre() + side * RAILS));
        }
        return path;
    }

    /** How high the front run's middle is: its top where the seat's pan is. */
    private double low() {
        return Math.max(figures.ground() + PAN, beneath + CLEARS) - RAIL;
    }

    private double high() {
        return low() + STEP;
    }

    /** Over the shocks, on the rails, in the middle across. */
    private Vec3 shock() {
        return new Vec3(axle().x() + SHOCK, high(), figures.centre());
    }

    private Vec3 axle() {
        return figures.rearAxle();
    }

    private Vec3 across(final Vec3 p, final double out) {
        return new Vec3(p.x(), p.y(), figures.centre() + out);
    }
}
