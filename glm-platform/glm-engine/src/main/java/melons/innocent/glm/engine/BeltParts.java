package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.glm.existences.Transmission;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A twin's final drive's parts, as its figures say ({@link BeltFigures}): the output pulley, steel, on the
 * countershaft's end out of the crankcase, flanged either side of the belt; the belt, its teeth all alike, each a
 * span of its back with a tooth on its inside, as many as it has; and the rear pulley, steel, on its hub and the back
 * axle, where the back wheel will be. The belt is drawn as its teeth, each where it has run to as the bike rolls.
 *
 * @param figures the engine's figures
 */
record BeltParts(EngineFigures figures) {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    private static final int ROUND = 24;
    /** How thick the belt's back is, and its teeth; how far a flange stands past the pulley's teeth. */
    private static final double BACK = 0.35;
    private static final double TOOTH = 0.3;
    private static final double FLANGE = 0.5;

    /** The output pulley. */
    Part output() {
        final BeltFigures belt = new BeltFigures(figures);
        final Spot shaft = belt.countershaft();
        final Vec3 at = new Vec3(shaft.u(), shaft.v(), belt.plane());
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>(flanged(BeltFigures.FRONT, belt.front(), at));
        made.add(shapes.drum(new Vec3(shaft.u(), shaft.v(), figures.length()), at.plus(ALONG.times(1.6)), 1.6));
        return new Part(figures.name() + " output pulley", Group.ENGINE, false, List.of(new Point("countershaft",
                new Vec3(shaft.u(), shaft.v(), figures.length())), new Point("belt", at)), shapes.joined(made),
                new Made(Material.STEEL, true)).as(new Transmission.OutputPulley());
    }

    /** The rear pulley, on its hub and the back axle. */
    Part rear() {
        final BeltFigures belt = new BeltFigures(figures);
        final Spot axle = belt.axle();
        final Vec3 at = new Vec3(axle.u(), axle.v(), belt.plane());
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>(flanged(BeltFigures.REAR, belt.rear(), at));
        made.add(shapes.drum(at.minus(ALONG.times(5)), at.minus(ALONG.times(1.4)), 5));
        made.add(shapes.drum(at.minus(ALONG.times(14)), at.plus(ALONG.times(2.5)), 1.2));
        return new Part(figures.name() + " rear pulley", Group.ENGINE, false, List.of(new Point("axle", at)),
                shapes.joined(made), new Made(Material.STEEL, true)).as(new MotorcycleParts.RearPulley());
    }

    /** The belt, its teeth where they are at rest. */
    Part belt() {
        final BeltFigures belt = new BeltFigures(figures);
        final Mesh tooth = tooth();
        final Spot shaft = belt.countershaft();
        final Spot axle = belt.axle();
        return new Part(figures.name() + " drive belt", Group.ENGINE, false, List.of(
                new Point("front", new Vec3(shaft.u(), shaft.v(), belt.plane())),
                new Point("rear", new Vec3(axle.u(), axle.v(), belt.plane()))),
                new Merged(places(0).stream().map(m -> new Merged.Placed(tooth, m)).toList()).mesh(),
                new Made(Material.RUBBER, true)).as(new MotorcycleParts.DriveBelt());
    }

    /** One of the belt's teeth, in its own frame: its back along +x from nought, its tooth inside it, toward +y. */
    Mesh tooth() {
        final double pitch = BeltFigures.PITCH;
        final Shapes shapes = new Shapes(8);
        return shapes.joined(List.of(
                shapes.box(new Vec3(pitch / 2, -BACK / 2, 0), pitch * 0.98, BACK, BeltFigures.WIDE),
                shapes.box(new Vec3(pitch / 2, TOOTH / 2, 0), pitch * 0.5, TOOTH, BeltFigures.WIDE)));
    }

    /** Where each of the belt's teeth is, the bike having rolled {@code rolled} forward, in the block's frame. */
    List<Mat4> places(final double rolled) {
        final BeltFigures figured = new BeltFigures(figures);
        final Belt line = figured.belt();
        final double pitch = line.length() / BeltFigures.TEETH;
        final double run = -figured.rear() * figured.wheel(rolled);
        final List<Mat4> places = new ArrayList<>();
        for (int i = 0; i < BeltFigures.TEETH; i++) {
            final Belt.On on = line.at(i * pitch + run);
            final Belt.On next = line.at((i + 1) * pitch + run);
            final double heading = Math.atan2(next.at().v() - on.at().v(), next.at().u() - on.at().u());
            places.add(new Mat4(new Vec3(on.at().u(), on.at().v(), figured.plane()))
                    .times(new Turned(ALONG, Math.toDegrees(heading)).matrix()));
        }
        return places;
    }

    /** A pulley of {@code teeth}, its belt's line {@code round}, at {@code at}, with a flange either side. */
    private List<Mesh> flanged(final int teeth, final double round, final Vec3 at) {
        final Shapes shapes = new Shapes(ROUND * 2);
        final double side = BeltFigures.WIDE / 2 + 0.15;
        return List.of(new Sprocket(teeth, round, BeltFigures.WIDE + 0.2).mesh(at, 0),
                shapes.drum(at.minus(ALONG.times(side + 0.2)), at.minus(ALONG.times(side)), round + FLANGE),
                shapes.drum(at.plus(ALONG.times(side)), at.plus(ALONG.times(side + 0.2)), round + FLANGE));
    }
}
