package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * One of a head's camshaft towers: a wall across the head from its deck up to its camshafts' axes, the lower half of
 * each camshaft's bearing cut in its top; and over each journal its cap, the bearing's upper half, held down by two
 * bolts, so the camshaft can be laid in from above and its caps bolted down over it.
 *
 * @param head how the head is laid out
 * @param bank which bank's head, from nought
 */
record CamTower(HeadFigures head, int bank) {

    private static final int ROUND = 12;
    /** How thick a tower is along the crank; how far a cap reaches past its journal, and over it. */
    private static final double THICK = 2;
    private static final double REACH = 1.1;
    private static final double OVER = 0.8;
    /** The seam between a cap and its tower. */
    private static final double SEAM = 0.05;

    /** The tower and its caps at {@code z} along the crank. */
    List<Mesh> at(final double z) {
        final List<Mesh> made = new ArrayList<>();
        final double h = head.high();
        final double r = head.journal();
        final List<Spot> outline = new ArrayList<>(List.of(new Spot(-head.side(false), 0),
                new Spot(head.side(true), 0), new Spot(head.side(true), h)));
        for (final boolean inlet : List.of(true, false)) {
            final double x = head.cam(inlet).x();
            outline.add(new Spot(x + r, h));
            outline.addAll(half(x, r, -1));
            outline.add(new Spot(x - r, h));
        }
        outline.add(new Spot(-head.side(false), h));
        made.add(plate(outline, z));
        final Shapes shapes = new Shapes(ROUND);
        for (final boolean inlet : List.of(true, false)) {
            final double x = head.cam(inlet).x();
            final List<Spot> cap = new ArrayList<>(List.of(new Spot(x - r - REACH, h + SEAM),
                    new Spot(x - r, h + SEAM)));
            cap.addAll(half(x, r, 1).reversed().stream().map(s -> new Spot(s.u(), s.v() + SEAM)).toList());
            cap.addAll(List.of(new Spot(x + r, h + SEAM), new Spot(x + r + REACH, h + SEAM),
                    new Spot(x + r + REACH, h + r + OVER), new Spot(x - r - REACH, h + r + OVER)));
            made.add(plate(cap, z));
            for (final int s : List.of(-1, 1)) {
                final Vec3 bolt = head.at(bank, 0, new Vec3(x + s * (r + REACH / 2), h + r + OVER,
                        z - head.figures().along(bank, 0)));
                made.add(shapes.drum(bolt, bolt.plus(head.way(bank, new Vec3(0, 0.6, 0))), 0.45));
            }
        }
        return made;
    }

    /** Half a bearing round {@code x} at the axes' height, {@code r} round: under it ({@code under} -1) or over. */
    private List<Spot> half(final double x, final double r, final int under) {
        final List<Spot> arc = new ArrayList<>();
        for (int j = 1; j < ROUND; j++) {
            final double t = Math.PI * j / ROUND;
            arc.add(new Spot(x + r * Math.cos(t), head.high() + under * r * Math.sin(t)));
        }
        return arc;
    }

    private Mesh plate(final List<Spot> outline, final double z) {
        return new Plate(outline, List.of(), head.at(bank, 0, new Vec3(0, 0, z - head.figures().along(bank, 0))),
                head.way(bank, new Vec3(1, 0, 0)), head.way(bank, new Vec3(0, 1, 0)), THICK).mesh();
    }
}
