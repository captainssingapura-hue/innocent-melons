package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * How a V twin's crankcase is cast, its cylinders' barrels cast apart and bolted on it ({@link Barrel}), and its
 * gearbox in it with its crank, as a motorcycle's is: a skirt round the crank and on back round the gearbox's two
 * shafts under the back bank, flat beneath for the oil pan; over it each bank's outer wall up to a face square to
 * its cylinder, its barrel's foot, as far out as the two barrels clear each other there; the faces meeting over the
 * crank. Its crank between a main bearing at each end, its two pins between them; its ends bored too for the
 * gearbox's shafts' bearings; a boss for a mount ahead under the front bank and behind round the gearbox.
 *
 * @param figures the engine's figures
 * @param block   how the block is cast
 */
record TwinCase(EngineFigures figures, BlockForm block) implements Casing {

    /** How far apart the two banks' barrels, and their liners, are where they come nearest, over the middle. */
    private static final double CLEAR = 0.3;
    private static final int ROUND = 24;

    /**
     * How far up its bank a barrel's foot is from the crank's axis: where its inner side has come clear of the
     * other barrel's, over the middle.
     */
    double foot() {
        return clearAt(block.half());
    }

    /**
     * How far up its bank a liner's foot is from the crank's axis: where it has come clear of the other bank's liner,
     * over the middle, and of the crank's swing.
     */
    double linerFoot() {
        return Math.max(clearAt(figures.bore() / 2 + block.liner()), block.clear() - 0.3);
    }

    /** How far up its bank what is {@code wide} each side of a cylinder's axis comes clear of the other bank's. */
    private double clearAt(final double wide) {
        final double half = Math.toRadians(figures.vee() / 2);
        return wide / Math.tan(half) + CLEAR / 2 / Math.sin(half);
    }

    /**
     * The section, drawn across and up, round to the left, starting along the open bottom, flat under the crank and
     * the gearbox, which the oil pan closes: up the skirt under the front bank, along each bank's outer wall to its
     * barrel's foot and across the foot to its inner side, and down behind the gearbox's shafts and round under them;
     * the smallest outline round them all, its case round its gearbox ({@link GearboxFigures}).
     */
    @Override
    public List<Spot> section() {
        final UprightCase upright = new UprightCase(figures, block);
        final GearboxFigures gearbox = new GearboxFigures(figures);
        final double s = upright.skirt();
        final double bottom = gearbox.bottom();
        final Spot counter = gearbox.countershaft();
        final List<Spot> round = new ArrayList<>(List.of(new Spot(counter.u(), bottom), new Spot(s, bottom)));
        for (final int b : List.of(0, 1)) {
            final Vec3 foot = block.up(b).times(foot());
            round.addAll(upright.flank(b, s));
            round.add(spot(foot.plus(block.out(b).times(block.half()))));
            round.add(spot(foot.minus(block.out(b).times(block.half()))));
        }
        final Outlines outlines = new Outlines(ROUND);
        round.addAll(outlines.circle(gearbox.mainshaft(), gearbox.mainCase()));
        round.addAll(outlines.circle(counter, gearbox.counterCase()));
        final List<Spot> hull = new Hull(round.stream().filter(p -> p.v() >= bottom).toList()).outline();
        // begin along the bottom, from its end toward -x, as a section does
        int first = -1;
        for (int i = 0; i < hull.size(); i++) {
            if (hull.get(i).v() < bottom + 1e-9 && (first < 0 || hull.get(i).u() < hull.get(first).u())) {
                first = i;
            }
        }
        final List<Spot> section = new ArrayList<>(hull.subList(first, hull.size()));
        section.addAll(hull.subList(0, first));
        return section;
    }

    /** Where its gearbox's shafts go through its ends, each in its bearing: the mainshaft and the countershaft. */
    @Override
    public List<Spot> shafts() {
        final GearboxFigures gearbox = new GearboxFigures(figures);
        return List.of(gearbox.mainshaft(), gearbox.countershaft());
    }

    /** No bulkhead between its cylinders: its two pins lie side by side between the end bearings. */
    @Override
    public List<Spot> between(final List<Spot> section) {
        return section;
    }

    /**
     * Where the engine's mount is on side {@code side}: ahead, toward +x, on the skirt's face under the front bank;
     * behind, on the case's back round the countershaft; each a little ahead of the middle along the crank.
     */
    @Override
    public Vec3 mount(final int side) {
        if (side > 0) {
            return new UprightCase(figures, block).mount(side);
        }
        final GearboxFigures gearbox = new GearboxFigures(figures);
        final Spot counter = gearbox.countershaft();
        return new Vec3(counter.u() - gearbox.counterCase() - 1.5, counter.v(), figures.length() * 0.42);
    }

    private Spot spot(final Vec3 p) {
        return new Spot(p.x(), p.y());
    }
}
