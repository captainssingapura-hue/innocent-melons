package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A body side, the right's, the left its mirror: one pressing in sheet steel, painted, as the original's is, from the
 * windscreen's pillar to the tail. Its A pillar up the windscreen's edge, its roof's side rail over the doors, its B
 * pillar between them, its C pillar down to the boot; its sill under the doors; its rear quarter round the rear wheel
 * to the tail; the front and rear doors' openings cut in it, and the small quarter light's in the C pillar, their edges
 * turned in as flanges for the doors' seals and the glass's rubber. Its outer face as the original's side view
 * outlines it and its front view curves it, made larger as the lines are.
 *
 * @param lines   the car's lines
 * @param anchor  where it is fixed to the body it stands on, on the right
 * @param headers where the roof's headers meet it
 */
record BodySide(SantanaLines lines, Vec3 anchor, ShellPanels headers) {

    private static final Making PRESSED = new Making(new Made(Material.STEEL, false));
    /** How thick its sheet is; how deep its door openings' flanges; how finely its faces are cut, along and up. */
    private static final double SHEET = 0.1;
    private static final double FLANGE = 3;
    private static final double ALONG = 5;
    private static final double UP = 2;

    /** It, its points where they are on the platform. */
    Part part() {
        final Map<String, Vec3> points = new LinkedHashMap<>();
        points.put("body", anchor);
        points.put("front door, hinge", on(lines.frontDoor().getFirst()));
        points.put("rear door, hinge", on(lines.rearDoor().getFirst()));
        points.put("header, front", headers.end(lines.frontHeader()));
        points.put("header, rear", headers.end(lines.rearHeader()));
        final List<List<Spot>> cut = List.of(lines.frontDoor(), lines.rearDoor(), lines.quarterLight());
        return PRESSED.part("body side", Group.BODYWORK, true, points, new Pressing(lines.side(), cut, this::on,
                new Vec3(1, 0, 0), SHEET, FLANGE, ALONG, UP).mesh());
    }

    /** Where a spot of the side view, along and up, comes to on its outer face. */
    private Vec3 on(final Spot spot) {
        return new Vec3(lines.half(spot.u(), spot.v()), spot.v(), spot.u());
    }
}
