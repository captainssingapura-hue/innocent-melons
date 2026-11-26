package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * The boxer's wheels, each as {@link Boxer#WHEEL} round as the bike's figures say: a tyre round in section, its
 * outside on the ground under its axle, the back one fatter; inside it a rim, a hoop; a hub on the axle, and
 * {@link #SPOKES} spokes, cast with it, from the hub out to the rim; on the front wheel a brake's disc beside its hub,
 * on the left. The tyre in the tyres' finish, the disc bright, the rest the wheels'. Each turns as its wheel rolls,
 * the front one as it is steered too.
 *
 * @param shapes what it is shaped with
 */
record Wheels(Shapes shapes) {

    /** How many spokes a wheel has; how far out the rim's inside is, how thick and how wide it is. */
    private static final int SPOKES = 5;
    private static final double RIM = 15.5;
    private static final double RIM_THICK = 2;
    private static final double RIM_WIDE = 8;
    /** How far across the hub reaches either side of the middle, and how round it is. */
    private static final double HUB = 7;
    private static final double HUB_ROUND = 6;

    /** Its parts: each wheel's tyre and its wheel, and the front one's disc. */
    List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        parts.addAll(wheel("front", Boxer.FRONT_AXLE, 7, Part.Moves.FRONT_WHEEL));
        parts.addAll(wheel("back", Boxer.BACK_AXLE, 8, Part.Moves.BACK_WHEEL));
        parts.add(new Part("front brake disc", Part.Slot.BRIGHT, Part.Moves.FRONT_WHEEL, shapes.drum(
                Boxer.FRONT_AXLE.plus(new Vec3(-HUB - 2, 0, 0)), Boxer.FRONT_AXLE.plus(new Vec3(-HUB - 1.4, 0, 0)),
                13)));
        return parts;
    }

    /** A wheel at {@code axle}, its tyre {@code thick} from its middle line out, turning as {@code moves}. */
    private List<Part> wheel(final String which, final Vec3 axle, final double thick, final Part.Moves moves) {
        final List<Mesh> wheel = new ArrayList<>();
        wheel.add(shapes.band(axle, RIM, RIM_THICK, RIM_WIDE, 0, 360));
        wheel.add(shapes.drum(axle.plus(new Vec3(-HUB, 0, 0)), axle.plus(new Vec3(HUB, 0, 0)), HUB_ROUND));
        for (int k = 0; k < SPOKES; k++) {
            final double turn = Math.toRadians(90 + 360.0 * k / SPOKES);
            final Vec3 out = new Vec3(0, Math.sin(turn), Math.cos(turn));
            wheel.add(shapes.pipe(List.of(axle.plus(out.times(HUB_ROUND - 1)), axle.plus(out.times(RIM + 0.5))),
                    1.3));
        }
        return List.of(
                new Part(which + " tyre", Part.Slot.TYRES, moves, shapes.ring(axle, Boxer.WHEEL - thick, thick)),
                new Part(which + " wheel", Part.Slot.WHEELS, moves, shapes.joined(wheel)));
    }
}
