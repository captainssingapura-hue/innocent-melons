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
 * The roof, one panel of dark glass in the original's steel roof's place, bonded over the cage: from the front
 * header to the rear, out over the body sides' roof rails nearly to their edges; crowned as the original roof is, its
 * middle higher than its edges as the drawing's outline, the car's middle, stands over the side's edge.
 *
 * @param lines  the car's lines
 * @param anchor where it is bonded to the front header
 */
record RoofGlass(SantanaLines lines, Vec3 anchor) {

    private static final Making GLAZED = new Making(new Made(Material.GLASS, false));
    /** How much higher its middle is than its edges; how far inside the body sides' edges it stops; how high over. */
    private static final double CROWN = 5;
    private static final double INSIDE = 1;
    private static final double OVER = 0.3;
    /** How thick it is; how finely its sides are drawn and its faces cut. */
    private static final double THICK = 0.6;
    private static final int SIDES = 16;
    private static final double STEP = 4;

    /** It, its points where they are on the platform. */
    Part part() {
        final double front = lines.frontHeader().u();
        final double back = lines.rearHeader().u();
        final List<Spot> right = new ArrayList<>();
        final List<Spot> left = new ArrayList<>();
        for (int k = 0; k <= SIDES; k++) {
            final double z = front + (back - front) * k / SIDES;
            right.add(new Spot(z, width(z)));
            left.add(new Spot(z, -width(z)));
        }
        final List<Spot> outline = new ArrayList<>(right);
        outline.addAll(left.reversed());
        return GLAZED.part("glass roof", Group.ROOF, false, Map.of("header", anchor),
                new Pressing(outline, List.of(), this::on, new Vec3(0, 1, 0), THICK, THICK, THICK, STEP, STEP)
                        .mesh());
    }

    /** Where a spot of it, along and across, comes to. */
    private Vec3 on(final Spot spot) {
        final double z = spot.u();
        final double across = spot.v() / (width(z) + INSIDE);
        return new Vec3(spot.v(), edge(z) + OVER + CROWN * (1 - across * across), z);
    }

    /** How high the body sides' roof edges are at {@code z} along. */
    private double edge(final double z) {
        return new Loop(lines.side()).top(z);
    }

    /** How far out it reaches at {@code z} along. */
    private double width(final double z) {
        return lines.half(z, edge(z)) - INSIDE;
    }
}
