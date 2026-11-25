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
 * as y, across as x. Its section, the same all along: each bank's deck standing as high over the crank as the figures
 * say, as wide as a cylinder with its liner, its water's jacket and its wall; its outer walls falling from the decks
 * to a deep skirt below the crank, open beneath for the sump; in a V, the valley between the banks closed low over
 * the crank and open above, where the turbochargers go. That section's walls carried the block's length, each deck
 * bored through for its bank's cylinders; a liner in each bore down past the piston's reach; its ends closed but for
 * the crank's seals; a bulkhead across between each pair of cylinders and at each end, bored for the crank's main
 * bearing; and a boss each side for the engine's mount.
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
    /** How far the skirt reaches under the crank; how far the valley stands over the crank's swing. */
    private static final double SKIRT = 7;
    private static final double SWING = 7.5;
    /** How far round a main bearing's bore is; how far down a bank's outer wall runs straight. */
    private static final double MAIN = 3.4;
    private static final double OUTER = 0.35;

    /** How wide a bank is, from its cylinders' axis out to its wall. */
    double half() {
        return figures.bore() / 2 + liner() + JACKET + WALL;
    }

    /** The way bank {@code bank}'s cylinders point, up from the crank. */
    Vec3 up(final int bank) {
        final double a = Math.toRadians(figures.lean(bank));
        return new Vec3(Math.sin(a), Math.cos(a), 0);
    }

    /** The way out of bank {@code bank}, square to its cylinders, toward its outer wall. */
    Vec3 out(final int bank) {
        final double a = Math.toRadians(figures.lean(bank));
        final Vec3 n = new Vec3(Math.cos(a), -Math.sin(a), 0);
        return figures.lean(bank) < 0 ? n.times(-1) : n;
    }

    /** How far across the skirt reaches each side. */
    double skirt() {
        double widest = figures.stroke() / 2 + 5;
        for (int b = 0; b < figures.banks(); b++) {
            widest = Math.max(widest, Math.abs(up(b).times(figures.deck() * OUTER).plus(out(b).times(half())).x()));
        }
        return widest;
    }

    /** The section, drawn across and up, round to the left, starting along the open bottom. */
    List<Spot> section() {
        final double s = skirt();
        final List<Spot> right = new ArrayList<>();
        final List<Spot> left = new ArrayList<>();
        if (figures.banks() == 1) {
            right.add(spot(up(0).times(figures.deck() * OUTER).plus(out(0).times(half()))));
            right.add(spot(up(0).times(figures.deck()).plus(out(0).times(half()))));
            final List<Spot> all = new ArrayList<>(List.of(new Spot(-s, -SKIRT), new Spot(s, -SKIRT)));
            all.addAll(right);
            all.addAll(right.reversed().stream().map(p -> new Spot(-p.u(), p.v())).toList());
            return all;
        }
        for (final int b : List.of(0, 1)) {
            final List<Spot> side = b == 0 ? right : left;
            final Vec3 deck = up(b).times(figures.deck());
            side.add(spot(up(b).times(figures.deck() * OUTER).plus(out(b).times(half()))));
            side.add(spot(deck.plus(out(b).times(half()))));
            final Vec3 inner = deck.minus(out(b).times(half()));
            side.add(spot(inner));
            final double floor = Math.max(SWING + figures.stroke() / 2,
                    inner.y() - inner.x() / up(b).x() * up(b).y() + 1.5);
            final double t = (inner.y() - floor) / up(b).y();
            side.add(spot(inner.minus(up(b).times(t))));
        }
        final List<Spot> all = new ArrayList<>(List.of(new Spot(-s, -SKIRT), new Spot(s, -SKIRT)));
        all.addAll(right);
        all.addAll(left.reversed());
        return all;
    }

    /** It. */
    Mesh mesh() {
        final List<Spot> section = section();
        final double length = figures.length();
        final List<Mesh> made = new ArrayList<>();
        for (int k = 1; k < section.size(); k++) {
            final Spot a = section.get(k);
            final Spot b = section.get((k + 1) % section.size());
            made.add(wall(a, b, length));
        }
        final Outlines outlines = new Outlines(ROUND);
        final List<List<Spot>> seal = List.of(outlines.circle(new Spot(0, 0), MAIN));
        final List<Double> at = bulkheads();
        for (int k = 0; k < at.size(); k++) {
            final boolean end = k == 0 || k == at.size() - 1;
            made.add(new Plate(end || figures.banks() > 1 ? section : web(), seal, new Vec3(0, 0, at.get(k)),
                    new Vec3(1, 0, 0), new Vec3(0, 1, 0), BULKHEAD).mesh());
        }
        for (int b = 0; b < figures.banks(); b++) {
            for (int k = 0; k < figures.perBank(); k++) {
                made.add(liner(b, k));
            }
        }
        for (final int side : List.of(1, -1)) {
            made.add(new Sleeve(mount(side), new Vec3(1, 0, 0), 3, 3.2, 1.2, ROUND).mesh());
        }
        return new Shapes(ROUND).joined(made);
    }

    /** A web between siamesed cylinders in line: the section up to the liners' feet, under the cylinders. */
    private List<Spot> web() {
        final double s = skirt();
        final double top = figures.deck() - deep();
        return List.of(new Spot(-s, -SKIRT), new Spot(s, -SKIRT), new Spot(s, top), new Spot(-s, top));
    }

    /** How deep a liner reaches from the deck: past the piston's reach. */
    private double deep() {
        return figures.stroke() + figures.crown() + 3;
    }

    /** How thick a liner is: as thick as it may be, siamesed cylinders thinner. */
    private double liner() {
        return Math.min(LINER, (figures.spacing() - figures.bore()) / 2 - 0.02);
    }

    /** Where the engine's mount is on side {@code side}: on the skirt's face, a little ahead of the middle. */
    Vec3 mount(final int side) {
        return new Vec3(side * (skirt() + 1.5), -1, figures.length() * 0.42);
    }

    /** Where the bulkheads stand: at each end, and between each pair of cylinders. */
    List<Double> bulkheads() {
        final List<Double> at = new ArrayList<>();
        at.add(BULKHEAD / 2);
        for (int k = 0; k + 1 < figures.perBank(); k++) {
            at.add(figures.along(0, k) + figures.spacing() / 2 + (figures.banks() - 1) * figures.offset() / 2);
        }
        at.add(figures.length() - BULKHEAD / 2);
        return at;
    }

    /** The wall along the block from {@code a} to {@code b} in section, bored where it is a bank's deck. */
    private Mesh wall(final Spot a, final Spot b, final double length) {
        final Vec3 from = new Vec3(a.u(), a.v(), 0);
        final Vec3 to = new Vec3(b.u(), b.v(), 0);
        final double wide = to.minus(from).length();
        final List<List<Spot>> holes = new ArrayList<>();
        final Outlines outlines = new Outlines(ROUND);
        for (int bank = 0; bank < figures.banks(); bank++) {
            final Vec3 deck = up(bank).times(figures.deck());
            final Vec3 way = to.minus(from).unit();
            final Vec3 off = deck.minus(from);
            final double u = off.dot(way);
            if (off.minus(way.times(u)).length() < 0.01 && u > 0 && u < wide) {
                for (int k = 0; k < figures.perBank(); k++) {
                    holes.add(outlines.circle(new Spot(u, figures.along(bank, k)), figures.bore() / 2));
                }
            }
        }
        return new Plate(List.of(new Spot(0, 0), new Spot(wide, 0), new Spot(wide, length), new Spot(0, length)), holes,
                from, to.minus(from), ALONG, SHELL).mesh();
    }

    /** The liner of bank {@code bank}'s cylinder {@code k}, from its deck down past the piston's reach. */
    private Mesh liner(final int bank, final int k) {
        final double deep = figures.stroke() + figures.crown() + 3;
        final Vec3 middle = up(bank).times(figures.deck() - deep / 2).plus(ALONG.times(figures.along(bank, k)));
        return new Sleeve(middle, up(bank), deep, figures.bore() / 2 + liner(), figures.bore() / 2, ROUND).mesh();
    }

    private Spot spot(final Vec3 p) {
        return new Spot(p.x(), p.y());
    }
}
