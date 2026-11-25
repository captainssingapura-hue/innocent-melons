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
 * A screen, the windscreen or the rear window: laminated or toughened glass bonded into its frame, from its foot up
 * to the roof's header, as wide as the body is there, inside its pillars; curved across, its middle further out than
 * its sides by {@code bulge}, ahead for the windscreen, behind for the rear window; and crowned toward its top, as the
 * roof is, by {@code crown}.
 *
 * @param name   what it is called
 * @param lines  the car's lines
 * @param foot   its foot at its sides, along and up
 * @param head   its head at its sides, along and up
 * @param bulge  how much further out its middle is than its sides, along, ahead if more than nought
 * @param crown  how much higher its middle is at its head than its sides
 * @param anchor where it is bonded to the roof's header
 */
record Screen(String name, SantanaLines lines, Spot foot, Spot head, double bulge, double crown, Vec3 anchor) {

    private static final Making GLAZED = new Making(new Made(Material.GLASS, false));
    /** How far inside the body's face its sides are; how thick it is; how finely its faces are cut. */
    private static final double INSET = 5;
    private static final double THICK = 0.5;
    private static final double STEP = 0.05;

    /** It, its points where they are on the platform. */
    Part part() {
        final List<Spot> whole = List.of(new Spot(-1, 0), new Spot(1, 0), new Spot(1, 1), new Spot(-1, 1));
        final Vec3 out = new Vec3(0, 0.5, Math.signum(head.u() - foot.u()) * -1);
        return GLAZED.part(name, Group.GLASS, false, Map.of("header", anchor),
                new Pressing(whole, List.of(), this::on, out, THICK, THICK, THICK, STEP, STEP).mesh());
    }

    /** Where a spot of it, {@code u} across from its left side to its right, {@code v} up from its foot, comes to. */
    private Vec3 on(final Spot spot) {
        final double round = 1 - spot.u() * spot.u();
        final double z = foot.u() + (head.u() - foot.u()) * spot.v() + bulge * round;
        final double y = foot.v() + (head.v() - foot.v()) * spot.v() + crown * round * spot.v();
        return new Vec3(spot.u() * (lines.half(z, y) - INSET), y, z);
    }
}
