package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * The boxer's engine, round as a boxer's is: its crankcase a big round block filling the frame, from over the cradle
 * up to under the tank, the gearbox a smaller one behind it, out toward the loop; out to either side, ahead of the
 * rider's knees, a cylinder lying flat, a drum, and on its end its head, round and wider than it, standing out past
 * the frame; and at the back wheel, on the right, the shaft's drive, round about the axle. All in the engine's
 * finish.
 *
 * @param shapes what it is shaped with
 */
record Engine(Shapes shapes) {

    /** Where the cylinders' line runs across, how far along and how high; how round they are. */
    private static final double BORE_ALONG = 24;
    private static final double BORE_UP = 38;
    private static final double BORE = 7.5;
    /** Where each cylinder begins, out from the middle, and ends, where its head begins. */
    private static final double FROM = 11;
    private static final double TO = 33;

    /** Its parts: the crankcase with its gearbox, a cylinder with its head each side, and the shaft's drive. */
    List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        parts.add(part("crankcase", shapes.joined(List.of(
                shapes.body(new Vec3(0, 37, 10), 30, 48, 62),
                shapes.body(new Vec3(0, 38, -14), 26, 36, 30)))));
        for (final int side : List.of(-1, 1)) {
            parts.add(part(side < 0 ? "left cylinder" : "right cylinder", shapes.joined(List.of(
                    shapes.drum(new Vec3(side * FROM, BORE_UP, BORE_ALONG), new Vec3(side * TO, BORE_UP, BORE_ALONG),
                            BORE),
                    shapes.body(new Vec3(side * (TO + 4), BORE_UP + 1, BORE_ALONG - 1), 10, 19, 22)))));
        }
        parts.add(part("shaft drive", shapes.body(Boxer.BACK_AXLE.plus(new Vec3(12, 0, 0)), 10, 16, 16)));
        return parts;
    }

    private Part part(final String name, final Mesh form) {
        return new Part(name, Part.Slot.ENGINE, Part.Moves.BODY, form);
    }
}
