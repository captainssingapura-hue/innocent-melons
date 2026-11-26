package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A large forest on level ground, square, its middle at the world's, cut into square cells: the trees of a cell are
 * worked out from which cell it is and from nothing else, the same every time it is asked, so the forest is never
 * held as a list and could as well have no end. In each cell, trees some seven metres apart in rows, each a little
 * off its place, turned any way and a little bigger or smaller, each one of the few trees grown. Centimetres.
 *
 * @param side   how far across the forest is, each way
 * @param cell   how far across a cell is
 * @param grown  how many different trees are grown, of which each tree standing is one
 * @param chance what its trees are worked out from, with the cell
 */
public record Forested(double side, double cell, int grown, long chance) {

    private static final double APART = 700;
    private static final double ASTRAY = 0.3;
    private static final double SMALLEST = 0.85;
    private static final double BIGGEST = 1.1;
    private static final double FULL = 360;
    private static final long MIXED = 1_000_003L;

    /** How many cells lie along each side. */
    public int along() {
        return (int) Math.max(1, Math.round(side / cell));
    }

    /** How many cells there are. */
    public int cells() {
        return along() * along();
    }

    /** Where cell number {@code index} begins: its least corner across and along, on the ground. */
    public Vec3 corner(final int index) {
        final double each = side / along();
        return new Vec3(index % along() * each - side / 2, 0, index / along() * each - side / 2);
    }

    /** The middle of cell number {@code index}, on the ground. */
    public Vec3 middle(final int index) {
        final double half = side / along() / 2;
        return corner(index).plus(new Vec3(half, 0, half));
    }

    /** The trees of cell number {@code index}, where each stands in the world. */
    public List<WoodDrawn.Planted> planted(final int index) {
        final Random chance = new Random(this.chance * 31 + index * MIXED);
        final double each = side / along();
        final int rows = (int) Math.max(1, Math.round(each / APART));
        final double apart = each / rows;
        final Vec3 corner = corner(index);
        final List<WoodDrawn.Planted> planted = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int along = 0; along < rows; along++) {
                final Vec3 at = corner.plus(new Vec3((along + 0.5 + (chance.nextDouble() * 2 - 1) * ASTRAY) * apart,
                        0, (row + 0.5 + (chance.nextDouble() * 2 - 1) * ASTRAY) * apart));
                final double times = SMALLEST + chance.nextDouble() * (BIGGEST - SMALLEST);
                planted.add(new WoodDrawn.Planted(chance.nextInt(grown), new Mat4(at)
                        .times(new Turned(new Vec3(0, 1, 0), chance.nextDouble() * FULL).matrix())
                        .times(new Sized(times).matrix()), times));
            }
        }
        return planted;
    }
}
