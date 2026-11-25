package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * What the bars turn, about the head tube's line ({@code steering}, down it from its top): the forks, a leg each side
 * of the front wheel, parallel to the head tube, from the front axle up to the top yoke, thick sliders below and
 * slimmer stanchions above; the yokes clamping them to the head tube, top and bottom; the axle through the wheel's hub
 * between them; the bars over the top yoke on a riser, swept back to the grips; the headlamp, round, ahead of the
 * yokes; and the front fender over the wheel, between the legs. The fender in the fenders' finish, the grips in the
 * tyres', the lamp's glass the headlamp's; the rest bright.
 *
 * @param shapes   what it is shaped with
 * @param steering the way the head tube's line runs, down from its top
 */
record FrontEnd(Shapes shapes, Vec3 steering) {

    /** How far each leg is out from the middle; how high the top yoke is, and the bottom. */
    private static final double LEG = 10;
    private static final double TOP_YOKE = 88;
    private static final double BOTTOM_YOKE = 70;
    /** How far up a leg its slider reaches from the axle, along it. */
    private static final double SLIDER = 36;
    /** How high the bars are at their middle, and how far back their ends are pulled toward the rider. */
    private static final double BARS = 98;
    private static final double PULL = 16;
    /** How high the headlamp is. */
    private static final double LAMP = 80;

    /** Its parts. */
    List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        final List<Mesh> forks = new ArrayList<>();
        for (final int side : List.of(-1, 1)) {
            final Vec3 foot = Boxer.FRONT_AXLE.plus(new Vec3(side * LEG, 0, 0));
            final Vec3 knee = up(foot, SLIDER);
            forks.add(shapes.capsule(foot, knee, 2.9));
            forks.add(shapes.drum(knee, at(foot, TOP_YOKE + 3), 2));
        }
        for (final double height : List.of(TOP_YOKE, BOTTOM_YOKE)) {
            final Vec3 legs = at(Boxer.FRONT_AXLE, height);
            final Vec3 head = height > Boxer.HEAD_FOOT.y() ? Boxer.HEAD_TOP : Boxer.HEAD_FOOT;
            forks.add(shapes.box(legs.plus(head).times(0.5), 2 * LEG + 6, 4, Math.abs(head.z() - legs.z()) + 6));
        }
        forks.add(shapes.drum(Boxer.FRONT_AXLE.plus(new Vec3(-LEG - 1, 0, 0)),
                Boxer.FRONT_AXLE.plus(new Vec3(LEG + 1, 0, 0)), 1.2));
        parts.add(steered("forks", Part.Slot.BRIGHT, shapes.joined(forks)));
        parts.addAll(bars());
        final double lamp = at(Boxer.FRONT_AXLE, LAMP).z() + 4;
        parts.add(steered("headlamp shell", Part.Slot.BRIGHT,
                shapes.drum(new Vec3(0, LAMP, lamp), new Vec3(0, LAMP, lamp + 8), 9)));
        parts.add(steered("headlamp", Part.Slot.HEADLAMP,
                shapes.drum(new Vec3(0, LAMP, lamp + 8), new Vec3(0, LAMP, lamp + 9), 8)));
        parts.add(steered("front fender", Part.Slot.FENDERS,
                shapes.band(Boxer.FRONT_AXLE, Boxer.WHEEL + 4, 1.2, 13, -20, 125)));
        return parts;
    }

    /** The bars, on a riser over the top yoke, swept back to a grip at each end. */
    private List<Part> bars() {
        final Vec3 riser = at(Boxer.FRONT_AXLE, TOP_YOKE + 2);
        final List<Mesh> bars = new ArrayList<>();
        final List<Mesh> grips = new ArrayList<>();
        final Vec3 top = new Vec3(0, BARS, riser.z() - 3);
        bars.add(shapes.drum(riser, top, 2));
        final Vec3 left = top.plus(new Vec3(-36, 3, -PULL));
        final Vec3 right = top.plus(new Vec3(36, 3, -PULL));
        final Vec3 bendLeft = top.plus(new Vec3(-14, 0, 0));
        final Vec3 bendRight = top.plus(new Vec3(14, 0, 0));
        bars.add(shapes.pipe(List.of(left, bendLeft, bendRight, right), 1.4));
        for (final List<Vec3> end : List.of(List.of(bendLeft, left), List.of(bendRight, right))) {
            final Vec3 from = end.getFirst();
            final Vec3 run = end.getLast().minus(from);
            grips.add(shapes.capsule(from.plus(run.times(0.6)), from.plus(run.times(1.04)), 1.9));
        }
        return List.of(steered("bars", Part.Slot.BRIGHT, shapes.joined(bars)),
                steered("grips", Part.Slot.TYRES, shapes.joined(grips)));
    }

    /** Where a leg through {@code foot} is, parallel to the head tube, as high as {@code height}. */
    private Vec3 at(final Vec3 foot, final double height) {
        return up(foot, (height - foot.y()) / -steering.y());
    }

    /** {@code by} up a leg from {@code foot}, against the way the head tube's line runs down. */
    private Vec3 up(final Vec3 foot, final double by) {
        return foot.minus(steering.times(by));
    }

    private Part steered(final String name, final Part.Slot slot, final Mesh form) {
        return new Part(name, slot, Part.Moves.STEERED, form);
    }
}
