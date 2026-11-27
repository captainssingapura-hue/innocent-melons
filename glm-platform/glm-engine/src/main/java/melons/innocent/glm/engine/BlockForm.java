package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * How a block is cast to an engine's figures, in its own frame: the crank's axis along z from the block's front, up
 * as y, across as x. Its section, the same all along, as its casing has it ({@link Casing}): its banks standing over
 * the crank in line or in a V ({@link UprightCase}), lying flat out of a crankcase in a boxer ({@link BoxerCase}), or
 * a twin's crankcase, its barrels cast apart and bolted on it ({@link TwinCase}, {@link Barrel}). That section's
 * walls carried the block's length, each bank's top bored through for its cylinders; a liner in each bore down past
 * the piston's reach, in a twin in its barrel; its ends closed but for the crank's seals; a bulkhead across between
 * each pair of cylinders and at each end, bored for the crank's main bearing; and a boss each side for its mount.
 *
 * @param figures the engine's figures
 */
record BlockForm(EngineFigures figures) {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    private static final int ROUND = 24;
    /** A cylinder's liner, its water's jacket and the block's wall; how thick its walls and its bulkheads are cast. */
    private static final double LINER = 0.6;
    private static final double JACKET = 0.9;
    private static final double WALL = 0.5;
    private static final double SHELL = 0.5;
    private static final double BULKHEAD = 2;
    /** How far round a main bearing's bore is, and a gearbox shaft's bearing's. */
    private static final double MAIN = 3.4;
    private static final double SHAFT = 1.8;
    /** How far the crankcase's walls stand clear of all that turns on the crank. */
    private static final double CLEAR = 0.6;

    /** How wide a bank is, from its cylinders' axis out to its wall. */
    double half() {
        return figures.bore() / 2 + liner() + JACKET + WALL;
    }

    /** The way bank {@code bank}'s cylinders point, up from the crank. */
    Vec3 up(final int bank) {
        final double a = Math.toRadians(figures.lean(bank));
        return new Vec3(Math.sin(a), Math.cos(a), 0);
    }

    /** The way out of bank {@code bank}, square to its cylinders, toward its outer wall: in a boxer, up. */
    Vec3 out(final int bank) {
        if (figures.boxer()) {
            return new Vec3(0, 1, 0);
        }
        final double a = Math.toRadians(figures.lean(bank));
        final Vec3 n = new Vec3(Math.cos(a), -Math.sin(a), 0);
        return figures.lean(bank) < 0 ? n.times(-1) : n;
    }

    /** How far the crankcase's walls stand from the crank's axis, at the least: clear of all that turns on it. */
    double clear() {
        return new CrankFigures(figures).swing() + CLEAR;
    }

    /** How far up its bank the block's top is, bored for the cylinders: its deck, or in a twin its barrels' feet. */
    double top() {
        return figures.twin() ? new TwinCase(figures, this).foot() : figures.deck();
    }

    /** How its casing is cast. */
    Casing casing() {
        if (figures.boxer()) {
            return new BoxerCase(figures, this);
        }
        return figures.twin() ? new TwinCase(figures, this) : new UprightCase(figures, this);
    }

    /** The section, drawn across and up, round to the left, starting along the open bottom. */
    List<Spot> section() {
        return casing().section();
    }

    /** It: in a twin, its crankcase. */
    Mesh mesh() {
        final Casing casing = casing();
        final List<Spot> section = casing.section();
        final double length = figures.length();
        final List<Mesh> made = new ArrayList<>();
        for (int k = 1; k < section.size(); k++) {
            final Spot a = section.get(k);
            final Spot b = section.get((k + 1) % section.size());
            made.add(wall(a, b, length));
        }
        final Outlines outlines = new Outlines(ROUND);
        final List<List<Spot>> seal = List.of(outlines.circle(new Spot(0, 0), MAIN));
        final List<List<Spot>> ends = new ArrayList<>(seal);
        casing.shafts().forEach(shaft -> ends.add(outlines.circle(shaft, SHAFT)));
        final List<Double> at = bulkheads();
        for (int k = 0; k < at.size(); k++) {
            final boolean end = k == 0 || k == at.size() - 1;
            made.add(new Plate(end ? section : casing.between(section), end ? ends : seal, new Vec3(0, 0, at.get(k)),
                    new Vec3(1, 0, 0), new Vec3(0, 1, 0), BULKHEAD).mesh());
        }
        for (int b = 0; b < figures.banks() && !figures.twin(); b++) {
            for (int k = 0; k < figures.perBank(); k++) {
                made.add(liner(b, k));
            }
        }
        for (final int side : List.of(1, -1)) {
            made.add(new Sleeve(mount(side), new Vec3(1, 0, 0), 3, 3.2, 1.2, ROUND).mesh());
        }
        return new Shapes(ROUND).joined(made);
    }

