package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.List;
import java.util.Map;

/**
 * A number plate, as the era's were, 520 by 110 mm, a plate's own size, not made larger with the car: pressed
 * aluminium, white, nothing on it; in a black plastic frame a little larger than it, standing off what holds it, the
 * front bumper's face or the tail panel between the tail lamps; facing ahead or behind as {@code facing} says.
 *
 * @param name   what it is called
 * @param middle where its middle is, on the face of what holds it
 * @param facing which way it faces along, ahead if more than nought
 */
record NumberPlate(String name, Vec3 middle, double facing) {

    private static final Making PRESSED = new Making(new Made(Material.ALUMINIUM, false));
    private static final Making MOULDED = new Making(new Made(Material.PLASTIC, true));
    /** How wide and high it is; how much larger its frame; how thick each is. */
    private static final double WIDE = 52;
    private static final double HIGH = 11;
    private static final double FRAME = 0.7;
    private static final double THICK = 0.15;
    private static final double FRAME_THICK = 0.3;

    /** Its frame fixed to {@code holder} at its point {@code at}, and the plate in its frame. */
    List<Connection> joined(final Part holder, final String at) {
        final double ahead = Math.signum(facing);
        final Part frame = MOULDED.part(name + " frame", Group.TRIM, false, Map.of("holder", middle),
                new Shapes(4).box(middle.plus(new Vec3(0, 0, ahead * FRAME_THICK / 2)), WIDE + 2 * FRAME,
                        HIGH + 2 * FRAME, FRAME_THICK));
        final Part plate = PRESSED.part(name, Group.PLATES, false, Map.of("frame", middle),
                new Shapes(4).box(middle.plus(new Vec3(0, 0, ahead * (FRAME_THICK + THICK / 2))), WIDE, HIGH,
                        THICK));
        return List.of(new Connection(name + " frame", holder, at, frame, "holder", Joining.BOLTED),
                new Connection(name, frame, "holder", plate, "frame", Joining.BOLTED));
    }
}
