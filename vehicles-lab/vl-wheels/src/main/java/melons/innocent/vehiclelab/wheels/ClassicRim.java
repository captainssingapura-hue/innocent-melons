package melons.innocent.vehiclelab.wheels;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The classic alloy rim, as the retro saloons of the eighties wore: a flat face dished in behind a bright lip, cast as
 * its solid middle, a solid ring round it and eight broad spokes between, eight windows left through it, narrow at
 * the middle and widening out toward the barrel; a large plain cap at its middle over the wheel's bolts; its centre a
 * boss from the hub's face out to its face. Each of its measures a share of the face's reach, so it is the same rim at
 * any size.
 *
 * @param shapes what it is shaped with
 */
public record ClassicRim(Shapes shapes) implements Rim {

    /** How many windows; how far in and out they reach and how wide they are there, in degrees round. */
    private static final int WINDOWS = 8;
    private static final double WINDOW_IN = 0.45;
    private static final double WINDOW_OUT = 0.87;
    private static final double HALF_IN = 8.5;
    private static final double HALF_OUT = 14.5;
    /** How far its face is dished in behind its lip; how thick its face is. */
    private static final double DISH = 3.5;
    private static final double FACE = 1.6;
    /** Its cap and its boss, as shares of its face's reach; how far the cap stands out, in two steps. */
    private static final double CAP = 0.37;
    private static final double CAP_TOP = 0.3;
    private static final double CAP_OUT = 1.4;
    private static final double CAP_TOP_OUT = 2;
    private static final double BOSS = 0.4;
    /** Its lip: how deep and how wide. */
    private static final double LIP = 1.3;
    private static final double LIP_WIDE = 1.2;
    /** How finely its outline and its windows are drawn. */
    private static final int ROUND = 64;
    private static final int ARC = 6;
    /** How far each spoke reaches into the solid middle and ring, so they meet without a seam. */
    private static final double SEAM = 0.3;

    @Override
    public Mesh mesh(final WheelSize size, final double mount) {
        final double reach = inside(size);
        final double edge = barrel(size) / 2;
        final double face = Math.max(edge - DISH, mount + 1);
        final List<Mesh> forms = new ArrayList<>();
        forms.add(shapes.band(new Vec3(0, 0, 0), reach, thick(), barrel(size), 0, 360));
        forms.add(shapes.band(new Vec3(edge - LIP_WIDE / 2, 0, 0), size.rimRadius() - 0.4, LIP, LIP_WIDE, 0, 360));
        forms.add(face(circle(reach + 0.2), List.of(circle(WINDOW_OUT * reach)), face));
        forms.add(face(circle(WINDOW_IN * reach), List.of(), face));
        for (int w = 0; w < WINDOWS; w++) {
            forms.add(face(spoke(reach, 360.0 * w / WINDOWS), List.of(), face));
        }
        forms.add(shapes.drum(new Vec3(mount, 0, 0), new Vec3(face, 0, 0), BOSS * reach));
        forms.add(shapes.drum(new Vec3(face, 0, 0), new Vec3(face + CAP_OUT, 0, 0), CAP * reach));
        forms.add(shapes.drum(new Vec3(face, 0, 0), new Vec3(face + CAP_TOP_OUT, 0, 0), CAP_TOP * reach));
        return shapes.joined(forms);
    }

    /** Its face's outline, a circle {@code reach} round. */
    private List<Spot> circle(final double reach) {
        final List<Spot> out = new ArrayList<>();
        for (int k = 0; k < ROUND; k++) {
            final double a = 2 * Math.PI * k / ROUND;
            out.add(new Spot(reach * Math.cos(a), reach * Math.sin(a)));
        }
        return out;
    }

    /** A flat piece of its face, {@code outline} cut with {@code holes}, at {@code face} out from the wheel's middle. */
    private Mesh face(final List<Spot> outline, final List<List<Spot>> holes, final double face) {
        return new Plate(outline, holes, new Vec3(face, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, 1), FACE).mesh();
    }

    /**
     * The spoke after the window at {@code middle} degrees round: from that window's trailing edge to the next one's
     * leading edge, each edge straight from the window's narrow end to its wide end; reaching a little into the solid
     * middle and the solid ring round it, so they meet without a seam.
     */
    private List<Spot> spoke(final double reach, final double middle) {
        final double next = middle + 360.0 / WINDOWS;
        final double in = WINDOW_IN * reach - SEAM;
        final double out = WINDOW_OUT * reach + SEAM;
        final List<Spot> spoke = new ArrayList<>();
        for (int k = 0; k <= ARC; k++) {
            spoke.add(polar(in, middle + HALF_IN + (next - middle - 2 * HALF_IN) * k / ARC));
        }
        for (int k = 0; k <= ARC; k++) {
            spoke.add(polar(out, next - HALF_OUT - (next - middle - 2 * HALF_OUT) * k / ARC));
        }
        return spoke;
    }

    private Spot polar(final double reach, final double degrees) {
        final double a = Math.toRadians(degrees);
        return new Spot(reach * Math.cos(a), reach * Math.sin(a));
    }
}
