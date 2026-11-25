package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The engine's trim cover, moulded in plastic, clipped on rubber grommets over the top of it all, as it is seen with
 * the bonnet up: a flat top over the head covers and the coils, a V's banks and the V between them alike, its
 * corners cut; a skirt round it, down toward the heads; two ribs along its top. No maker's name on it. Its point: on
 * the first head cover's top, where it is clipped.
 *
 * @param figures the engine's figures
 */
record TrimCover(EngineFigures figures) {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How far over the coils its top stands, how far its skirt reaches down, its corners cut, how thick. */
    private static final double OVER = 1.2;
    private static final double SKIRT = 4;
    private static final double CORNER = 3;
    private static final double THICK = 0.35;
    private static final double PAST = 1;

    /** It. */
    Part part() {
        final double[] box = reach();
        final double y = box[4];
        final List<Spot> outline = List.of(new Spot(box[0] + CORNER, box[2]), new Spot(box[1] - CORNER, box[2]),
                new Spot(box[1], box[2] + CORNER), new Spot(box[1], box[3] - CORNER),
                new Spot(box[1] - CORNER, box[3]), new Spot(box[0] + CORNER, box[3]),
                new Spot(box[0], box[3] - CORNER), new Spot(box[0], box[2] + CORNER));
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(outline, List.of(), new Vec3(0, y, 0), new Vec3(1, 0, 0), ALONG, THICK).mesh());
        for (int i = 0; i < outline.size(); i++) {
            final Spot a = outline.get(i);
            final Spot b = outline.get((i + 1) % outline.size());
            final Vec3 way = new Vec3(b.u() - a.u(), 0, b.v() - a.v());
            made.add(new Plate(List.of(new Spot(0, 0), new Spot(way.length(), 0), new Spot(way.length(), SKIRT),
                    new Spot(0, SKIRT)), List.of(), new Vec3(a.u(), y - SKIRT, a.v()), way, new Vec3(0, 1, 0),
                    THICK).mesh());
        }
        final Shapes shapes = new Shapes(8);
        final double length = box[3] - box[2] - 2 * CORNER - 4;
        for (final double x : List.of(-0.22, 0.22)) {
            final double across = box[0] + (box[1] - box[0]) * (0.5 + x);
            made.add(shapes.box(new Vec3(across, y + 0.4, (box[2] + box[3]) / 2), 1.2, 0.6, length));
        }
        return new Part(figures.name() + " trim cover", Group.COVERS, false,
                List.of(new Point("clip", new HeadCover(figures, 0).middle())), shapes.joined(made),
                new Made(Material.PLASTIC, true));
    }

    /**
     * How far it reaches, in the block's frame, as {left, right, front, rear, top}: a little past the head covers, the
     * coils and the timing cover every way across and along; and its top over the highest of them.
     */
    double[] reach() {
        final List<Vec3> under = new ArrayList<>(new TimingCover(figures).part().form().points());
        for (int b = 0; b < figures.banks(); b++) {
            under.addAll(new HeadCover(figures, b).part().form().points());
            under.addAll(new CoilPacks(figures, b).part().form().points());
        }
        final double left = under.stream().mapToDouble(Vec3::x).min().orElseThrow();
        final double right = under.stream().mapToDouble(Vec3::x).max().orElseThrow();
        final double front = under.stream().mapToDouble(Vec3::z).min().orElseThrow();
        final double rear = under.stream().mapToDouble(Vec3::z).max().orElseThrow();
        final double high = under.stream().mapToDouble(Vec3::y).max().orElseThrow();
        return new double[] {left - PAST, right + PAST, front - PAST, rear + PAST, high + OVER};
    }
}
