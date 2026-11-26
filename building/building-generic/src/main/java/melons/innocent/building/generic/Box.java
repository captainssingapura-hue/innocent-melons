package melons.innocent.building.generic;

/**
 * A rectangle in plan, square to it: a pillar's, most often. In centimetres.
 *
 * @param x0 its edge furthest left, across
 * @param z0 its edge nearest, along
 * @param x1 its edge furthest right
 * @param z1 its edge furthest off
 */
record Box(double x0, double z0, double x1, double z1) {

    /** It as a shear wall called {@code name}: along its longer way, as thick as its shorter. */
    FloorPlan.Wall wall(final String name) {
        final double mx = (x0 + x1) / 2;
        final double mz = (z0 + z1) / 2;
        return x1 - x0 >= z1 - z0
                ? new FloorPlan.Wall(x0, mz, x1, mz, z1 - z0, name)
                : new FloorPlan.Wall(mx, z0, mx, z1, x1 - x0, name);
    }
}
