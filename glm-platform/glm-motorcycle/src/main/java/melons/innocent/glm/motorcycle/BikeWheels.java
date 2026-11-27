package melons.innocent.glm.motorcycle;

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
import melons.innocent.glm.engine.BeltFigures;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;
import melons.innocent.vehiclelab.wheels.SpokedRim;
import melons.innocent.vehiclelab.wheels.Tyre;
import melons.innocent.vehiclelab.wheels.WheelSize;

import java.util.ArrayList;
import java.util.List;

/**
 * A motorcycle's wheels, as its figures place them ({@link BikeFigures}): each a cast wheel of round spokes from the
 * wheels lab, its tyre round in section on its rim, turning on its axle across the bike; and its brakes, a disc on
 * each wheel's left, 320 mm in front and 260 mm behind, as the reference bike's, and each disc's caliper, the front's
 * four pistons on the fork's left leg behind the axle, the back's one on the swingarm's left arm under it.
 *
 * @param figures the bike's figures
 */
record BikeWheels(BikeFigures figures) {

    private static final int ROUND = 48;
    /** How far round each disc is, and its hole; how thick a disc is; how far to the left of the middle it is. */
    private static final double FRONT_DISC = 16;
    private static final double REAR_DISC = 13;
    private static final double DISC_THICK = 0.5;
    private static final double DISC_LEFT = 6.5;
    /** How far round an axle is. */
    private static final double AXLE = 1.25;

    /** The wheel in front, or behind. */
    Part wheel(final boolean front) {
        final WheelSize size = size(front);
        final Shapes shapes = new Shapes(ROUND);
        return new Part(called(front, "wheel"), Group.WHEELS, false, List.of(new Point("hub", axle(front))),
                placed(new SpokedRim(shapes).mesh(size, 0), front), new Made(Material.ALUMINIUM, false))
                .as(new MotorcycleParts.CastWheel());
    }

    /** The tyre in front, or behind. */
    Part tyre(final boolean front) {
        return new Part(called(front, "tyre"), Group.TYRES, false, List.of(new Point("rim", axle(front))),
                placed(new Tyre(size(front), new Shapes(ROUND)).mesh(), front), new Made(Material.RUBBER, true))
                .as(new MotorcycleParts.Tyre());
    }

    /**
     * The axle in front, or behind: across, through the fork's feet or the swingarm's ends, {@code across} between
     * them; behind, through the rear pulley's hub too, in the belt's plane.
     */
    Part axle(final boolean front, final double across) {
        final Vec3 middle = axle(front);
        final Vec3 left = middle.plus(new Vec3(0, 0, -across / 2));
        final Vec3 right = middle.plus(new Vec3(0, 0, across / 2));
        final Vec3 pulley = new Vec3(middle.x(), middle.y(), new BeltFigures(figures.engine()).plane());
        return new Part(called(front, "axle"), Group.WHEELS, false, List.of(new Point("hub", middle),
                new Point("left", left), new Point("right", right), new Point("pulley", pulley)),
                new Shapes(16).drum(left.minus(new Vec3(0, 0, 1.5)), right.plus(new Vec3(0, 0, 1.5)), AXLE),
                new Made(Material.STEEL, true)).as(new MotorcycleParts.Axle());
    }

    /** The brake's disc in front, or behind, on the wheel's left. */
    Part disc(final boolean front) {
        final Outlines outlines = new Outlines(ROUND);
        final double round = front ? FRONT_DISC : REAR_DISC;
        final Vec3 at = disc(front, 0);
        return new Part(called(front, "brake disc"), Group.BRAKES, false, List.of(new Point("hub", axle(front))),
                new Plate(outlines.circle(new Spot(at.x(), at.y()), round), List.of(outlines.circle(
                        new Spot(at.x(), at.y()), round * 0.55)), new Vec3(0, 0, at.z()), new Vec3(1, 0, 0),
                        new Vec3(0, 1, 0), DISC_THICK).mesh(), new Made(Material.IRON, true))
                .as(new MotorcycleParts.BrakeDisc());
    }

    /**
     * The brake's caliper in front, or behind, gripping its disc's rim: a pad either side of the disc, a little off
     * it, bridged outside its rim; in front behind the axle and over it, behind under it.
     */
    Part caliper(final boolean front) {
        final double round = front ? FRONT_DISC : REAR_DISC;
        final double angle = Math.toRadians(front ? -65 : 200);
        final Vec3 way = new Vec3(Math.sin(angle), Math.cos(angle), 0);
        final Vec3 pad = disc(front, 0).plus(way.times(round - 2));
        final Vec3 bridge = disc(front, 0).plus(way.times(round + 1.6));
        final Vec3 along = new Vec3(0, 0, 1);
        final double near = DISC_THICK / 2 + 0.3;
        final double far = near + 1.6;
        final Shapes shapes = new Shapes(12);
        final List<Mesh> made = new ArrayList<>(List.of(
                shapes.drum(bridge.minus(along.times(far)), bridge.plus(along.times(far)), 1.1)));
        for (final int side : List.of(-1, 1)) {
            made.add(shapes.drum(pad.plus(along.times(side * near)), pad.plus(along.times(side * far)), 2.2));
            made.add(shapes.drum(pad.plus(along.times(side * (near + far) / 2)),
                    bridge.plus(along.times(side * (near + far) / 2)), 0.7));
        }
        final Vec3 mount = front ? axle(true).plus(new Vec3(0, 0, -BikeFigures.LEGS / 2)) : axle(false);
        return new Part(called(front, "brake caliper"), Group.CALIPERS, false, List.of(new Point("mount", mount)),
                shapes.joined(made), new Made(Material.ALUMINIUM, false)).as(new MotorcycleParts.BrakeCaliper());
    }

    /** The middle of the axle in front, or behind. */
    Vec3 axle(final boolean front) {
        return front ? figures.frontAxle() : figures.rearAxle();
    }

    /** Where the disc in front, or behind, is, on the wheel's left, {@code out} further out than its middle. */
    Vec3 disc(final boolean front, final double out) {
        return axle(front).plus(new Vec3(0, 0, -DISC_LEFT - out));
    }

    /** {@code mesh}, made round the wheels lab's axis across, turned to roll about the bike's, at its axle. */
    private Mesh placed(final Mesh mesh, final boolean front) {
        final Mat4 at = new Mat4(axle(front)).times(new Turned(new Vec3(0, 1, 0), -90).matrix());
        return new Merged(List.of(new Merged.Placed(mesh, at))).mesh();
    }

    private WheelSize size(final boolean front) {
        return front ? BikeFigures.FRONT : BikeFigures.REAR;
    }

    private String called(final boolean front, final String what) {
        return (front ? "front " : "rear ") + what;
    }
}
