package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * The wood of a tree as one mesh: each bough a round bar that thins as the bough does, bending where the bough
 * bends, closed at its far end. A thick bough is round in many flat sides and a twig in few, since a twig is
 * never seen so close that it matters. Where a bough forks, the two that leave it begin inside it, so nothing
 * shows between them. For a tree seen from further off, the wood may be rounded in so many times fewer sides
 * ({@code fewer}), never fewer than three.
 */
public record Wooded(List<Bough> boughs, int fewer) {

    /** The fewest flat sides a bough is round in. */
    private static final int FEWEST = 3;

    private static final double FULL = 2 * Math.PI;
    /**
     * How far apart along the wood, in the second of a point's numbers on its bark, two boughs one after another are
     * set; and after how many boughs the setting begins again, to keep the numbers small enough to be exact.
     */
    private static final double APART = 131;
    private static final int AGAIN = 64;

    public Wooded {
        boughs = List.copyOf(boughs);
        if (fewer < 1) {
            throw new IllegalArgumentException("wood is rounded in as many sides as grown or fewer, not " + fewer);
        }
    }

    /** {@code boughs}, rounded in as many sides as grown. */
    public Wooded(final List<Bough> boughs) {
        this(boughs, 1);
    }

    /** The wood of {@code tree}. */
    public Wooded(final Tree tree) {
        this(tree.boughs());
    }

    /**
     * The mesh. Each point says where on its bough's bark it is, for the bark's marks to follow the wood: how far
     * round the bough, as measured on its bark; how far along it, from where it leaves the bough before it, set on
     * by a stretch of the bough's own so that no two boughs near one another are marked alike, the trunk's nothing;
     * and, third, how thick the bough is there, from its middle to its bark, for a bark that changes as the wood
     * thins. All are in centimetres.
     */
    public Mesh formed() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        for (int bough = 0; bough < boughs.size(); bough++) {
            barred(forming, boughs.get(bough), bough % AGAIN * APART);
        }
        return forming.formed();
    }

    /** How many flat sides a bough so thick is round in. */
    public int sides(final double thick) {
        return thick >= 8 ? 12 : thick >= 2 ? 8 : thick >= 0.8 ? 6 : 4;
    }

    /**
     * One bough added to {@code forming}: a ring of points round it at each of its points, and the faces between.
     * A ring's first point is there twice, once at the start of the way round and once at its end, so the bark's
     * marks have no seam where the way round begins again.
     */
    private void barred(final Forming forming, final Bough bough, final double own) {
        final int sides = Math.max(FEWEST, sides(bough.thick().getFirst()) / fewer);
        final int count = bough.points().size();
        final int[] rings = new int[count];
        Vec3 across = null;
        double run = own;
        for (int point = 0; point < count; point++) {
            run += point == 0 ? 0 : bough.points().get(point).minus(bough.points().get(point - 1)).length();
            final Vec3 along = bough.points().get(Math.min(point + 1, count - 1))
                    .minus(bough.points().get(Math.max(point - 1, 0))).unit();
            // the ring is carried along the bough without turning about it, so its faces do not twist
            final Vec3 was = across == null ? (Math.abs(along.y()) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0))
                    : across;
            across = was.minus(along.times(was.dot(along))).unit();
            final Vec3 other = along.cross(across);
            final double thick = bough.thick().get(point);
            rings[point] = -1;
            for (int side = 0; side <= sides; side++) {
                final double round = FULL * side / sides;
                final Vec3 out = across.times(Math.cos(round)).plus(other.times(Math.sin(round)));
                final int added = forming.point(bough.points().get(point).plus(out.times(thick)), out,
                        new Vec3(round * thick, run, thick));
                rings[point] = rings[point] < 0 ? added : rings[point];
            }
        }
        for (int point = 0; point + 1 < count; point++) {
            for (int side = 0; side < sides; side++) {
                forming.faced(rings[point] + side, rings[point + 1] + side, rings[point + 1] + side + 1);
                forming.faced(rings[point] + side, rings[point + 1] + side + 1, rings[point] + side + 1);
            }
        }
        // its far end closed: a point at its middle there, facing the way it was going
        final Vec3 tip = bough.ends().minus(bough.points().get(count - 2)).unit();
        final int middle = forming.point(bough.ends(), tip, new Vec3(0, run, bough.thick().getLast()));
        for (int side = 0; side < sides; side++) {
            forming.faced(middle, rings[count - 1] + side, rings[count - 1] + side + 1);
        }
    }
}
