package melons.innocent.road.bridge;

/**
 * How a spiral ramp's turns are held up: each on beams across under it, from a ring of columns outside its outer edge
 * to a core walled round its middle, inside its inner edge ({@link #CORE}); or to a second ring of columns inside its
 * inner edge ({@link #COLUMNS}).
 */
public enum Carried {
    CORE, COLUMNS
}
