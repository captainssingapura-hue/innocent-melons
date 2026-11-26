package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The boot lid, pressed steel, painted, hinged at its front to the shell, as the original's is: from the rear
 * window's foot back to the tail, between the body sides' tops along the boot, inside them by the shut gap; its face
 * level with their tops at its edges and crowned toward its middle, as the drawing's outline, the car's middle, stands
 * over the side's edge; falling at its back with the deck to the tail; its edges turned down.
 *
 * @param lines the car's lines
 */
record BootLid(SantanaLines lines) {

    private static final Making PRESSED = new Making(new Made(Material.STEEL, false));
    /** Where it begins at the rear window's foot and ends at the tail, along, in the drawing's millimetres. */
    private static final double FRONT = -2_990;
    private static final double BACK = -3_445;
    /** How much higher its middle is than its edges; its shut gap; its sheet; how deep its edges are turned down. */
    private static final double CROWN = 3;
    private static final double GAP = 0.4;
    private static final double SHEET = 0.08;
    private static final double RIM = 3;
    /** How finely its sides are drawn and its faces cut. */
    private static final int SIDES = 16;
    private static final double STEP = 4;

    /** It, its points where they are on the platform. */
    Part part() {
        return PRESSED.part("boot lid", Group.BODYWORK, false, Map.of("hinge", hinge()),
                new Pressing(outline(), List.of(), this::on, new Vec3(0, 1, 0), SHEET, RIM, RIM, STEP, STEP).mesh());
    }

    /** Where it is hinged to the shell: the middle of its front edge. */
    Vec3 hinge() {
        return on(new Spot(front(), 0));
    }

    /** Its outline, along and across. */
    private List<Spot> outline() {
        final List<Spot> right = new ArrayList<>();
        final List<Spot> left = new ArrayList<>();
        for (int k = 0; k <= SIDES; k++) {
            final double z = front() + (back() - front()) * k / SIDES;
            right.add(new Spot(z, width(z) - GAP));
            left.add(new Spot(z, GAP - width(z)));
        }
        final List<Spot> out = new ArrayList<>(right);
        out.addAll(left.reversed());
        return out;
    }

    /** Where a spot of it, along and across, comes to on its face. */
    private Vec3 on(final Spot spot) {
        final double z = spot.u();
        final double across = spot.v() / width(z);
        return new Vec3(spot.v(), edge(z) + CROWN * (1 - across * across), z);
    }

    /** How high the body sides' tops are, at {@code z} along. */
    private double edge(final double z) {
        return new Loop(lines.side()).top(z);
    }

    /** How far out the body sides' tops are, at {@code z} along. */
    private double width(final double z) {
        return lines.half(z, edge(z));
    }

    private double front() {
        return FRONT * lines.scale() / 10;
    }

    private double back() {
        return BACK * lines.scale() / 10;
    }
}
