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
 * The timing cover, cast in aluminium, across the engine's front: closing in the chain that runs from the crank's
 * sprocket up round each camshaft's wheel, its face ahead of the wheels, as far round as the crank's sprocket and
 * every camshaft's wheel reach, the crank's nose through it; and its wall back from its face to the block and the
 * heads. Its point: the crank's axis at the block's front, where it is bolted.
 *
 * @param figures the engine's figures
 */
record TimingCover(EngineFigures figures) {

    private static final int ROUND = 24;
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** Where its face is along the crank, ahead of the block's front; how thick it is cast. */
    private static final double FACE = -3;
    private static final double THICK = 0.4;
    /** How far past a camshaft's wheel it reaches, and round the crank's sprocket. */
    private static final double PAST = 1.2;
    private static final double SPROCKET = 7;

    /** It. */
    Part part() {
        final HeadFigures head = new HeadFigures(figures);
        final List<Spot> round = new ArrayList<>();
        final Outlines outlines = new Outlines(ROUND);
        round.addAll(outlines.circle(new Spot(0, 0), SPROCKET));
        for (int b = 0; b < figures.banks(); b++) {
            for (final boolean inlet : List.of(true, false)) {
                final Vec3 cam = head.at(b, 0, head.cam(inlet));
                round.addAll(outlines.circle(new Spot(cam.x(), cam.y()), Camshaft.WHEEL + PAST));
            }
        }
        final List<Spot> outline = new Hull(round).outline();
        final List<Mesh> made = new ArrayList<>();
        final double nose = new CrankFigures(figures).main() * 0.8 + 0.3;
        made.add(new Plate(outline, List.of(outlines.circle(new Spot(0, 0), nose)), new Vec3(0, 0, FACE),
                new Vec3(1, 0, 0), new Vec3(0, 1, 0), THICK).mesh());
        for (int i = 0; i < outline.size(); i++) {
            final Spot a = outline.get(i);
            final Spot b = outline.get((i + 1) % outline.size());
            final Vec3 from = new Vec3(a.u(), a.v(), FACE);
            final Vec3 way = new Vec3(b.u() - a.u(), b.v() - a.v(), 0);
            if (way.length() > 1e-6) {
                made.add(new Plate(List.of(new Spot(0, 0), new Spot(way.length(), 0),
                        new Spot(way.length(), -FACE), new Spot(0, -FACE)), List.of(), from, way, ALONG, THICK)
                        .mesh());
            }
        }
        return new Part(figures.name() + " timing cover", Group.COVERS, false,
                List.of(new Point("crank", new Vec3(0, 0, 0))), new Shapes(ROUND)
                .joined(made), new Made(Material.ALUMINIUM, true));
    }

    /** Where its face is along the crank. */
    double face() {
        return FACE;
    }
}
