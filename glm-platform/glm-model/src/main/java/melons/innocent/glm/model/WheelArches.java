package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * What the retro saloon wears round its wheels outside, as the owners' photographs show it: round each arch's lip a
 * chrome trim, a band laid on the panel just beyond the lip and curled in over it, the front one on the wing, the rear
 * one on the body side, narrow enough to clear the rear door; and behind each wheel a mud flap of black rubber,
 * hanging from the body to a hand's breadth over the ground, as wide as the tyre.
 *
 * @param lines the car's lines
 */
record WheelArches(SantanaLines lines) {

    private static final Making CHROMED = new Making(new Made(Material.STEEL, true));
    private static final Making RUBBER = new Making(new Made(Material.RUBBER, true));
    /** How far the trim reaches beyond the lip and over it, in millimetres; how far it stands off the panel. */
    private static final double WIDE = 35;
    private static final double OVER = 5;
    private static final double OFF = 0.3;
    /** The trim's sheet; how far its edges curl in; how finely it is drawn round. */
    private static final double SHEET = 0.12;
    private static final double CURL = 1.5;
    private static final int ROUND = 32;
    /** How far the wings stand off the body's face. */
    private static final double WING = 0.12;
    /** The flaps: how high their foot; how wide and thick; how far inside the body's face; where each hangs along. */
    private static final double FLAP_FOOT = 9;
    private static final double FLAP_WIDE = 20;
    private static final double FLAP_THICK = 0.6;
    private static final double FLAP_IN = 2;
    private static final double FRONT_FLAP = -410;
    private static final double REAR_FLAP = -2_935;

    /** The trims and the flaps: the front ones on {@code wing}, at {@code wingAt}, the rear on {@code side}. */
    List<Connection> joined(final Part wing, final Vec3 wingAt, final Part side, final Vec3 sideAt) {
        final Arch front = new SantanaFront(lines).arch();
        final Arch rear = lines.rearArch();
        final List<Connection> joined = new ArrayList<>();
        joined.add(new Connection("front arch trim", wing, "shell", trim("front arch trim", front, WING + OFF,
                wingAt), "holder", Joining.BOLTED));
        joined.add(new Connection("rear arch trim", side, "body", trim("rear arch trim", rear, OFF, sideAt),
                "holder", Joining.BOLTED));
        final double sill = front.at(front.to(), 0, lines.scale()).v();
        joined.add(new Connection("front mud flap", wing, "shell", flap("front mud flap", mm(FRONT_FLAP), sill + 1,
                wingAt), "holder", Joining.BOLTED));
        joined.add(new Connection("rear mud flap", side, "body", flap("rear mud flap", mm(REAR_FLAP), 44, sideAt),
                "holder", Joining.BOLTED));
        return joined;
    }

    /** A trim round {@code arch}'s lip, standing {@code off} beyond the body's face, held at {@code held}. */
    private Part trim(final String name, final Arch arch, final double off, final Vec3 held) {
        final List<Spot> outline = new ArrayList<>();
        for (int k = 0; k <= ROUND; k++) {
            outline.add(arch.at(arch.from() + (arch.to() - arch.from()) * k / ROUND, WIDE, lines.scale()));
        }
        for (int k = ROUND; k >= 0; k--) {
            outline.add(arch.at(arch.from() + (arch.to() - arch.from()) * k / ROUND, -OVER, lines.scale()));
        }
        return CHROMED.part(name, Group.TRIM, true, Map.of("holder", held), new Pressing(outline, List.of(),
                s -> new Vec3(lines.half(s.u(), s.v()) + off, s.v(), s.u()), new Vec3(1, 0, 0), SHEET, CURL, CURL,
                3, 1.5).mesh());
    }

    /** A mud flap hanging at {@code z} along from {@code top} to its foot, held at {@code held}. */
    private Part flap(final String name, final double z, final double top, final Vec3 held) {
        final double out = lines.half(z, FLAP_FOOT) - FLAP_IN;
        return RUBBER.part(name, Group.TRIM, true, Map.of("holder", held),
                new Sheets(FLAP_THICK).across(z, out - FLAP_WIDE, FLAP_FOOT, out, top));
    }

    /** So many of the drawing's millimetres, in the platform's centimetres, made larger. */
    private double mm(final double millimetres) {
        return millimetres * lines.scale() / 10;
    }
}
