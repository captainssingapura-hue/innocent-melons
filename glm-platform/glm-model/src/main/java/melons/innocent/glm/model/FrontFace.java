package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The retro saloon's front face, between the wings' front edges, from the bumper up to the bonnet's edge, as the
 * maker's photographs show it, no badge: a panel of black plastic across it, bolted to the shell; on it the slim black
 * grille, its slats across, a chrome bar along its top; each side a rectangular headlamp, a chrome reflector behind a
 * glass lens, and outboard of it the white indicator. Across, each a share of the face's half-width, as the near
 * head-on photograph has them; up, as the side photograph has them, scaled and made larger as the car's lines are.
 *
 * @param lines the car's lines
 */
record FrontFace(SantanaLines lines) {

    private static final Making MOULDED = new Making(new Made(Material.PLASTIC, true));
    private static final Making CHROMED = new Making(new Made(Material.STEEL, true));
    private static final Making GLAZED = new Making(new Made(Material.GLASS, false));
    private static final Making LENS = new Making(new Made(Material.PLASTIC, false));
    /** Where its face stands along, just behind the wings' front edges, in the drawing's millimetres. */
    private static final double FACE = 796;
    /** How high the panel reaches, the lamps and the grille run, in the drawing's millimetres. */
    private static final double PANEL_LOW = 552;
    private static final double PANEL_HIGH = 752;
    private static final double LAMP_LOW = 580;
    private static final double LAMP_HIGH = 730;
    private static final double GRILLE_LOW = 592;
    private static final double GRILLE_HIGH = 718;
    /** Across, as shares of the face's half-width: the grille's edge, the headlamp's, the indicator's. */
    private static final double GRILLE = 0.555;
    private static final double LAMP_IN = 0.565;
    private static final double LAMP_OUT = 0.868;
    private static final double SIGNAL_OUT = 0.985;
    /** How many slats across the grille; how deep the slats, the lamps and the indicators are, in centimetres. */
    private static final int SLATS = 5;
    private static final double SLAT_DEEP = 1.5;
    private static final double LAMP_DEEP = 5;
    private static final double SKIN = 0.3;

    /** Its parts, as each is fixed to the shell's {@code root} or to the panel. */
    List<Connection> joined(final Part root) {
        final Part panel = panel();
        final Part headlamp = lamp("headlamp", LAMP_IN, LAMP_OUT, CHROMED, Group.TRIM);
        final Part signal = lamp("indicator", LAMP_OUT + 0.01, SIGNAL_OUT, LENS, Group.LAMPS);
        final List<Connection> joined = new ArrayList<>();
        joined.add(new Connection("grille panel", root, "front panel", panel, "shell", Joining.BOLTED));
        joined.add(new Connection("grille", panel, "shell", grille(), "panel", Joining.BOLTED));
        joined.add(new Connection("grille bar", panel, "shell", bar(), "panel", Joining.BOLTED));
        joined.add(new Connection("headlamp", panel, "headlamp, right", headlamp, "panel", Joining.BOLTED));
        joined.add(new Connection("headlamp lens", headlamp, "panel", lens(), "headlamp", Joining.SEATED));
        joined.add(new Connection("indicator", panel, "indicator, right", signal, "panel", Joining.BOLTED));
        return joined;
    }

    /** Where the panel is bolted to the shell: the middle of its face. */
    Vec3 mount() {
        return new Vec3(0, mm((PANEL_LOW + PANEL_HIGH) / 2), face());
    }

