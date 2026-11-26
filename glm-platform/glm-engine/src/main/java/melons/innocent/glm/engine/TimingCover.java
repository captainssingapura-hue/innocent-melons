package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The timing cover, cast in aluminium, across the engine's front: closing in the chain that runs from the crank's
 * sprocket up round each camshaft's wheel, its face ahead of the wheels, as far round as the crank's sprocket and
 * every camshaft's wheel reach, in a V notched down between its banks as far as its chains let it, the crank's
 * nose through it; and its wall back from its face to the block and the
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
    private static final double PAST = 0.5;
    private static final double SPROCKET = 7;

    /**
     * Its outline, across the crank: round the crank's sprocket and every camshaft's; in a V, round each bank's
     * own, so where the banks stand apart it is notched down between them as far as their chains, each a tower of its
     * own ({@link Union}).
     */
    List<Spot> outline() {
        if (figures.banks() == 2 && !figures.boxer()) {
            return new Union(round(1, SPROCKET), round(0, SPROCKET - 0.1)).outline();
        }
        final List<Spot> all = new ArrayList<>(round(0, SPROCKET));
        for (int b = 1; b < figures.banks(); b++) {
            all.addAll(round(b, SPROCKET));
        }
        return new Hull(all).outline();
    }

    /** The outline round the crank's sprocket, {@code crank} round, and bank {@code bank}'s camshafts'. */
    private List<Spot> round(final int bank, final double crank) {
        final Outlines outlines = new Outlines(ROUND);
        final List<Spot> round = new ArrayList<>(outlines.circle(new Spot(0, 0), crank));
        final ChainFigures chain = new ChainFigures(figures, bank);
        for (final boolean inlet : List.of(true, false)) {
            round.addAll(outlines.circle(chain.cam(inlet), chain.cam() + Sprocket.TOOTH + PAST));
        }
        return new Hull(round).outline();
    }

    /** It. */
    Part part() {
        final Outlines outlines = new Outlines(ROUND);
        final List<Spot> outline = outline();
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
                .joined(made), new Made(Material.ALUMINIUM, true)).as(new EngineParts.TimingCover());
    }

    /** Where its face is along the crank. */
    double face() {
        return FACE;
    }
}
