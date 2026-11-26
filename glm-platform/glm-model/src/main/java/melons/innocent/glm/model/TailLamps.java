package melons.innocent.glm.model;

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
import java.util.List;
import java.util.Map;

/**
 * The retro saloon's tail lamps, as the maker's photographs show them: a wide unit each side across the tail panel,
 * just under the boot lid, the number plate between them; each a black housing set in its opening in the tail panel,
 * its lenses side by side from the plate out to the corner: the white reversing lamp, the red tail lamp, the amber
 * indicator. Up, from 740 to 868 mm as the side photograph has them; across, each lens a share of the unit as the
 * rear photograph has them; scaled and made larger as the car's lines are.
 *
 * @param lines the car's lines
 */
record TailLamps(SantanaLines lines) {

    private static final Making MOULDED = new Making(new Made(Material.PLASTIC, true));
    private static final Making LENS = new Making(new Made(Material.PLASTIC, false));
    /** How high the lamps run, in the drawing's millimetres. */
    private static final double LOW = 740;
    private static final double HIGH = 868;
    /** How far out they begin, beside the number plate; how far inside the body's face they end; in centimetres. */
    private static final double IN = 27;
    private static final double INSIDE = 3.5;
    /** Each lens's share of the unit's width, from the plate out: reversing, tail, indicator. */
    private static final double[] SHARES = {0.15, 0.5, 0.35};
    /** How deep the housing is, how far it stands proud of the panel; how deep the lenses; the gap between them. */
    private static final double DEEP = 6;
    private static final double PROUD = 0.4;
    private static final double LENS_DEEP = 0.8;
    private static final double GAP = 0.4;

    /** Its parts, each housing bolted to {@code panel}, the tail panel, its lenses carried by it. */
    List<Connection> joined(final Part panel) {
        final Vec3 middle = middle();
        final Part housing = MOULDED.part("tail lamp housing", Group.TRIM, true, Map.of("panel", middle),
                new Shapes(8).box(new Vec3(middle.x(), middle.y(), z() - PROUD + DEEP / 2), out() - IN,
                        high() - low(), DEEP));
        final List<Connection> joined = new ArrayList<>();
        joined.add(new Connection("tail lamp housing", panel, "tail lamp, right", housing, "panel",
                Joining.BOLTED));
        final List<String> names = List.of("reversing lamp", "tail lamp", "rear indicator");
        final List<Group> groups = List.of(Group.LAMPS, Group.TAIL_LAMPS, Group.SIGNALS);
        double from = IN;
        for (int k = 0; k < SHARES.length; k++) {
            final double to = from + SHARES[k] * (out() - IN);
            final Part lens = LENS.part(names.get(k), groups.get(k), true, Map.of("housing", middle),
                    new Shapes(8).box(new Vec3((from + to) / 2, middle.y(), z() - PROUD - LENS_DEEP / 2),
                            to - from - GAP, high() - low() - GAP, LENS_DEEP));
            joined.add(new Connection(names.get(k), housing, "panel", lens, "housing", Joining.SEATED));
            from = to;
        }
        return joined;
    }

    /** The right lamp's opening in the tail panel, across and up. */
    List<Spot> opening() {
        return new Sheets(0.1).rectangle(IN, low(), out(), high());
    }

    /** The middle of the right lamp, on the tail panel's face. */
    Vec3 middle() {
        return new Vec3((IN + out()) / 2, (low() + high()) / 2, z());
    }

    /** Where the tail panel's face is, along. */
    private double z() {
        return lines.tail() + 0.3;
    }

    /** How far out the lamps reach, just inside the body sides at the tail. */
    private double out() {
        return lines.half(z(), (low() + high()) / 2) - INSIDE;
    }

    private double low() {
        return LOW * lines.scale() / 10;
    }

    private double high() {
        return HIGH * lines.scale() / 10;
    }
}
