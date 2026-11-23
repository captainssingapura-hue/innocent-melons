package melons.innocent.terrain.bench;

/**
 * The bands of height the eye is kept within, each {@code storey} high from the sea's level up, and how finely the
 * land is seen from each: as finely as two pixels cover looking straight down from the top of the band, a pixel
 * taking in {@code pixel} of the view's angle. Within a band the land is seen the one way wherever the eye goes, so
 * the view stays as it is; it changes only as the eye is moved to another band. In centimetres.
 *
 * @param storey how high each band is
 * @param pixel  how much of the view's angle a pixel takes in, in radians
 */
public record Altitudes(double storey, double pixel) {

    public Altitudes {
        if (!(storey > 0) || !(pixel > 0)) {
            throw new IllegalArgumentException("bands of some height, pixels of some size: " + storey + ", " + pixel);
        }
    }

    /** Which band the eye is in, {@code height} over the sea's level: the first, from the sea up, is the 0th. */
    public int band(final double height) {
        return Math.max(0, (int) Math.floor(height / storey));
    }

    /** How high band {@code band} begins. */
    public double floor(final int band) {
        return band * storey;
    }

    /** How high band {@code band} ends. */
    public double top(final int band) {
        return (band + 1) * storey;
    }

    /** {@code height} held within band {@code band}, a centimetre short of its top. */
    public double within(final int band, final double height) {
        return Math.clamp(height, floor(band), top(band) - 1);
    }

    /** How finely the land is seen from band {@code band}: what two pixels cover, straight down from its top. */
    public double scale(final int band) {
        return 2 * top(band) * pixel;
    }
}
