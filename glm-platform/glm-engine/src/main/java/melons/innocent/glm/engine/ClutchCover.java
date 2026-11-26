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
 * A twin's clutch cover, cast in aluminium, on the bike's left, at the crankcase's front end, outside the cam chains
 * and their timing cover: closing in the primary drive from the crank's gear to the clutch's round its basket, and
 * the clutch on the mainshaft ({@link GearboxFigures}); its face out past the clutch, as far round as the two gears
 * reach; its wall back from its face to the timing cover's face where that lies behind it, and on back to the
 * crankcase's end where it does not. Its point: the crank's axis at the crankcase's front end, where it is bolted.
 *
 * @param figures the engine's figures
 */
record ClutchCover(EngineFigures figures) {

    private static final int ROUND = 32;
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How far out its face stands past the timing cover's face: past the clutch's basket and plates; how thick. */
    static final double OUT = 7;
    private static final double THICK = 0.4;
    /** How far past each gear's teeth it reaches; how long a piece of its wall is at most. */
    private static final double PAST = 0.8;
    private static final double PIECE = 1;

    /** Its outline, across the crank: round the crank's gear and the clutch's. */
    List<Spot> outline() {
        final GearboxFigures gearbox = new GearboxFigures(figures);
        final Outlines outlines = new Outlines(ROUND);
        final List<Spot> round = new ArrayList<>(outlines.circle(new Spot(0, 0), gearbox.crankGear() + 0.25 + PAST));
        round.addAll(outlines.circle(gearbox.mainshaft(), gearbox.clutchGear() + 0.25 + PAST));
        return new Hull(round).outline();
    }

    /** Where its face is along the crank: out past the timing cover's. */
    double face() {
        return new TimingCover(figures).face() - OUT;
    }

    /** It. */
    Part part() {
        final TimingCover timing = new TimingCover(figures);
        final List<Spot> behind = timing.outline();
        final List<Spot> outline = outline();
        final List<Mesh> made = new ArrayList<>();
        made.add(new Plate(outline, List.of(), ALONG.times(face()), new Vec3(1, 0, 0), new Vec3(0, 1, 0), THICK)
                .mesh());
        for (int i = 0; i < outline.size(); i++) {
            final Spot a = outline.get(i);
            final Spot b = outline.get((i + 1) % outline.size());
            final int pieces = (int) Math.ceil(Math.hypot(b.u() - a.u(), b.v() - a.v()) / PIECE);
            for (int k = 0; k < pieces; k++) {
                final Spot from = between(a, b, (double) k / pieces);
                final Spot to = between(a, b, (k + 1.0) / pieces);
                final double back = within(between(from, to, 0.5), behind) ? timing.face() : 0;
                made.add(wall(from, to, back));
            }
        }
        return new Part(figures.name() + " clutch cover", Group.COVERS, false,
                List.of(new Point("crank", new Vec3(0, 0, 0))), new Shapes(ROUND).joined(made),
                new Made(Material.ALUMINIUM, true)).as(new EngineParts.ClutchCover());
    }

    /** Its wall from {@code from} to {@code to} in outline, from its face back along the crank to {@code back}. */
    private Mesh wall(final Spot from, final Spot to, final double back) {
        final Vec3 way = new Vec3(to.u() - from.u(), to.v() - from.v(), 0);
        final double deep = back - face();
        return new Plate(List.of(new Spot(0, 0), new Spot(way.length(), 0), new Spot(way.length(), deep),
                new Spot(0, deep)), List.of(), new Vec3(from.u(), from.v(), face()), way, ALONG, THICK).mesh();
    }

    /** The spot {@code t} of the way from {@code a} to {@code b}. */
    private Spot between(final Spot a, final Spot b, final double t) {
        return new Spot(a.u() + t * (b.u() - a.u()), a.v() + t * (b.v() - a.v()));
    }

    /** Whether {@code p} is within {@code outline}, however it is shaped: crossing it an odd number of times. */
    private boolean within(final Spot p, final List<Spot> outline) {
        boolean in = false;
        for (int i = 0, j = outline.size() - 1; i < outline.size(); j = i++) {
            final Spot a = outline.get(i);
            final Spot b = outline.get(j);
            if ((a.v() > p.v()) != (b.v() > p.v())
                    && p.u() < (b.u() - a.u()) * (p.v() - a.v()) / (b.v() - a.v()) + a.u()) {
                in = !in;
            }
        }
        return in;
    }
}
