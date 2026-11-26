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
 * A twin's clutch cover, cast in aluminium, across the crankcase's back end: closing in the primary drive from the
 * crank's gear to the clutch's round its basket, and the clutch on the mainshaft ({@link GearboxFigures}); its face
 * out past the clutch, as far round as the two gears reach; and its wall back from its face to the crankcase's end.
 * Its point: the crank's axis at the crankcase's back end, where it is bolted.
 *
 * @param figures the engine's figures
 */
record ClutchCover(EngineFigures figures) {

    private static final int ROUND = 32;
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How far out its face stands past the crankcase's end: past the clutch's basket and plates; how thick it is. */
    static final double OUT = 7;
    private static final double THICK = 0.4;
    /** How far past each gear's teeth it reaches. */
    private static final double PAST = 0.8;

    /** Its outline, across the crank: round the crank's gear and the clutch's. */
    List<Spot> outline() {
        final GearboxFigures gearbox = new GearboxFigures(figures);
        final Outlines outlines = new Outlines(ROUND);
        final List<Spot> round = new ArrayList<>(outlines.circle(new Spot(0, 0), gearbox.crankGear() + 0.25 + PAST));
        round.addAll(outlines.circle(gearbox.mainshaft(), gearbox.clutchGear() + 0.25 + PAST));
        return new Hull(round).outline();
    }

    /** It. */
    Part part() {
        final double end = figures.length();
        final List<Spot> outline = outline();
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(outline, List.of(), new Vec3(0, 0, end + OUT), new Vec3(1, 0, 0), new Vec3(0, 1, 0),
                THICK).mesh());
        for (int i = 0; i < outline.size(); i++) {
            final Spot a = outline.get(i);
            final Spot b = outline.get((i + 1) % outline.size());
            final Vec3 way = new Vec3(b.u() - a.u(), b.v() - a.v(), 0);
            if (way.length() > 1e-6) {
                made.add(new Plate(List.of(new Spot(0, 0), new Spot(way.length(), 0), new Spot(way.length(), OUT),
                        new Spot(0, OUT)), List.of(), new Vec3(a.u(), a.v(), end), way, ALONG, THICK).mesh());
            }
        }
        return new Part(figures.name() + " clutch cover", Group.COVERS, false,
                List.of(new Point("crank", ALONG.times(end))), new Shapes(ROUND).joined(made),
                new Made(Material.ALUMINIUM, true)).as(new EngineParts.ClutchCover());
    }
}
