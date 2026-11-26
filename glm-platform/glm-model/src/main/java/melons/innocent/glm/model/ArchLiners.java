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
 * The retro saloon's inner arch liners: in each wheel's arch a shell of black plastic round over the tyre, from the
 * arch's foot ahead of it, over its top, to its foot behind it, closing the arch so no one sees into the body; inside
 * the shell's own arch, clear of the tyre however far the front wheels are steered, from just outboard of the
 * suspension, its strut, its dampers and its links, out to just inside the body's face. The front ones are carried by
 * the wings, the rear ones by the body sides.
 *
 * @param lines       the car's lines
 * @param proportions the platform's figures
 */
record ArchLiners(SantanaLines lines, Proportions proportions) {

    private static final Making MOULDED = new Making(new Made(Material.PLASTIC, true));
    /** How far it stands off the tyre; how thick it is; how far round it runs past level, ahead and behind. */
    private static final double CLEAR = 2.7;
    private static final double THICK = 0.3;
    private static final double PAST = 15;
    /** How far in it reaches, just outboard of the suspension, and how far inside the body's face it stops. */
    private static final double IN = 64.5;
    private static final double INSIDE = 1;

    /** The liners: the front ones on {@code wing}, at {@code wingAt}; the rear ones on {@code side}. */
    List<Connection> joined(final Part wing, final Vec3 wingAt, final Part side, final Vec3 sideAt) {
        return List.of(
                new Connection("front arch liner", wing, "shell", liner("front arch liner", 0, wingAt), "holder",
                        Joining.BOLTED),
                new Connection("rear arch liner", side, "body", liner("rear arch liner", proportions.back(), sideAt),
                        "holder", Joining.BOLTED));
    }

    /** A liner round the right wheel on the axle at {@code axle} along, held at {@code held}. */
    private Part liner(final String name, final double axle, final Vec3 held) {
        final double out = lines.half(axle, 0) - INSIDE;
        return MOULDED.part(name, Group.TRIM, true, Map.of("holder", held), new Shapes(32).band(
                new Vec3((IN + out) / 2, proportions.tyre(), axle), proportions.tyre() + CLEAR, THICK, out - IN,
                -PAST, 180 + PAST));
    }
}
