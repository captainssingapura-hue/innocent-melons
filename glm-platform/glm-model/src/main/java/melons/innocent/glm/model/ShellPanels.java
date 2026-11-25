package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Section;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The shell's panels welded to its underbody and its body sides, each a part of its own, pressed steel, painted: the
 * bulkhead between the engine and the cabin, up to the scuttle under the windscreen, notched over the tunnel; the
 * tail panel across the tail, from the boot's floor up to the boot lid's edge, cut for the tail lamps each side of the
 * number plate; and the roof's two headers across between the body sides' roof rails, over the windscreen and over
 * the rear window, closing the shell into a cage the roof's glass will lie on.
 *
 * @param lines where the shell runs
 */
record ShellPanels(ShellLines lines) {

    private static final Making PRESSED = new Making(new Made(Material.STEEL, false));
    private static final Sheets SHEETS = new Sheets(0.15);
    private static final Section HEADER = new Section(8, 4, 8);
    /** How far inside the body sides' faces the panels stop; how far the scuttle reaches ahead. */
    private static final double INSIDE = 2;
    private static final double SCUTTLE = 14;
    /** The tail lamps' openings, across and up; how high the tail panel's top is. */
    private static final double LAMP_IN = 18;
    private static final double LAMP_LOW = 79;
    private static final double LAMP_HIGH = 91;
    private static final double TAIL_TOP = 100.5;
    /** How much higher a header is at the middle than at its ends. */
    private static final double CROWN = 3;

    /** The bulkhead, with the scuttle's tray along its top. */
    Part bulkhead() {
        final double z = lines.bulkhead();
        final double top = lines.scuttle();
        final double t = lines.tunnelX();
        final double floor = lines.floor();
        final double foot = lines.sill() + 3;
        final List<Spot> outline = new ArrayList<>(List.of(new Spot(t, floor), new Spot(foot, floor)));
        outline.addAll(edge(z, 31, top, 1));
        outline.addAll(edge(z, 31, top, -1).reversed());
        outline.addAll(List.of(new Spot(-foot, floor), new Spot(-t, floor), new Spot(-t, lines.tunnelY()),
                new Spot(t, lines.tunnelY())));
        final double across = lines.half(z, top) - INSIDE;
        return PRESSED.part("bulkhead", Group.BODYWORK, false, Map.of("underbody", new Vec3(0, floor, z)),
                new Shapes(16).joined(List.of(SHEETS.across(z, outline, List.of()),
                        SHEETS.level(top, -across, z, across, z + SCUTTLE))));
    }

    /** The tail panel, cut for the tail lamps. */
    Part tailPanel() {
        final double z = lines.tail();
        final double floor = lines.boot();
        final List<Spot> outline = new ArrayList<>(edge(z, floor, TAIL_TOP, 1));
        outline.addAll(edge(z, floor, TAIL_TOP, -1).reversed());
        final double out = lines.half(z, (LAMP_LOW + LAMP_HIGH) / 2) - INSIDE - 6;
        final List<List<Spot>> lamps = List.of(SHEETS.rectangle(LAMP_IN, LAMP_LOW, out, LAMP_HIGH),
                SHEETS.rectangle(-out, LAMP_LOW, -LAMP_IN, LAMP_HIGH));
        return PRESSED.part("tail panel", Group.BODYWORK, false, Map.of("underbody", new Vec3(0, floor, z)),
                SHEETS.across(z, outline, lamps));
    }

    /** A roof header across at {@code at}, along and up, from the right body side's rail to the left's. */
    Part header(final String name, final Spot at) {
        final Vec3 end = end(at);
        final Map<String, Vec3> points = new LinkedHashMap<>();
        points.put("body side, right", end);
        points.put("body side, left", new Vec3(-end.x(), end.y(), end.z()));
        points.put("glass", glass(at));
        return PRESSED.part(name, Group.BODYWORK, false, points, SHEETS.member(List.of(points.get("body side, left"),
                new Vec3(0, end.y() + CROWN, end.z()), end), HEADER));
    }

    /** Where the glass is bonded to a header at {@code at}, along and up: at its middle. */
    Vec3 glass(final Spot at) {
        final Vec3 end = end(at);
        return new Vec3(0, end.y(), end.z());
    }

    /** Where a header at {@code at}, along and up, meets the right body side, just inside its face. */
    Vec3 end(final Spot at) {
        return new Vec3(lines.half(at.u(), at.v()) - INSIDE, at.v(), at.u());
    }

    /** The edge of a panel across at {@code z}, just inside the body sides' faces, from {@code low} to {@code high}. */
    private List<Spot> edge(final double z, final double low, final double high, final int side) {
        final List<Spot> out = new ArrayList<>();
        final int steps = 16;
        for (int k = 0; k <= steps; k++) {
            final double y = low + (high - low) * k / steps;
            out.add(new Spot(side * (lines.half(z, y) - INSIDE), y));
        }
        return out;
    }
}
