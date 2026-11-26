package melons.innocent.building.generic;

import java.util.List;

/**
 * A storey's plan: its structure, its shear walls, each a straight line in plan so thick, its cores' among them
 * apart, the walls round its stairs and lifts, and its slabs, each a rectangle in plan; and its cover, the walls that
 * close it in from outside and hold nothing up, each a straight run in plan, so thick, of its kind; and its stairs,
 * each up from every storey to the next ({@link DogLeg}). The same plan, storey on storey, is the building
 * ({@link Raised}). In centimetres, across and along, its front toward along.
 *
 * @param walls  its shear walls but its cores'
 * @param cores  its cores' shear walls, round its stairs and lifts
 * @param slabs  its slabs
 * @param covers its cover
 * @param stairs its stairs
 */
public record FloorPlan(List<Wall> walls, List<Wall> cores, List<Slab> slabs, List<Cover> covers,
                        List<DogLeg> stairs) {

    public FloorPlan {
        walls = List.copyOf(walls);
        cores = List.copyOf(cores);
        slabs = List.copyOf(slabs);
        covers = List.copyOf(covers);
        stairs = List.copyOf(stairs);
    }

    /** One with no cores nor stairs. */
    public FloorPlan(final List<Wall> walls, final List<Slab> slabs, final List<Cover> covers) {
        this(walls, List.of(), slabs, covers, List.of());
    }

    /** Its structure, bare: no cover. */
    public FloorPlan(final List<Wall> walls, final List<Slab> slabs) {
        this(walls, slabs, List.of());
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

    /** What a run of cover is. */
    public enum Kind {
        /** A wall with a window as wide as the run, a sill wall under it and a band of wall over it. */
        WINDOWED,
        /** A wall, its full height, no window in it. */
        SOLID,
        /** A wall half a storey high, along an open edge: an access balcony's. */
        PARAPET,
        /** A doorway as wide as the run, its door in it, and a band of wall over it up to the slab. */
        DOOR
    }

    /**
     * A run of cover in plan: its wall, along its middle line, so thick, of its kind.
     *
     * @param wall what it runs along, and how thick it is
     * @param kind what it is
     */
    public record Cover(Wall wall, Kind kind) {
    }
}
