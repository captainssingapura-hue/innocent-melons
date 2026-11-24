package melons.innocent.plants.bench;

/**
 * The outline of an area of forest on the bench, as common shapes: each says whether a spot is within it, the spot
 * given across and along from the area's middle, the area reaching one each way. For trying a whole area as one mesh
 * whose edge is its own and not a square's.
 */
public enum Outline {
    CIRCLE("A circle"),
    SQUARE("A square"),
    OVAL("An oval"),
    STAR("A star"),
    CRESCENT("A crescent");

    /** How far along an oval reaches, of how far across; a star's inner corners, of its points; and its points. */
    private static final double FLAT = 0.55;
    private static final double INNER = 0.45;
    private static final int POINTS = 5;
    /** The circle cut out of a crescent: where its middle is, across, and how far it reaches. */
    private static final double CUT_AT = 0.45;
    private static final double CUT = 0.8;

    private final String title;

    Outline(final String title) {
        this.title = title;
    }

    /** What it is called in the bench's window. */
    public String title() {
        return title;
    }

    /** Whether the spot {@code x} across and {@code z} along from the middle, each from -1 to 1, is within it. */
    public boolean holds(final double x, final double z) {
        final double round = x * x + z * z;
        return switch (this) {
            case CIRCLE -> round <= 1;
            case SQUARE -> Math.abs(x) <= 1 && Math.abs(z) <= 1;
            case OVAL -> x * x + z * z / (FLAT * FLAT) <= 1;
            case STAR -> Math.sqrt(round) <= reach(Math.atan2(x, z));
            case CRESCENT -> round <= 1 && (x - CUT_AT) * (x - CUT_AT) + z * z > CUT * CUT;
        };
    }

    /** How far a star reaches from its middle the way {@code angle} points, a point straight along. */
    private double reach(final double angle) {
        final double half = Math.PI / POINTS;
        final double folded = Math.abs(((angle % (2 * half)) + 3 * half) % (2 * half) - half);
        // the edge from a point, one out along, to the inner corner half a sector round
        final double dx = INNER * Math.cos(half) - 1;
        final double dy = INNER * Math.sin(half);
        return dy / (Math.cos(folded) * dy - Math.sin(folded) * dx);
    }
}
