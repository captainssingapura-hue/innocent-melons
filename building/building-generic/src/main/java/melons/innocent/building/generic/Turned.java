package melons.innocent.building.generic;

import com.example.fxsuite.building.Structure;

import java.util.List;

/**
 * A plan moved and turned: moved {@code dx} across and {@code dz} along, then turned {@code quarters} quarter turns
 * about the middle, each across into along, along into back across. A point block's four flats are one, turned so.
 *
 * @param quarters how many quarter turns
 * @param dx       how far across it is moved first
 * @param dz       how far along
 */
record Turned(int quarters, double dx, double dz) {

    /** {@code plan}, every wall, slab, run of cover and stair in it, moved and turned. */
    FloorPlan plan(final FloorPlan plan) {
        return new FloorPlan(plan.walls().stream().map(this::wall).toList(),
                plan.cores().stream().map(this::wall).toList(),
                plan.slabs().stream().map(this::slab).toList(),
                plan.covers().stream().map(c -> new FloorPlan.Cover(wall(c.wall()), c.kind())).toList(),
                plan.stairs().stream().map(this::stair).toList());
    }

    /** {@code way}, moved and turned, as high as it was. */
    Structure.WayIn way(final Structure.WayIn way) {
        final double[] at = at(way.across(), way.along());
        return new Structure.WayIn(at[0], at[1], way.level());
    }

    /** {@code s}, its front edge moved and turned, and running in as it turns. */
    private DogLeg stair(final DogLeg s) {
        final double[] at = at(s.x(), s.z());
        final double[] in = at(s.x() + s.runX(), s.z() + s.runZ());
        return new DogLeg(at[0], at[1], Math.round(in[0] - at[0]), Math.round(in[1] - at[1]), s.wide(), s.deep());
    }

    /** {@code covers}, moved and turned. */
    List<FloorPlan.Cover> covers(final List<FloorPlan.Cover> covers) {
        return covers.stream().map(c -> new FloorPlan.Cover(wall(c.wall()), c.kind())).toList();
    }

    /** {@code walls}, moved and turned. */
    List<FloorPlan.Wall> walls(final List<FloorPlan.Wall> walls) {
        return walls.stream().map(this::wall).toList();
    }

    private FloorPlan.Wall wall(final FloorPlan.Wall w) {
        final double[] a = at(w.x0(), w.z0());
        final double[] b = at(w.x1(), w.z1());
        return new FloorPlan.Wall(a[0], a[1], b[0], b[1], w.thick(), w.name());
    }

    private FloorPlan.Slab slab(final FloorPlan.Slab s) {
        final double[] a = at(s.x0(), s.z0());
        final double[] b = at(s.x1(), s.z1());
        return new FloorPlan.Slab(Math.min(a[0], b[0]), Math.min(a[1], b[1]), Math.max(a[0], b[0]),
                Math.max(a[1], b[1]));
    }

    /** Where ({@code x}, {@code z}) is, moved and turned. */
    private double[] at(final double x, final double z) {
        double across = x + dx;
        double along = z + dz;
        for (int k = 0; k < Math.floorMod(quarters, 4); k++) {
            final double was = across;
            across = -along;
            along = was;
        }
        return new double[] {across, along};
    }
}
