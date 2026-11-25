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
 * The bonnet, pressed steel, painted, hinged at its back to the bulkhead, as the original's is: from the scuttle's
 * front, ahead of the windscreen, to where the wings' tops roll down to the nose; between the wings' tops, nearly the
 * car's width, inside them by the shut gap; its face level with their tops at its edges, the bonnet's shut lines, and
 * crowned a little toward its middle; its edges turned down.
 *
 * @param lines the car's lines
 */
record Bonnet(SantanaLines lines) {

    private static final Making PRESSED = new Making(new Made(Material.STEEL, false));
    /** Where it begins at the scuttle and ends at the nose, along, in centimetres. */
    private static final double BACK = -30;
    private static final double FRONT = 80.5;
    /** How much higher its middle is than its edges; its shut gap; its sheet; how deep its edges are turned down. */
    private static final double CROWN = 2.5;
    private static final double GAP = 0.4;
    private static final double SHEET = 0.08;
    private static final double RIM = 3;
    /** How finely its sides are drawn and its faces cut. */
    private static final int SIDES = 20;
    private static final double STEP = 4;

    /** It, its points where they are on the platform. */
    Part part() {
        final List<Spot> right = new ArrayList<>();
        final List<Spot> left = new ArrayList<>();
        for (int k = 0; k <= SIDES; k++) {
            final double z = BACK + (FRONT - BACK) * k / SIDES;
            right.add(new Spot(z, width(z) - GAP));
            left.add(new Spot(z, GAP - width(z)));
        }
        final List<Spot> outline = new ArrayList<>(right);
        outline.addAll(left.reversed());
        return PRESSED.part("bonnet", Group.BODYWORK, false, Map.of("hinge", hinge()),
                new Pressing(outline, List.of(), this::on, new Vec3(0, 1, 0), SHEET, RIM, RIM, STEP, STEP).mesh());
    }

    /** Where it is hinged to the bulkhead: the middle of its back edge. */
    Vec3 hinge() {
        return on(new Spot(BACK, 0));
    }

    /** How high its face is at {@code x} across and {@code z} along. */
    double height(final double x, final double z) {
        final double across = x / width(z);
        return edge(z) + CROWN * (1 - across * across);
    }

    /** Where a spot of it, along and across, comes to on its face. */
    private Vec3 on(final Spot spot) {
        return new Vec3(spot.v(), height(spot.v(), spot.u()), spot.u());
    }

    /** How high the wings' tops are, at {@code z} along. */
    private double edge(final double z) {
        return new Loop(new SantanaFront(lines).wing()).top(z);
    }

    /** How far out the wings' tops are, at {@code z} along. */
    private double width(final double z) {
        return lines.half(z, edge(z));
    }
}
