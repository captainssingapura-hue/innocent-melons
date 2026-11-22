package melons.innocent.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A terrain's land features, laid over its ground one over another in the order they are listed: what lies at a
 * spot is the last of them that lies there, or none. Their labels are their own: no two the same. Asked of a spot, the
 * ground there is worked out once, its slope from how high it is {@link #ACROSS} either side, across and along; a
 * sheet of heights already worked out asks each feature's coverage itself, layer by layer, with its own.
 */
public final class LandFeatures {

    /** How far either side of a spot its slope is taken over, in centimetres. */
    public static final double ACROSS = 200;

    private final Terrain terrain;
    private final List<Feature> laid;
    private final List<Feature.Coverage> coverages;

    /** {@code laid} over {@code terrain}'s ground, in turn. */
    public LandFeatures(final Terrain terrain, final List<Feature> laid) {
        this.terrain = terrain;
        this.laid = List.copyOf(laid);
        if (this.laid.stream().map(Feature::label).distinct().count() != this.laid.size()) {
            throw new IllegalArgumentException(terrain.name() + ": two features labelled the same");
        }
        final List<Feature.Coverage> covered = new ArrayList<>();
        for (final Feature feature : this.laid) {
            covered.add(feature.coverage(terrain));
        }
        this.coverages = List.copyOf(covered);
    }

    /** The terrain they lie on. */
    public Terrain terrain() {
        return terrain;
    }

    /** They, in the order they are laid. */
    public List<Feature> laid() {
        return laid;
    }

    /** Where each lies, in the order they are laid. */
    public List<Feature.Coverage> coverages() {
        return coverages;
    }

    /** What lies at ({@code x}, {@code z}): the last of them laid that lies there, or none. */
    public Optional<Feature> at(final double x, final double z) {
        final double across = terrain.height(x + ACROSS, z) - terrain.height(x - ACROSS, z);
        final double along = terrain.height(x, z + ACROSS) - terrain.height(x, z - ACROSS);
        final Sample sample = new Sample(x, z, terrain.height(x, z), Math.hypot(across, along) / (2 * ACROSS));
        for (int i = laid.size() - 1; i >= 0; i--) {
            if (coverages.get(i).covers(sample)) {
                return Optional.of(laid.get(i));
            }
        }
        return Optional.empty();
    }
}