    /** The panel, black plastic across the whole face. */
    private Part panel() {
        final List<Spot> right = new ArrayList<>();
        final int steps = 8;
        for (int k = 0; k <= steps; k++) {
            final double y = mm(PANEL_LOW + (PANEL_HIGH - PANEL_LOW) * k / steps);
            right.add(new Spot(half(y), y));
        }
        final List<Spot> outline = new ArrayList<>(right);
        right.reversed().forEach(s -> outline.add(new Spot(-s.u(), s.v())));
        final Map<String, Vec3> points = new LinkedHashMap<>();
        points.put("shell", mount());
        for (final String lamp : List.of("headlamp", "indicator")) {
            final Vec3 at = lampMiddle(lamp.equals("headlamp") ? LAMP_IN : LAMP_OUT + 0.01,
                    lamp.equals("headlamp") ? LAMP_OUT : SIGNAL_OUT);
            points.put(lamp + ", right", at);
            points.put(lamp + ", left", new Vec3(-at.x(), at.y(), at.z()));
        }
        return MOULDED.part("grille panel", Group.TRIM, false, points,
                new Sheets(SKIN).across(face(), outline, List.of()));
    }

    /** The grille's slats across, between the headlamps. */
    private Part grille() {
        final Shapes shapes = new Shapes(8);
        final double x = GRILLE * half(mm(LAMP_HIGH));
        final List<Mesh> slats = new ArrayList<>();
        for (int k = 0; k < SLATS; k++) {
            final double y = mm(GRILLE_LOW + (GRILLE_HIGH - GRILLE_LOW) * (k + 0.5) / SLATS);
            slats.add(shapes.box(new Vec3(0, y, face() + SLAT_DEEP / 2), 2 * x, mm(GRILLE_HIGH - GRILLE_LOW) / SLATS
                    * 0.55, SLAT_DEEP));
        }
        return MOULDED.part("grille", Group.TRIM, false, Map.of("panel", mount()), shapes.joined(slats));
    }

    /** The chrome bar along the grille's top. */
    private Part bar() {
        final double x = GRILLE * half(mm(LAMP_HIGH));
        return CHROMED.part("grille bar", Group.TRIM, false, Map.of("panel", mount()), new Shapes(8).box(
                new Vec3(0, mm(GRILLE_HIGH) + 0.8, face() + 1), 2 * x, 1.1, 2));
    }

    /** A lamp on the right, from {@code in} to {@code out} of the face's half-width, as deep as a lamp is. */
    private Part lamp(final String name, final double in, final double out, final Making making, final Group group) {
        final Vec3 middle = lampMiddle(in, out);
        final double wide = (out - in) * half(mm(LAMP_HIGH));
        return making.part(name, group, true, Map.of("panel", middle), new Shapes(8).box(
                middle.plus(new Vec3(0, 0, SKIN - LAMP_DEEP / 2 + 0.6)), wide, mm(LAMP_HIGH - LAMP_LOW), LAMP_DEEP));
    }

    /** The headlamp's glass lens, over its reflector. */
    private Part lens() {
        final Vec3 middle = lampMiddle(LAMP_IN, LAMP_OUT);
        final double wide = (LAMP_OUT - LAMP_IN) * half(mm(LAMP_HIGH));
        final double high = mm(LAMP_HIGH - LAMP_LOW);
        final List<Spot> outline = new Sheets(SKIN).rectangle(middle.x() - wide / 2 + 0.3,
                middle.y() - high / 2 + 0.3, middle.x() + wide / 2 - 0.3, middle.y() + high / 2 - 0.3);
        return GLAZED.part("headlamp lens", Group.GLASS, true, Map.of("headlamp", middle),
                new Sheets(0.4).across(face() + 1.1, outline, List.of()));
    }

    /** The middle of a lamp on the right, from {@code in} to {@code out} of the face's half-width, on its face. */
    private Vec3 lampMiddle(final double in, final double out) {
        final double h = half(mm(LAMP_HIGH));
        return new Vec3((in + out) / 2 * h, mm((LAMP_LOW + LAMP_HIGH) / 2), face());
    }

    /** How far out the face reaches at {@code y} up, just inside the wings. */
    private double half(final double y) {
        return lines.half(face(), y) - 0.6;
    }

    private double face() {
        return mm(FACE);
    }

    /** So many of the drawing's millimetres, in the platform's centimetres, made larger. */
    private double mm(final double millimetres) {
        return millimetres * lines.scale() / 10;
    }
}
