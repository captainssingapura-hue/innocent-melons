package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A bank's inlet or exhaust camshaft, in steel: a shaft along the head over its valves, a journal in each of the head's
 * towers, at the front the wheel its chain drives it by, and a lobe under each valve's bucket, its base circle round
 * and its nose lifting the valve as {@link ValveTiming} says. Turned by the crank's chain at half the crank's speed the
 * same way round, so each lobe's nose stands toward its valve when its cylinder's cycle wants that valve open the most.
 * Its point: its axis at the head's front, where it turns in the head.
 *
 * @param figures the engine's figures
 * @param bank    its bank, from nought
 * @param inlet   the inlets' camshaft, or the exhausts'
 */
record Camshaft(EngineFigures figures, int bank, boolean inlet) {

    private static final int ROUND = 20;
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How many points round a lobe is drawn in; how wide it is; how wide a journal; how big the front wheel. */
    private static final int LOBE = 90;
    private static final double LOBE_WIDE = 1.5;
    private static final double JOURNAL_WIDE = 1.8;
    static final double WHEEL = 3.2;

    /** What it is called. */
    String called() {
        return figures.name() + " " + (inlet ? "inlet" : "exhaust") + " camshaft"
                + (figures.banks() == 2 ? ", bank " + (bank + 1) : "");
    }

    /** It. */
    Part part() {
        final HeadFigures head = new HeadFigures(figures);
        final Shapes shapes = new Shapes(ROUND);
        final double front = head.front(bank);
        final double rear = head.rear(bank);
        final List<Mesh> made = new ArrayList<>();
        made.add(shapes.drum(axis(front - 2.5), axis(rear - 0.5), head.journal() * 0.75));
        made.add(shapes.drum(axis(front - 2.5), axis(front - 1.2), WHEEL));
        for (final double z : List.of(front + 1, rear - 1)) {
            made.add(shapes.drum(axis(z - JOURNAL_WIDE / 2), axis(z + JOURNAL_WIDE / 2), head.journal()));
        }
        for (int k = 0; k + 1 < figures.perBank(); k++) {
            final double z = figures.along(bank, k) + figures.spacing() / 2;
            made.add(shapes.drum(axis(z - JOURNAL_WIDE / 2), axis(z + JOURNAL_WIDE / 2), head.journal()));
        }
        for (int k = 0; k < figures.perBank(); k++) {
            for (final int side : List.of(-1, 1)) {
                made.add(lobe(new Valve(figures, bank, k, inlet, side)));
            }
        }
        return new Part(called(), Group.ENGINE, false, List.of(new Point("front", axis(front))), shapes.joined(made),
                new Made(Material.STEEL, true));
    }

    /** How far round its lobes' base circle is: no lift. */
    double base() {
        return new HeadFigures(figures).journal() * 1.1;
    }

    /**
     * How far its lobe for {@code valve} reaches toward that valve with the crank turned {@code turned} degrees on
     * from rest: its base circle, and as far as the valve is then lifted.
     */
    double reach(final Valve valve, final double turned) {
        final double toward = degrees(valve.way().times(-1)) - (nose(valve) + turned / 2);
        return base() + new ValveTiming(figures).lifted(inlet, 2 * toward);
    }

    /** How far it has turned with the crank turned {@code turned} degrees on from rest: half as far, about its axis. */
    Mat4 turned(final double turned) {
        final Vec3 c = axis(0);
        return new Mat4(c).times(new Turned(ALONG, -turned / 2).matrix()).times(new Mat4(c.times(-1)));
    }

    /**
     * Which way {@code valve}'s lobe's nose stands at rest, in degrees from straight up toward +x: so that, turned
     * half as far as the crank, it stands toward the valve as its cylinder's cycle wants it open the most.
     */
    private double nose(final Valve valve) {
        final double most = new Firing(figures).fires(bank, valve.k()) + new ValveTiming(figures).peak(inlet);
        return degrees(valve.way().times(-1)) - most / 2;
    }

    /** The lobe under {@code valve}'s bucket, its nose where it stands at rest. */
    private Mesh lobe(final Valve valve) {
        final double nose = nose(valve);
        final List<Spot> outline = new ArrayList<>();
        for (int j = 0; j < LOBE; j++) {
            final double off = 360.0 * j / LOBE;
            final double r = base() + new ValveTiming(figures).lifted(inlet, 2 * off);
            final double a = Math.toRadians(nose + off);
            outline.add(new Spot(r * Math.sin(a), r * Math.cos(a)));
        }
        return new Plate(outline, List.of(), axis(valve.seat().z()), new Vec3(1, 0, 0), new Vec3(0, 1, 0),
                LOBE_WIDE).mesh();
    }

    /** The point on its axis {@code z} along the crank, in the block's frame. */
    private Vec3 axis(final double z) {
        final HeadFigures head = new HeadFigures(figures);
        final Vec3 cam = head.cam(inlet);
        return head.at(bank, 0, new Vec3(cam.x(), cam.y(), z - figures.along(bank, 0)));
    }

    /** Which way {@code v} points across the crank, in degrees from straight up toward +x. */
    private double degrees(final Vec3 v) {
        return Math.toDegrees(Math.atan2(v.x(), v.y()));
    }
}
