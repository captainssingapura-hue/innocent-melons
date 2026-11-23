package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Land features laid over a ground and along a map's lines one over another in the order they are listed: what lies
 * at a spot is the last of them that lies there, or none. Their labels are their own: no two the same. Asked of a
 * spot, the ground there is worked out once, its slope from how high it is {@link #ACROSS} either side, across and
 * along; a sheet of heights already worked out asks each feature's coverage itself, layer by layer, with its own.
 */
public final class LandFeatures {

    /** How far either side of a spot its slope is taken over, in centimetres. */
    public static final double ACROSS = 200;

    private final String name;
    private final Ground ground;
    private final Graph<Spot, Course> map;
    private final List<Feature> laid;
    private final List<Feature.Coverage> coverages;
    private final List<WaterBody.Level> levels;

    /**
     * {@code laid} over {@code name}'s {@code ground}, along {@code map}'s lines, in turn. They only read the ground:
     * none changes how high it is.
     */
    public LandFeatures(final String name, final Ground ground, final Graph<Spot, Course> map,
                        final List<Feature> laid) {
        this.name = name;
        this.ground = ground;
        this.map = map;
        this.laid = List.copyOf(laid);
        if (this.laid.stream().map(Feature::label).distinct().count() != this.laid.size()) {
            throw new IllegalArgumentException(name + ": two features labelled the same");
        }
        final List<Feature.Coverage> covered = new ArrayList<>();
        for (final Feature feature : this.laid) {
            covered.add(feature.coverage(map, ground));
        }
        this.coverages = List.copyOf(covered);
        final List<WaterBody.Level> water = new ArrayList<>();
        for (final Feature feature : this.laid) {
            if (feature instanceof WaterBody body) {
                water.add(body.level(map, ground));
            }
        }
        this.levels = List.copyOf(water);
    }

    /** The name of the land they lie on. */
    public String name() {
        return name;
    }

    /** The ground they lie on. */
    public Ground ground() {
        return ground;
    }

    /** The map's lines they lie along. */
    public Graph<Spot, Course> map() {
        return map;
    }

    /** They, in the order they are laid. */
    public List<Feature> laid() {
        return laid;
    }

    /** Where each lies, in the order they are laid. */
    public List<Feature.Coverage> coverages() {
        return coverages;
    }

    /**
     * How high the water's top is at ({@code x}, {@code z}), of whichever of its water bodies reaches there, the
     * highest if more than one does, whether the ground there is under it or not: not a number if none reaches there.
     */
    public double water(final double x, final double z) {
        double top = Double.NaN;
        for (final WaterBody.Level level : levels) {
            final double at = level.at(x, z);
            if (!Double.isNaN(at) && (Double.isNaN(top) || at > top)) {
                top = at;
            }
        }
        return top;
    }

    /** What lies at ({@code x}, {@code z}): the last of them laid that lies there, or none. */
    public Optional<Feature> at(final double x, final double z) {
        final double across = ground.height(x + ACROSS, z) - ground.height(x - ACROSS, z);
        final double along = ground.height(x, z + ACROSS) - ground.height(x, z - ACROSS);
        final Sample sample = new Sample(x, z, ground.height(x, z), Math.hypot(across, along) / (2 * ACROSS));
        for (int i = laid.size() - 1; i >= 0; i--) {
            if (coverages.get(i).covers(sample)) {
                return Optional.of(laid.get(i));
            }
        }
        return Optional.empty();
    }
}
