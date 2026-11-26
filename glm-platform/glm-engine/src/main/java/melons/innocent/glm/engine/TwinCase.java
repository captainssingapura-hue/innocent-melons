package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * How a V twin's crankcase is cast, its cylinders' barrels cast apart and bolted on it ({@link Barrel}): its lower
 * half a V block's, a deep skirt below the crank open beneath for the sump and its sides up to each bank's shoulder;
 * over them each bank's outer wall up to a face square to its cylinder, its barrel's foot, as far out as the two
 * barrels clear each other there; the faces meeting over the crank. Its crank between a main bearing at each end,
 * its two pins between them; a boss each side on the skirt for its mount.
 *
 * @param figures the engine's figures
 * @param block   how the block is cast
 */
record TwinCase(EngineFigures figures, BlockForm block) implements Casing {

    /** How far apart the two banks' barrels, and their liners, are where they come nearest, over the middle. */
    private static final double CLEAR = 0.3;

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
     * The section, drawn across and up, round to the left, starting along the open bottom: up the skirt each side,
     * across to the shoulder where it is further in, up its bank's outer wall to its barrel's foot, and across the
     * foot to its inner side.
     */
    @Override
    public List<Spot> section() {
        final UprightCase upright = new UprightCase(figures, block);
        final double s = upright.skirt();
        final List<Spot> right = new ArrayList<>();
        final List<Spot> left = new ArrayList<>();
        for (final int b : List.of(0, 1)) {
            final List<Spot> side = figures.lean(b) > 0 ? right : left;
            final Vec3 foot = block.up(b).times(foot());
            side.addAll(upright.flank(b, s));
            side.add(spot(foot.plus(block.out(b).times(block.half()))));
            side.add(spot(foot.minus(block.out(b).times(block.half()))));
        }
        final List<Spot> all = new ArrayList<>(List.of(new Spot(-s, -UprightCase.SKIRT),
                new Spot(s, -UprightCase.SKIRT)));
        all.addAll(right);
        all.addAll(left.reversed());
        return all;
    }

    /** No bulkhead between its cylinders: its two pins lie side by side between the end bearings. */
    @Override
    public List<Spot> between(final List<Spot> section) {
        return section;
    }

    /** Where the engine's mount is on side {@code side}: on the skirt's face, a little ahead of the middle. */
    @Override
    public Vec3 mount(final int side) {
        return new UprightCase(figures, block).mount(side);
    }

    private Spot spot(final Vec3 p) {
        return new Spot(p.x(), p.y());
    }
}