    /**
     * How deep a liner reaches from the deck: past the piston's reach; in a boxer or a twin, whose short deck brings
     * its bores down into the crank's swing, short of that, the pistons' skirts leaving their bores at the bottom of
     * the stroke; in a twin short too of the other bank's liner, across its narrow V ({@link TwinCase#linerFoot()}).
     */
    double deep() {
        if (figures.twin()) {
            return figures.deck() - new TwinCase(figures, this).linerFoot();
        }
        if (figures.boxer()) {
            return figures.deck() - new CrankFigures(figures).swing() - CLEAR / 2;
        }
        return figures.stroke() + figures.crown() + 3;
    }

    /** How thick a liner is: as thick as it may be, siamesed cylinders thinner. */
    double liner() {
        return Math.min(LINER, (figures.spacing() - figures.bore()) / 2 - 0.02);
    }

    /** Where the engine's mount is on side {@code side}. */
    Vec3 mount(final int side) {
        return casing().mount(side);
    }

    /** Where the bulkheads stand: at each end, and between each pair of cylinders. */
    List<Double> bulkheads() {
        if (figures.boxer()) {
            return new BoxerCase(figures, this).bulkheads(BULKHEAD);
        }
        final List<Double> at = new ArrayList<>();
        at.add(BULKHEAD / 2);
        for (int k = 0; k + 1 < figures.perBank(); k++) {
            at.add(figures.along(0, k) + figures.spacing() / 2 + (figures.banks() - 1) * figures.offset() / 2);
        }
        at.add(figures.length() - BULKHEAD / 2);
        return at;
    }

    /**
     * The wall along the block from {@code a} to {@code b} in section, bored where it is a bank's top: for the bores,
     * or in a twin for its barrels' liners.
     */
    private Mesh wall(final Spot a, final Spot b, final double length) {
        final Vec3 from = new Vec3(a.u(), a.v(), 0);
        final Vec3 to = new Vec3(b.u(), b.v(), 0);
        final double wide = to.minus(from).length();
        final double hole = figures.bore() / 2 + (figures.twin() ? liner() : 0);
        final List<List<Spot>> holes = new ArrayList<>();
        final Outlines outlines = new Outlines(ROUND);
        for (int bank = 0; bank < figures.banks(); bank++) {
            final Vec3 top = up(bank).times(top());
            final Vec3 way = to.minus(from).unit();
            final Vec3 off = top.minus(from);
            final double u = off.dot(way);
            if (off.minus(way.times(u)).length() < 0.01 && u > 0 && u < wide) {
                for (int k = 0; k < figures.perBank(); k++) {
                    holes.add(outlines.circle(new Spot(u, figures.along(bank, k)), hole));
                }
            }
        }
        return new Plate(List.of(new Spot(0, 0), new Spot(wide, 0), new Spot(wide, length), new Spot(0, length)), holes,
                from, to.minus(from), ALONG, SHELL).mesh();
    }

    /** The liner of bank {@code bank}'s cylinder {@code k}, from its deck down past the piston's reach. */
    Mesh liner(final int bank, final int k) {
        final double deep = deep();
        final Vec3 middle = up(bank).times(figures.deck() - deep / 2).plus(ALONG.times(figures.along(bank, k)));
        return new Sleeve(middle, up(bank), deep, figures.bore() / 2 + liner(), figures.bore() / 2, ROUND).mesh();
    }
}
