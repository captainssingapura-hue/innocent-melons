package melons.innocent.terrain;

import java.util.List;

/**
 * The natural ground of a stretch of the map as a topographic map draws it, and nothing else: its contours, its spot
 * heights and its break lines, and how high the ground is outside every contour, its datum. How high the ground is
 * at a spot is read across the band of the map it lies in ({@link Banded}): on every line, the line's height; between
 * them, a smooth curve that never goes past them; smooth across every contour, turning at once across a break line.
 * What lies on the ground, and what people have made of it, are no part of it. The same every time, whoever asks.
 */
public final class Topography implements Ground {

    private final String name;
    private final Extent extent;
    private final double datum;
    private final List<Contour> contours;
    private final List<SpotHeight> spotHeights;
    private final List<BreakLine> breakLines;
    private final Banded banded;

    /**
     * {@code name}'s ground over {@code extent}: {@code datum} high outside every one of {@code contours}, which
     * never cross, {@code spotHeights} marked among them, and its {@code breakLines}.
     */
    public Topography(final String name, final Extent extent, final double datum, final List<Contour> contours,
                      final List<SpotHeight> spotHeights, final List<BreakLine> breakLines) {
        this.name = name;
        this.extent = extent;
        this.datum = datum;
        this.contours = List.copyOf(contours);
        this.spotHeights = List.copyOf(spotHeights);
        this.breakLines = List.copyOf(breakLines);
        this.banded = new Banded(new Nesting(this.contours, datum, this.spotHeights), this.breakLines, extent);
    }

    /** Its name. */
    public String name() {
        return name;
    }

    @Override
    public Extent extent() {
        return extent;
    }

    /** How high the ground is outside every contour. */
    public double datum() {
        return datum;
    }

    /** Its contours. */
    public List<Contour> contours() {
        return contours;
    }

    /** Its spot heights, as marked. */
    public List<SpotHeight> spotHeights() {
        return spotHeights;
    }

    /** Its break lines. */
    public List<BreakLine> breakLines() {
        return breakLines;
    }

    @Override
    public double height(final double x, final double z) {
        return banded.height(x, z);
    }
}
