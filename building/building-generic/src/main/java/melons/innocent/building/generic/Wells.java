package melons.innocent.building.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Slabs with stairs' wells cut through them: each slab, less where any of {@code wells} goes through it, as the
 * rectangles of it round the well, square to the plan as every slab is; a slab no well goes through as it is.
 *
 * @param wells where the wells go through, in plan
 */
record Wells(List<Box> wells) {

    Wells {
        wells = List.copyOf(wells);
    }

    /** {@code slabs}, the wells cut out of them. */
    List<FloorPlan.Slab> of(final List<FloorPlan.Slab> slabs) {
        List<FloorPlan.Slab> cut = slabs;
        for (final Box well : wells) {
            cut = cut.stream().flatMap(s -> less(s, well).stream()).toList();
        }
        return cut;
    }

    /** {@code slab} less {@code well}: either side of it the slab's whole depth, and in front of it and behind. */
    private List<FloorPlan.Slab> less(final FloorPlan.Slab slab, final Box well) {
        if (well.x1() <= slab.x0() || well.x0() >= slab.x1() || well.z1() <= slab.z0() || well.z0() >= slab.z1()) {
            return List.of(slab);
        }
        final double x0 = Math.max(slab.x0(), well.x0());
        final double x1 = Math.min(slab.x1(), well.x1());
        final List<FloorPlan.Slab> left = new ArrayList<>();
        add(left, slab.x0(), slab.z0(), x0, slab.z1());
        add(left, x1, slab.z0(), slab.x1(), slab.z1());
        add(left, x0, slab.z0(), x1, well.z0());
        add(left, x0, well.z1(), x1, slab.z1());
        return left;
    }

    /** A rectangle of slab added to {@code left}, if it is any size each way. */
    private void add(final List<FloorPlan.Slab> left, final double x0, final double z0, final double x1,
                     final double z1) {
        if (x1 - x0 > 1e-6 && z1 - z0 > 1e-6) {
            left.add(new FloorPlan.Slab(x0, z0, x1, z1));
        }
    }
}
