package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A door, the right's, the left its mirror, hung on its hinges in its opening in the body side: pressed steel,
 * painted, as the original's are. Its skin flush with the body side, inside its opening by the shut gap all round,
 * its edge folded in over its frame; above the waist its window's frame, slim, round the glass's opening, the opening
 * split by a divider bar where the original's is, a small pane one side of it; below the waist its inner panel, as far
 * in as its frame is deep, closing it into a box.
 *
 * @param name    what it is called
 * @param lines   the car's lines
 * @param opening its opening in the body side, the hinge's foot first
 * @param divider where its window is divided, along
 */
record Door(String name, SantanaLines lines, List<Spot> opening, double divider) {

    private static final Making PRESSED = new Making(new Made(Material.STEEL, false));
    /** How far inside its opening it is; how wide its window's frame and divider; how deep it is; its sheet. */
    private static final double GAP = 0.4;
    private static final double FRAME = 2.8;
    private static final double BAR = 2.2;
    private static final double DEEP = 6;
    private static final double SHEET = 0.08;
    /** How deep its window's flange is; how finely its faces are cut, along and up. */
    private static final double FLANGE = 1.5;
    private static final double ALONG = 5;
    private static final double UP = 2;

    /** It, its points where they are on the platform. */
    Part part() {
        final Map<String, Vec3> points = new LinkedHashMap<>();
        points.put("hinge", on(opening.getFirst()));
        return PRESSED.part(name, Group.BODYWORK, true, points, new Shapes(16).joined(List.of(skin(), inner())));
    }

    /** Its skin and its window's frame, cut for the glass either side of the divider. */
    private Mesh skin() {
        final Loop glass = new Loop(opening).inset(GAP + FRAME).up(lines.belt(), true);
        final List<List<Spot>> panes = List.of(glass.along(divider + BAR / 2, true).spots(),
                glass.along(divider - BAR / 2, false).spots());
        return new Pressing(new Loop(opening).inset(GAP).spots(), panes, this::on, new Vec3(1, 0, 0), SHEET, DEEP,
                FLANGE, ALONG, UP).mesh();
    }

    /** Its inner panel, below the waist, as far in as it is deep. */
    private Mesh inner() {
        final List<Spot> below = new Loop(opening).inset(GAP + SHEET).up(lines.waist(), false).spots();
        return new Pressing(below, List.of(), s -> on(s).minus(new Vec3(DEEP, 0, 0)), new Vec3(-1, 0, 0), SHEET,
                SHEET, FLANGE, ALONG, UP).mesh();
    }

    /** Where a spot of the side view, along and up, comes to on the body side's outer face. */
    private Vec3 on(final Spot spot) {
        return new Vec3(lines.half(spot.u(), spot.v()), spot.v(), spot.u());
    }
}
