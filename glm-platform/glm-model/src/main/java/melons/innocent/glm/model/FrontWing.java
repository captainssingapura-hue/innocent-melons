package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;
import java.util.Map;

/**
 * A front wing, the right's, the left its mirror: one pressing in sheet steel, painted, bolted to the shell, as the
 * original's are. Its outline as the front end has it ({@link SantanaFront}); its face the body's, curved as the front
 * view has it and drawn in round the nose, standing a sheet's thickness proud of the body side, whose windscreen
 * pillar's foot it lies over; its edges turned in, its arch's lip, its top along the bonnet's shut line, its front
 * beside the headlamp.
 *
 * @param lines the car's lines
 */
record FrontWing(SantanaLines lines) {

    private static final Making PRESSED = new Making(new Made(Material.STEEL, false));
    /** How thick its sheet is; how far it stands off the body side; how deep its edges are turned in. */
    private static final double SHEET = 0.08;
    private static final double PROUD = 0.12;
    private static final double RIM = 2.5;
    /** How finely its faces are cut, along and up. */
    private static final double ALONG = 4;
    private static final double UP = 2;

    /** It, its points where they are on the platform. */
    Part part() {
        final SantanaFront front = new SantanaFront(lines);
        return PRESSED.part("front wing", Group.BODYWORK, true, Map.of("shell", anchor()),
                new Pressing(front.wing(), List.of(), this::on, new Vec3(1, 0, 0), SHEET, RIM, RIM, ALONG,
                        UP).mesh());
    }

    /** Where it is bolted to the shell, on the right. */
    Vec3 anchor() {
        return on(new SantanaFront(lines).anchor());
    }

    /** Where a spot of the side view, along and up, comes to on its outer face. */
    private Vec3 on(final Spot spot) {
        return new Vec3(lines.half(spot.u(), spot.v()) + PROUD, spot.v(), spot.u());
    }
}
