package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * The wood of a tree as one mesh: each bough a round bar that thins as the bough does, bending where the bough
 * bends, closed at its far end. A thick bough is round in many flat sides and a twig in few, since a twig is
 * never seen so close that it matters. Where a bough forks, the two that leave it begin inside it, so nothing
 * shows between them.
 */
public record Wooded(List<Bough> boughs) {

    private static final double FULL = 2 * Math.PI;

    public Wooded {
        boughs = List.copyOf(boughs);
    }

    /** The wood of {@code tree}. */
    public Wooded(final Tree tree) {
        this(tree.boughs());
    }

    /** The mesh. */
    public Mesh formed() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        boughs.forEach(bough -> barred(forming, bough));
        return forming.formed();
    }

    /** How many flat sides a bough so thick is round in. */
    public int sides(final double thick) {
        return thick >= 8 ? 12 : thick >= 2 ? 8 : thick >= 0.8 ? 6 : 4;
    }

    /** One bough added to {@code forming}: a ring of points round it at each of its points, and the faces between. */
    private void barred(final Forming forming, final Bough bough) {
        final int sides = sides(bough.thick().getFirst());
        final int count = bough.points().size();
        final int[] rings = new int[count];
        Vec3 across = null;
        for (int point = 0; point < count; point++) {
            final Vec3 along = bough.points().get(Math.min(point + 1, count - 1))
                    .minus(bough.points().get(Math.max(point - 1, 0))).unit();
            // the ring is carried along the bough without turning about it, so its faces do not twist
            final Vec3 was = across == null ? (Math.abs(along.y()) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0))
                    : across;
            across = was.minus(along.times(was.dot(along))).unit();
            final Vec3 other = along.cross(across);
            rings[point] = -1;
            for (int side = 0; side < sides; side++) {
                final double round = FULL * side / sides;
                final Vec3 out = across.times(Math.cos(round)).plus(other.times(Math.sin(round)));
                final int added = forming.point(bough.points().get(point).plus(out.times(bough.thick().get(point))),
                        out);
                rings[point] = rings[point] < 0 ? added : rings[point];
            }
        }
        for (int point = 0; point + 1 < count; point++) {
            for (int side = 0; side < sides; side++) {
                final int next = (side + 1) % sides;
                forming.faced(rings[point] + side, rings[point + 1] + side, rings[point + 1] + next);
                forming.faced(rings[point] + side, rings[point + 1] + next, rings[point] + next);
            }
        }
        // its far end closed: a point at its middle there, facing the way it was going
        final Vec3 tip = bough.ends().minus(bough.points().get(count - 2)).unit();
        final int middle = forming.point(bough.ends(), tip);
        for (int side = 0; side < sides; side++) {
            forming.faced(middle, rings[count - 1] + side, rings[count - 1] + (side + 1) % sides);
        }
    }
}
