package melons.innocent.building.generic;

/**
 * The flights of a dog-leg stair up one storey {@code rise} high, as the housing board's stairs are: as many risers
 * as keep each no higher than {@link #RISER}, an even number, half up to its half landing and half on up to the
 * floor over; each flight's treads one fewer than its risers, the last riser of each up to the landing or the floor,
 * and each tread {@link #GOING} deep. In centimetres.
 *
 * @param rise how high the storey is, floor to floor
 */
record Flights(double rise) {

    /** How high a riser is at most, and how deep a tread. */
    static final double RISER = 17.5;
    static final double GOING = 27.5;

    Flights {
        if (!(rise > 0)) {
            throw new IllegalArgumentException("a stair up some way: " + rise);
        }
    }

    /** How many risers up it, all told. */
    int risers() {
        return 2 * (int) Math.ceil(rise / (2 * RISER) - 1e-9);
    }

    /** How high each riser is. */
    double riser() {
        return rise / risers();
    }

    /** How many treads each of its two flights has. */
    int treads() {
        return risers() / 2 - 1;
    }

    /** How far each flight runs, its treads end to end. */
    double run() {
        return treads() * GOING;
    }
}
