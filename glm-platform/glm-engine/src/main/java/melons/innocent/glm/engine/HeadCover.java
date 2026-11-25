package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A bank's cylinder head cover, cast in aluminium: bolted round the head's top on its gasket, closing in its
 * camshafts, caps, buckets and springs and the oil thrown off them; its sides rising straight, then leaning in to its
 * top, clear of the cams' lobes at their highest and the caps' bolts; its ends closed; through its top a hole over
 * each spark plug's tube for its coil; on the first bank's, the oil's filler. Its points: where it sits on its head,
 * and the middle of its top, where its coils and the engine's trim cover sit.
 *
 * @param figures the engine's figures
 * @param bank    which bank's head it covers, from nought
 */
record HeadCover(EngineFigures figures, int bank) {

    private static final int ROUND = 20;
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How far its sides rise straight, how far they lean in, how far clear of the cams it stands; how thick. */
    private static final double STRAIGHT = 1.2;
    private static final double IN = 1.5;
    private static final double CLEAR = 0.8;
    private static final double THICK = 0.4;
    /** How far round a coil's hole is. */
    private static final double COIL = 1.6;

    /** It, called as its head is. */
    Part part() {
        final HeadFigures head = new HeadFigures(figures);
        return new Part(figures.name() + " cylinder head cover" + (figures.banks() == 2 ? ", bank " + (bank + 1)
                : ""), Group.COVERS, false, List.of(new Point("head", head.top(bank)), new Point("top", middle())),
                mesh(head), new Made(Material.ALUMINIUM, true));
    }

    /** How high its top stands over its bank's deck: clear of its cams' lobes at their highest and the caps' bolts. */
    double top() {
        final HeadFigures head = new HeadFigures(figures);
        final double lobes = head.journal() * 1.1 + new ValveTiming(figures).most();
        final double bolts = head.journal() + 0.8 + 0.6;
        return head.high() + Math.max(lobes, bolts) + CLEAR + THICK / 2;
    }

    /** The middle of its top, in the block's frame. */
    Vec3 middle() {
        final HeadFigures head = new HeadFigures(figures);
        return head.at(bank, 0, new Vec3(0, top(), (head.front(bank) + head.rear(bank)) / 2
                - figures.along(bank, 0)));
    }

    /** Its section, across and up in its bank's frame, from its inlet side's foot over to its exhaust side's. */
    List<Spot> section() {
        final HeadFigures head = new HeadFigures(figures);
        final double in = head.side(true);
        final double ex = -head.side(false);
        final double h = head.high();
        return List.of(new Spot(in, h), new Spot(in, h + STRAIGHT), new Spot(in - IN, top()),
                new Spot(ex + IN, top()), new Spot(ex, h + STRAIGHT), new Spot(ex, h));
    }

    private Mesh mesh(final HeadFigures head) {
        final List<Spot> section = section();
        final double front = head.front(bank);
        final double length = head.rear(bank) - front;
        final List<Mesh> made = new ArrayList<>();
        for (int i = 0; i + 1 < section.size(); i++) {
            made.add(wall(head, section.get(i), section.get(i + 1), front, length, i == 2));
        }
        for (final double z : List.of(front + THICK / 2, front + length - THICK / 2)) {
            made.add(new Plate(section, List.of(), head.at(bank, 0, new Vec3(0, 0, z - figures.along(bank, 0))),
                    head.way(bank, new Vec3(1, 0, 0)), head.way(bank, new Vec3(0, 1, 0)), THICK).mesh());
        }
        if (bank == 0) {
            final Vec3 cap = head.at(bank, 0, new Vec3(head.side(true) - IN - 3, top(), front + 4
                    - figures.along(bank, 0)));
            made.add(new Shapes(ROUND).drum(cap, cap.plus(head.way(bank, new Vec3(0, 1.4, 0))), 2.2));
        }
        return new Shapes(ROUND).joined(made);
    }

    /** Its wall from {@code a} to {@code b} in section, its whole length; its top holed for the coils. */
    private Mesh wall(final HeadFigures head, final Spot a, final Spot b, final double front, final double length,
                      final boolean holed) {
        final Vec3 from = head.at(bank, 0, new Vec3(a.u(), a.v(), front - figures.along(bank, 0)));
        final Vec3 to = head.at(bank, 0, new Vec3(b.u(), b.v(), front - figures.along(bank, 0)));
        final double wide = to.minus(from).length();
        final List<List<Spot>> holes = new ArrayList<>();
        if (holed) {
            final Outlines outlines = new Outlines(ROUND);
            for (int k = 0; k < figures.perBank(); k++) {
                holes.add(outlines.circle(new Spot(a.u(), figures.along(bank, k) - front), COIL));
            }
        }
        return new Plate(List.of(new Spot(0, 0), new Spot(wide, 0), new Spot(wide, length), new Spot(0, length)),
                holes, from, to.minus(from), ALONG, THICK).mesh();
    }
}
