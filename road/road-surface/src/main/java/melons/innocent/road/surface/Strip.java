package melons.innocent.road.surface;

/**
 * A strip of a road's surface, across it, so wide: a lane, going the way the road's line goes or against it; a
 * shoulder beside the lanes, paved and kept clear; a kerb, raised along an edge; a barrier, a low wall of concrete
 * keeping traffic in or one way from the other. In centimetres.
 *
 * @param kind    what it is
 * @param wide    how wide it is
 * @param against whether, a lane, it goes against the road's line
 */
public record Strip(Kind kind, double wide, boolean against) {

    /** What a strip is. */
    public enum Kind {
        LANE, SHOULDER, KERB, BARRIER
    }

    public Strip {
        if (kind == null || !(wide > 0)) {
            throw new IllegalArgumentException("a strip of some kind, some way wide: " + kind + ", " + wide);
        }
    }

    /** A strip going the road's way, or none. */
    public Strip(final Kind kind, final double wide) {
        this(kind, wide, false);
    }

    /** Whether it is paved, driven on: a lane or a shoulder. */
    public boolean paved() {
        return kind == Kind.LANE || kind == Kind.SHOULDER;
    }
}
