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
 * A side window's pane, the right's, the left its mirror: toughened glass in a door's window or in the quarter light,
 * a little larger than its opening, its edges in the frame's channel, set in from the body's face, curved as it is.
 *
 * @param name    what it is called
 * @param lines   the car's lines
 * @param opening the opening it shuts, along and up
 * @param held    what its point is called, where what holds it is
 * @param anchor  where what holds it is
 */
record Pane(String name, SantanaLines lines, List<Spot> opening, String held, Vec3 anchor) {

    private static final Making GLAZED = new Making(new Made(Material.GLASS, false));
    /** How far past its opening it reaches; how far in from the body's face it is; how thick it is. */
    private static final double PAST = 0.8;
    private static final double IN = 2;
    private static final double THICK = 0.4;
    /** How finely its faces are cut, along and up. */
    private static final double ALONG = 5;
    private static final double UP = 2;

    /** It, its points where they are on the platform. */
    Part part() {
        return GLAZED.part(name, Group.GLASS, true, Map.of(held, anchor),
                new Pressing(new Loop(opening).inset(-PAST).spots(), List.of(), this::on, new Vec3(1, 0, 0), THICK,
                        THICK, THICK, ALONG, UP).mesh());
    }

    /** Where a spot of it, along and up, comes to: set in from the body's face. */
    private Vec3 on(final Spot spot) {
        return new Vec3(lines.half(spot.u(), spot.v()) - IN, spot.v(), spot.u());
    }
}
