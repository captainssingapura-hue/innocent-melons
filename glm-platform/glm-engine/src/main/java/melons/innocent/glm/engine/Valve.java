package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * One of a cylinder's valves, laid out as {@link HeadFigures} says, and what works it: the valve, its head seated
 * flush in its chamber's roof, its stem up its guide to its tip, the spring's retainer held on it a little under the
 * tip, and the bucket over the tip that its cam presses on, all moving as one, in titanium as all that turns and runs
 * is designed in; and its spring, a coil of steel wire from its seat on the head up to the retainer, squeezed shorter
 * as the valve opens. Its points: the valve's seat, where it slides in its guide; the spring's seat on the head.
 *
 * @param figures the engine's figures
 * @param bank    its bank, from nought
 * @param k       its cylinder in its bank, from nought at the front
 * @param inlet   an inlet valve, or an exhaust
 * @param side    the front one of its pair (-1) or the rear (1)
 */
record Valve(EngineFigures figures, int bank, int k, boolean inlet, int side) {

    private static final int ROUND = 16;
    /** How thick a valve's head is, its stem, its retainer and its bucket; the spring's coil, wire and turns. */
    private static final double HEAD = 0.2;
    private static final double STEM = 0.3;
    private static final double RETAINER = 1.15;
    private static final double COIL = 1.05;
    private static final double WIRE = 0.16;
    private static final int TURNS = 6;
    /** How far under its tip its retainer is, and the spring's seat on the head. */
    private static final double HELD = 0.8;
    private static final double SEATED = 4.6;

    /** What it is called, after its cylinder and its place: inlet valve 1.2, front. */
    String called() {
        return (inlet ? "inlet" : "exhaust") + " valve " + (bank + 1) + "." + (k + 1) + (side < 0 ? ", front"
                : ", rear");
    }

    /** The valve, with its retainer and its bucket. */
    Part valve() {
        final HeadFigures head = new HeadFigures(figures);
        final Shapes shapes = new Shapes(ROUND);
        final double r = head.valve(inlet) / 2;
        final double stem = head.stem();
        final double cup = head.cam(inlet).minus(head.seat(inlet, 0)).length() - head.journal() * 1.1;
        final List<Mesh> made = new ArrayList<>(List.of(
                shapes.drum(along(0), along(HEAD), r),
                shapes.drum(along(HEAD), along(HEAD + 0.35), r * 0.55),
                shapes.drum(along(0), along(stem), STEM),
                shapes.drum(along(stem - HELD - 0.25), along(stem - HELD), RETAINER),
                shapes.drum(along(stem - 0.3), along(cup), bucket())));
        return new Part(figures.name() + " " + called(), Group.ENGINE, false, List.of(new Point("seat", along(0))),
                shapes.joined(made), new Made(Material.TITANIUM, true)).as(new EngineParts.Valve());
    }

    /** Its spring, from its seat on the head up to the retainer, as fitted with the valve shut. */
    Part spring() {
        final Vec3 a = way();
        final Vec3 b = new Vec3(0, 0, 1);
        final Vec3 c = a.cross(b).unit();
        final List<Vec3> coil = new ArrayList<>();
        final int steps = TURNS * 12;
        for (int i = 0; i <= steps; i++) {
            final double t = 2 * Math.PI * i / 12;
            final Vec3 middle = along(springSeat() + (fitted() - WIRE * 2) * i / steps + WIRE);
            coil.add(middle.plus(b.times(COIL * Math.cos(t))).plus(c.times(COIL * Math.sin(t))));
        }
        return new Part(figures.name() + " " + called().replace("valve", "valve spring"), Group.ENGINE, false,
                List.of(new Point("seat", along(springSeat()))), new Shapes(6).pipe(coil, WIRE),
                new Made(Material.STEEL, true)).as(new EngineParts.ValveSpring());
    }

    /** How far round its bucket is. */
    double bucket() {
        return figures.bore() * 0.17;
    }

    /** Where its seat is, in the block's frame. */
    Vec3 seat() {
        return along(0);
    }

    /** Where its spring sits on the head, in the block's frame. */
    Vec3 springAt() {
        return along(springSeat());
    }

    /** The way up its stem, from its seat to its tip, in the block's frame. */
    Vec3 way() {
        return new HeadFigures(figures).way(bank, new HeadFigures(figures).axis(inlet)).unit();
    }

    /** How far its valve has moved, lifted {@code lift} off its seat. */
    Mat4 lifted(final double lift) {
        return new Mat4(way().times(-lift));
    }

    /** How far its spring has been squeezed, its valve lifted {@code lift}: shorter along its stem, from its seat. */
    Mat4 squeezed(final double lift) {
        final Vec3 a = way();
        final Vec3 o = springAt();
        final double f = lift / fitted();
        final double[] m = new double[16];
        final double[] w = {a.x(), a.y(), a.z()};
        final double[] p = {o.x(), o.y(), o.z()};
        for (int i = 0; i < 3; i++) {
            double moved = 0;
            for (int j = 0; j < 3; j++) {
                m[i * 4 + j] = (i == j ? 1 : 0) - f * w[i] * w[j];
                moved += m[i * 4 + j] * p[j];
            }
            m[i * 4 + 3] = p[i] - moved;
        }
        m[15] = 1;
        return new Mat4(m);
    }

    /** How long its spring is as fitted, the valve shut: from its seat to the retainer's underside. */
    double fitted() {
        return SEATED - HELD - 0.25;
    }

    private double springSeat() {
        return new HeadFigures(figures).stem() - SEATED;
    }

    /** The point {@code s} up its stem from its seat, in the block's frame. */
    private Vec3 along(final double s) {
        final HeadFigures head = new HeadFigures(figures);
        return head.at(bank, k, head.seat(inlet, side).plus(head.axis(inlet).times(s)));
    }
}
