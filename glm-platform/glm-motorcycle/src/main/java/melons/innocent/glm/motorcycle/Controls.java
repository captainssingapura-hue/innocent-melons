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
 * What the rider steers and rides a motorcycle with, at its bars, all turning with the front end: the handlebar, an
 * inch round, on two risers up from the top yoke, across and swept back and out to its grips, some 84 cm across them,
 * as the reference bike's is; a rubber grip on each end; the clutch's lever ahead of the left grip and the front
 * brake's ahead of the right, pivoting on its master cylinder, clamped on the bar beside it; a mirror on a stalk
 * either side; and the round gauge over the bar's middle, facing the rider. Its places estimates.
 *
 * @param figures the bike's figures
 */
record Controls(BikeFigures figures) {

    private static final int ROUND = 16;
    /** How far round the bar is, a grip, a lever; how far up the steering axis its risers stand over the top yoke. */
    private static final double BAR = 1.27;
    private static final double GRIP = 1.7;
    private static final double LEVER = 0.55;
    private static final double RISERS = 8;
    /** How far apart the risers are; how far out its sweep, the grip's inner end and its outer end are. */
    private static final double RISER = 4;
    private static final double MIDDLE = 9;
    private static final double SWEEP = 22;
    private static final double INNER = 29;
    private static final double OUTER = 42;

    /** The handlebar and its risers. */
    Part handlebar() {
        final Shapes shapes = new Shapes(ROUND);
        final List<Vec3> path = new ArrayList<>();
        for (final int side : List.of(-1, 1)) {
            final List<Vec3> half = List.of(at(-16, 1, OUTER * side), at(-14, 2, INNER * side),
                    at(-8, 3, SWEEP * side), at(0, 0, MIDDLE * side));
            path.addAll(side < 0 ? half : half.reversed());
        }
        final List<Mesh> made = new ArrayList<>(List.of(shapes.pipe(path, BAR)));
        for (final int side : List.of(-1, 1)) {
            made.add(shapes.drum(yoke().plus(across(side * RISER)), clamp().plus(across(side * RISER)), 1.5));
        }
        return new Part("handlebar", Group.STEERING, false, List.of(new Point("yoke", yoke()),
                new Point("grip, left", at(-14, 2, -INNER)), new Point("grip, right", at(-14, 2, INNER))),
                shapes.joined(made), new Made(Material.STEEL, false)).as(new MotorcycleParts.Handlebar());
    }

    /** The grip on side {@code side}, -1 the left; where the rider's hand holds it, halfway along it. */
    Part grip(final int side) {
        final Vec3 inner = at(-14, 2, INNER * side);
        final Vec3 outer = at(-16, 1, (OUTER + 0.5) * side);
        return new Part("grip, " + called(side), Group.TRIM, false, List.of(new Point("bar", inner),
                new Point(Riding.HOLDS, inner.plus(outer).times(0.5))), new Shapes(ROUND).drum(inner, outer, GRIP),
                new Made(Material.RUBBER, true)).as(new MotorcycleParts.HandGrip());
    }

    /**
     * The lever on side {@code side}, ahead of the bar where it sweeps forward: the clutch's on the left, on its perch
     * on the bar; the front brake's on the right, pivoting on its master cylinder.
     */
    Part lever(final int side) {
        final Shapes shapes = new Shapes(ROUND);
        final Vec3 pivot = at(-8.5, 3.8, (INNER - 2) * side);
        final Vec3 end = at(-11.5, 1.2, (OUTER - 1) * side);
        final Vec3 perch = side < 0 ? at(-14, 2, (INNER - 2) * side) : at(-12, 4.8, INNER - 2);
        return new Part((side < 0 ? "clutch" : "brake") + " lever", Group.STEERING, false, List.of(
                new Point("bar", at(-14, 2, INNER * side))), shapes.joined(List.of(shapes.capsule(pivot, end, LEVER),
                shapes.drum(perch, pivot, 0.7))), new Made(Material.ALUMINIUM, false))
                .as(new MotorcycleParts.HandLever());
    }

    /** The front brake's master cylinder, on the bar inside the right grip. */
    Part masterCylinder() {
        return new Part("front master cylinder", Group.STEERING, false, List.of(new Point("bar", at(-14, 2,
                INNER))), new Shapes(ROUND).box(at(-13, 4.2, INNER - 3.5), 3, 2.6, 4),
                new Made(Material.ALUMINIUM, false)).as(new MotorcycleParts.MasterCylinder());
    }

    /** The mirror on side {@code side}: a stalk up from the bar, its glass round, facing back. */
    Part mirror(final int side) {
        final Shapes shapes = new Shapes(ROUND);
        final Vec3 foot = at(-9, 3.5, (SWEEP + 2) * side);
        final Vec3 head = at(-12, 22, (SWEEP + 9) * side);
        return new Part("mirror, " + called(side), Group.STEERING, false, List.of(new Point("bar", at(-14, 2,
                INNER * side))), shapes.joined(List.of(shapes.capsule(foot, head, 0.5),
                shapes.drum(head.plus(new Vec3(1, 0, 0)), head.minus(new Vec3(1, 0, 0)), 5))),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.Mirror());
    }

    /** The gauge, round, over the bar's middle, tilted to face the rider. */
    Part gauge() {
        final Vec3 middle = at(2, 8, 0);
        final Vec3 facing = new Vec3(-0.8, 0.6, 0);
        return new Part("gauge", Group.STEERING, false, List.of(new Point("yoke", yoke())),
                new Shapes(ROUND * 2).drum(middle.minus(facing.times(2)), middle.plus(facing.times(1.5)), 5.5),
                new Made(Material.PLASTIC, false)).as(new MotorcycleParts.Gauge());
    }

    /** Where the risers stand on the top yoke, in the middle. */
    Vec3 yoke() {
        return figures.stem(BikeFigures.HIGH_YOKE + 1.75);
    }

    /** Where the bar is clamped on the risers, in the middle. */
    private Vec3 clamp() {
        return figures.stem(BikeFigures.HIGH_YOKE + 1.75 + RISERS);
    }

    /** The place {@code back} along the bike from the clamp, {@code up} over it, {@code out} across from the middle. */
    private Vec3 at(final double back, final double up, final double out) {
        return clamp().plus(new Vec3(back, up, out));
    }

    private Vec3 across(final double out) {
        return new Vec3(0, 0, out);
    }

    private String called(final int side) {
        return side < 0 ? "left" : "right";
    }
}
