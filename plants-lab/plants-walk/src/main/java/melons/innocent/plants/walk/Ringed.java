package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;

import java.util.Optional;

/**
 * How each cell of a forest is drawn, by how far its middle is from the eye: within its rings' nearest
 * ({@link Rings}), tree by
 * tree, each at the stage its own distance calls for; beyond, as one mesh baked ahead, at boughs out to
 * their second, at limbs out to their third, and its trunks alone beyond. A cell that has gone
 * coarser comes back finer only a tenth nearer, so one at a ring's edge does not go to and fro. The distance is
 * through the air, so from high above the whole forest is far. No card.
 *
 * <p>The nearest ring is wide enough that a tree at its edge is itself at boughs (which begin 49 m off), so where
 * trees give way to a cell's mesh the two are the same wood. The rings beyond are set by what a card can hold and
 * draw, not by what could be told apart: boughs for every far cell would be thousands of millions of triangles.
 */
public final class Ringed {

    private static final double METRE = 100;
    private static final double BACK = 0.9;
    /** How high a cell's middle is taken to be: half a tree's height. */
    private static final double HALF = 750;

    private final Forested forest;
    private Rings rings;
    /** The ring each cell was last in: 0 near, then boughs, limbs, and trunks alone. */
    private final int[] ring;

    /** The cells of {@code forest}, each in no ring yet. */
    public Ringed(final Forested forest) {
        this(forest, new Rings());
    }

    /** The same, its rings reaching as {@code rings} says. */
    public Ringed(final Forested forest, final Rings rings) {
        this.forest = forest;
        this.rings = rings;
        this.ring = new int[forest.cells()];
        java.util.Arrays.fill(ring, -1);
    }

    /** Its rings from now on reach as {@code now} says. */
    public void ringed(final Rings now) {
        this.rings = now;
    }

    /** The ring a cell {@code off} metres from the eye is in. */
    private int ring(final double off) {
        return off < rings.near() ? 0 : off < rings.boughs() ? 1 : off < rings.limbs() ? 2 : 3;
    }

    /** How cell number {@code index} is drawn from {@code eye}: tree by tree, if none; or baked at a stage. */
    public Optional<Stage> wanted(final int index, final Vec3 eye) {
        final double off = forest.middle(index).plus(new Vec3(0, HALF, 0)).minus(eye).length() / METRE;
        final int now = ring(off);
        if (ring[index] < 0 || now >= ring[index]) {
            ring[index] = now;
        } else {
            // nearer than the ring it is in: finer only once it is a tenth within the ring it comes into
            final double back = off / BACK;
            ring[index] = Math.min(ring[index], ring(back));
        }
        return switch (ring[index]) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(Stage.BOUGHS);
            case 2 -> Optional.of(Stage.LIMBS);
            default -> Optional.of(Stage.WHOLE);
        };
    }
}
