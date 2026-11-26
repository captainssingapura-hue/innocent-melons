package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Course;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Profiled;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * What moves in one cylinder, each in titanium, bare, where the crank at rest puts it: the piston, up its bore as far
 * as its rod reaches from the pin, its crown, its three rings and its skirt, a slipper's, only on the two sides the
 * rod pushes it against, its pin's bosses either side of the rod;
 * the gudgeon pin through them; and the connecting rod, an I beam its web in the crank's plane, its big end split
 * round the crankpin and bolted, its small end round the gudgeon pin.
 *
 * @param figures the engine's figures
 * @param bank    which bank, from nought
 * @param k       which cylinder in it, from nought
 */
record Cylinder(EngineFigures figures, int bank, int k) {

    private static final Made TITANIUM = new Made(Material.TITANIUM, true);
    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    private static final int ROUND = 24;
    /** How deep a piston's crown is; where its rings sit under its top; how far its skirt reaches under its pin. */
    private static final double CROWN = 2.4;
    private static final List<Double> RINGS = List.of(0.5, 1.0, 1.5);
    private static final double SKIRT = 2.6;
    /** How far a slipper skirt reaches along the crank either side, of the bore's radius; in how many steps round. */
    private static final double SLIPPER = 0.6;
    private static final int SLIPPER_STEPS = 6;

    /** How near the crank's axis its piston comes, up its bore, at the bottom of its stroke: its skirt's foot. */
    double reach() {
        return figures.rod() - figures.stroke() / 2 - SKIRT;
    }

    /**
     * One side of a piston's skirt, a slipper's, from {@code foot} on its bore's axis {@code high} up: on the side
     * {@code side} the rod pushes it against, only as far round as its pin's bosses reach along the crank, so it is
     * cut away fore and aft where the crank's webs pass under a piston at the bottom of its stroke.
     */
    private Mesh slipper(final Vec3 foot, final double high, final int side) {
        final double r = figures.bore() / 2;
        final double round = Math.asin(SLIPPER);
        final Vec3 thrust = up().cross(ALONG).times(side);
        final List<List<Vec3>> rings = new ArrayList<>();
        for (int i = 0; i <= SLIPPER_STEPS; i++) {
            final double a = -round + 2 * round * i / SLIPPER_STEPS;
            final Vec3 d = thrust.times(Math.cos(a)).plus(ALONG.times(Math.sin(a)));
            final Vec3 top = foot.plus(up().times(high));
            rings.add(List.of(foot.plus(d.times(r - 0.4)), foot.plus(d.times(r - 0.08)), top.plus(d.times(r - 0.08)),
                    top.plus(d.times(r - 0.4))));
        }
        return new Shapes(ROUND).lofted(rings, foot.plus(up().times(high / 2)));
    }

    /** What it is called: its bank and its place in the bank, from one. */
    String called() {
        return figures.name() + " " + (bank + 1) + "." + (k + 1);
    }

    /** The way its bore points, up from the crank. */
    Vec3 up() {
        return new BlockForm(figures).up(bank);
    }

    /** Where its crankpin is under its rod, at rest. */
    Vec3 bigEnd() {
        return bigEnd(0);
    }

    /** The same, the crank turned {@code turned} degrees on. */
    Vec3 bigEnd(final double turned) {
        return new CrankFigures(figures).pin(bank, k, turned);
    }

    /** Where its gudgeon pin is: up the bore as far as the rod reaches from the crankpin, at rest. */
    Vec3 smallEnd() {
        return smallEnd(0);
    }

    /** The same, the crank turned {@code turned} degrees on. */
    Vec3 smallEnd(final double turned) {
        final Vec3 p = bigEnd(turned);
        final Vec3 flat = new Vec3(p.x(), p.y(), 0);
        final double along = flat.dot(up());
        final double s = along + Math.sqrt(figures.rod() * figures.rod() - flat.dot(flat) + along * along);
        return up().times(s).plus(ALONG.times(p.z()));
    }

    /** Where its bore meets the deck, on the bore's axis. */
    Vec3 deck() {
        return up().times(figures.deck()).plus(ALONG.times(figures.along(bank, k)));
    }

    /** The connecting rod. */
    Part rod() {
        final CrankFigures crank = new CrankFigures(figures);
        final Vec3 p = bigEnd();
        final Vec3 q = smallEnd();
        final Vec3 way = q.minus(p).unit();
        final Vec3 side = ALONG.cross(way).unit();
        final double wide = crank.rodWide();
        final List<Spot> section = new Outlines(ROUND).ibeam(wide, 2.0, 0.5, 0.45);
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>();
        made.add(new Profiled(new Course(List.of(p.plus(way.times(crank.pin() + 0.6)), q.minus(way.times(
                crank.gudgeon() + 0.4))), 2), List.of(section, section), ALONG).mesh());
        made.add(new Sleeve(p, ALONG, wide, crank.pin() + 0.8, crank.pin(), ROUND).mesh());
        made.add(new Sleeve(q, ALONG, wide, crank.gudgeon() + 0.55, crank.gudgeon(), ROUND).mesh());
        for (final int s : List.of(1, -1)) {
            final Vec3 bolt = p.plus(side.times(s * (crank.pin() + 0.9)));
            made.add(shapes.drum(bolt.minus(way.times(crank.pin() + 0.5)), bolt.plus(way.times(0.4)), 0.35));
        }
        final List<Point> points = List.of(new Point("big end", p), new Point("small end", q));
        return new Part(figures.name() + " rod " + where(), Group.ENGINE, false, points, shapes.joined(made),
                TITANIUM).as(new EngineParts.ConnectingRod());
    }

    /** The piston. */
    Part piston() {
        final CrankFigures crank = new CrankFigures(figures);
        final Vec3 q = smallEnd();
        final Vec3 top = q.plus(up().times(figures.crown()));
        final double r = figures.bore() / 2;
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>();
        made.add(shapes.drum(top.minus(up().times(CROWN)), top, r - 0.05));
        for (final double down : RINGS) {
            made.add(new Sleeve(top.minus(up().times(down)), up(), 0.18, r - 0.01, r - 0.3, ROUND).mesh());
        }
        final double skirt = figures.crown() - CROWN + SKIRT;
        for (final int s : List.of(1, -1)) {
            made.add(slipper(top.minus(up().times(CROWN + skirt)), skirt, s));
        }
        final double bosses = figures.bore() * 0.62;
        final double boss = (bosses - crank.rodWide()) / 2 - 0.1;
        for (final int s : List.of(1, -1)) {
            made.add(new Sleeve(q.plus(ALONG.times(s * (bosses / 2 - boss / 2))), ALONG, boss,
                    crank.gudgeon() + 0.55, crank.gudgeon(), ROUND).mesh());
        }
        final List<Point> points = List.of(new Point("pin", q), new Point("bore", deck()));
        return new Part(figures.name() + " piston " + where(), Group.ENGINE, false, points, shapes.joined(made),
                TITANIUM).as(new EngineParts.Piston());
    }

    /** The gudgeon pin, through the piston's bosses and the rod's small end. */
    Part gudgeon() {
        final CrankFigures crank = new CrankFigures(figures);
        final Vec3 q = smallEnd();
        return new Part(figures.name() + " gudgeon pin " + where(), Group.ENGINE, false,
                List.of(new Point("middle", q)), new Sleeve(q, ALONG, figures.bore() * 0.62 - 0.2, crank.gudgeon(),
                crank.gudgeon() * 0.55, ROUND).mesh(), TITANIUM).as(new EngineParts.GudgeonPin());
    }

    private String where() {
        return (bank + 1) + "." + (k + 1);
    }
}
