package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.List;

/**
 * The boxer's bodywork, what the bars do not turn: its tank, long and round, astride the top tubes behind the head
 * tube, its filler cap bright on top; its seat, a scooped saddle, and under it a side cover painted as the tank is,
 * filling the frame's triangle there ({@link Saddle}); the back fender, broad, a band curving over the back wheel from
 * ahead of its top round and down behind it; and the tail lamp at the fender's end.
 *
 * @param shapes what it is shaped with
 */
record Bodywork(Shapes shapes) {

    /** How far the back fender stands out over the tyre. */
    private static final double CLEAR = 5;

    /** Its parts. */
    List<Part> parts() {
        return List.of(
                new Part("tank", Part.Slot.TANK, Part.Moves.BODY, shapes.body(new Vec3(0, 74, 16), 30, 22, 66)),
                new Part("filler cap", Part.Slot.BRIGHT, Part.Moves.BODY,
                        shapes.drum(new Vec3(0, 84, 22), new Vec3(0, 86, 22), 3.5)),
                new Part("seat", Part.Slot.SEAT, Part.Moves.BODY, new Saddle(shapes).seat()),
                new Part("side cover", Part.Slot.TANK, Part.Moves.BODY, new Saddle(shapes).cover()),
                new Part("back fender", Part.Slot.FENDERS, Part.Moves.BODY,
                        shapes.band(Boxer.BACK_AXLE, Boxer.WHEEL + CLEAR, 1.4, 22, 35, 160)),
                new Part("tail lamp", Part.Slot.TAIL_LAMP, Part.Moves.BODY,
                        shapes.capsule(new Vec3(0, 62, -112), new Vec3(0, 60, -115), 4)));
    }
}
