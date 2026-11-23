package melons.innocent.building.generic;

import java.util.List;

/**
 * A storey's plan of its structure: its shear walls, each a straight line in plan, so thick; and its slabs, each a
 * rectangle in plan. The same plan, storey on storey, is the building: its walls raised from each floor to the slab
 * over it ({@link Raised}). In centimetres, across and along, its front toward along.
 *
 * @param walls its shear walls
 * @param slabs its slabs
 */
public record FloorPlan(List<Wall> walls, List<Slab> slabs) {

    public FloorPlan {
        walls = List.copyOf(walls);
        slabs = List.copyOf(slabs);
    }

    /**
     * A shear wall in plan: its middle line, from one point to another, and how thick it is either side of it all told.
     *
     * @param x0    where it begins, across
     * @param z0    where it begins, along
     * @param x1    where it ends, across
     * @param z1    where it ends, along
     * @param thick how thick it is
     * @param name  what it is called
     */
    public record Wall(double x0, double z0, double x1, double z1, double thick, String name) {

        public Wall {
            if (!(Math.hypot(x1 - x0, z1 - z0) > 0) || !(thick > 0)) {
                throw new IllegalArgumentException(name + ": a wall some way long and some way thick");
            }
        }

        /** How long it is. */
        public double length() {
            return Math.hypot(x1 - x0, z1 - z0);
        }
    }

    /**
     * A slab in plan: a rectangle, square to the plan.
     *
     * @param x0 its edge furthest left, across
     * @param z0 its edge nearest, along
     * @param x1 its edge furthest right
     * @param z1 its edge furthest off
     */
    public record Slab(double x0, double z0, double x1, double z1) {

        public Slab {
            if (!(x1 > x0) || !(z1 > z0)) {
                throw new IllegalArgumentException("a slab of some size each way: " + x0 + ".." + x1 + ", " + z0
                        + ".." + z1);
            }
        }
    }
}
