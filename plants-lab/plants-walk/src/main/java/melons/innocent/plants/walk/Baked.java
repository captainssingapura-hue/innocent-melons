package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Pruned;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.tree.Wooded;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Trees merged ahead into the meshes of one whole, with no card: each tree's wood at a level, moved where it stands,
 * one mesh for each kind of tree; and what the level drops of each, its crown's haze, one mesh for each grown tree
 * and level of hiding, in the colour its bark comes to over the wood dropped. As a far area of a wood might be
 * baked: the trees as they stand, whatever outline they stand within. A whole of more than {@value #MOST} triangles
 * is refused, as a level too fine for an area that size. Each grown tree at each level is worked out once and kept.
 *
 * @param grown  the trees as grown, by their numbers
 * @param levels how much a haze hides, each clump at the nearest of these
 */
public record Baked(List<WoodDrawn.Grown> grown, List<Double> levels, Map<String, Staged> kept) {

    /** The most triangles one whole may have. */
    private static final long MOST = 12_000_000;

    /** A grown tree at a level: its wood, its crown's haze by level of hiding, and the haze's colour. */
    public record Staged(Mesh wood, Map<Double, Mesh> hazes, Vec3 hue) {
    }

    /** A haze of the whole: its mesh, how much it hides, and its colour. */
    public record Haze(Mesh mesh, double hides, Vec3 hue) {
    }

    /** One whole: its wood by kind of tree, and its hazes. */
    public record Whole(Map<Kind, Mesh> wood, List<Haze> hazes) {
    }

    public Baked(final List<WoodDrawn.Grown> grown, final List<Double> levels) {
        this(List.copyOf(grown), List.copyOf(levels), new ConcurrentHashMap<>());
    }

    /** {@code planted} at {@code level}, as one whole; refused, if it would be too many triangles. */
    public Whole whole(final List<WoodDrawn.Planted> planted, final Stage level) {
        long triangles = 0;
        final Map<Kind, List<Merged.Placed>> wood = new EnumMap<>(Kind.class);
        final Map<String, List<Merged.Placed>> hazes = new LinkedHashMap<>();
        final Map<String, Haze> of = new LinkedHashMap<>();
        for (final WoodDrawn.Planted tree : planted) {
            final Staged staged = staged(tree.grown(), level);
            triangles += staged.wood().triangles();
            if (triangles > MOST) {
                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "%d trees at %s are over %,d "
                        + "triangles: too fine a level for one mesh of this size", planted.size(),
                        level.name().toLowerCase(Locale.ROOT).replace('_', ' '), MOST));
            }
            wood.computeIfAbsent(grown.get(tree.grown()).kind(), kind -> new ArrayList<>())
                    .add(new Merged.Placed(staged.wood(), tree.placed()));
            staged.hazes().forEach((hides, mesh) -> {
                final String key = tree.grown() + " " + hides;
                hazes.computeIfAbsent(key, k -> new ArrayList<>()).add(new Merged.Placed(mesh, tree.placed()));
                of.putIfAbsent(key, new Haze(mesh, hides, staged.hue()));
            });
        }
        final Map<Kind, Mesh> merged = new EnumMap<>(Kind.class);
        wood.forEach((kind, parts) -> merged.put(kind, new Merged(parts).mesh()));
        final List<Haze> hazed = new ArrayList<>();
        hazes.forEach((key, parts) -> hazed.add(new Haze(new Merged(parts).mesh(), of.get(key).hides(),
                of.get(key).hue())));
        return new Whole(merged, hazed);
    }

    /** {@code planted} at {@code level}, its wood alone: one mesh for each kind of tree, no hazes, no limit. */
    public Map<Kind, Mesh> wood(final List<WoodDrawn.Planted> planted, final Stage level) {
        final Map<Kind, List<Merged.Placed>> wood = new EnumMap<>(Kind.class);
        for (final WoodDrawn.Planted tree : planted) {
            wood.computeIfAbsent(grown.get(tree.grown()).kind(), kind -> new ArrayList<>())
                    .add(new Merged.Placed(staged(tree.grown(), level).wood(), tree.placed()));
        }
        final Map<Kind, Mesh> merged = new EnumMap<>(Kind.class);
        wood.forEach((kind, parts) -> merged.put(kind, new Merged(parts).mesh()));
        return merged;
    }

    /** Grown tree number {@code number} at {@code level}: worked out once, and kept. */
    private Staged staged(final int number, final Stage level) {
        return kept.computeIfAbsent(number + " " + level, key -> {
            final Pruned pruned = new Pruned(grown.get(number).tree(), level);
            final Vec3 hue = pruned.dropped().isEmpty() ? new Vec3(1, 1, 1)
                    : new Afar(new Shades().of(new Barks().of(grown.get(number).kind())))
                    .over(new Wooded(pruned.dropped()).formed());
            return new Staged(pruned.wood(), pruned.hazes(levels), hue);
        });
    }
}
