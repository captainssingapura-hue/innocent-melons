package melons.innocent.plants.walk;

/**
 * How far from the eye a forest's rings reach, in metres: out to {@code near} its cells are trees; out to
 * {@code boughs} they are meshes baked at boughs; out to {@code limbs}, tiles baked at limbs; and beyond, wider tiles
 * of trunks alone. For tuning: {@code -Dforest.near=}, {@code -Dforest.boughs=} and {@code -Dforest.limbs=} set
 * them for a run, 120, 200 and 600 unless said. The nearest may not be under {@value #LEAST} metres: a tree at the
 * edge of the trees has to be at boughs itself, which begin 49 m off, for it to be the same wood as the mesh beyond.
 *
 * @param near   how far the trees reach
 * @param boughs how far meshes at boughs reach
 * @param limbs  how far tiles at limbs reach
 */
public record Rings(double near, double boughs, double limbs) {

    private static final double LEAST = 90;

    public Rings {
        if (!(near >= LEAST && boughs >= near && limbs >= boughs)) {
            throw new IllegalArgumentException("a forest's rings reach " + LEAST + " m or more for its trees, then no"
                    + " less for boughs, then no less for limbs: " + near + ", " + boughs + ", " + limbs);
        }
    }

    /** These rings drawn in to {@code quality} of how far they reach, the trees no nearer than they may be. */
    public Rings scaled(final double quality) {
        final double nearest = Math.max(LEAST, near * quality);
        final double second = Math.max(nearest, boughs * quality);
        return new Rings(nearest, second, Math.max(second, limbs * quality));
    }

    /** The rings a run is told, or 120, 200 and 600 m where it is told none. */
    public Rings() {
        this(Double.parseDouble(System.getProperty("forest.near", "120")),
                Double.parseDouble(System.getProperty("forest.boughs", "200")),
                Double.parseDouble(System.getProperty("forest.limbs", "600")));
    }
}
