package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Tint;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.tree.Staging;
import melons.innocent.plants.tree.Tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Trees on the card, each drawn at the stage its distance calls for ({@link Staging}): a few different trees, each
 * sent once at every stage, and drawn once for every tree of the wood planted as it. Its wood, in its kind's bark,
 * hides what is behind it and casts its shadow; its clumps are seen through, so they are drawn apart, after all
 * else, the furthest tree first. One look serves every tree of a kind, and one look each level of hiding, the clumps'
 * colour told with each draw.
 *
 * <p>The stage is chosen as for a picture 1080 pixels high in the game's view of 48 degrees: on a lower picture a tree
 * is drawn finer than it need be, never coarser. {@code -Dbench.trees.stage=fine} (or any stage) holds every tree
 * at one stage, to see it from wherever.
 */
public final class WoodDrawn implements AutoCloseable {

    /** How much the clumps hide, each drawn at the nearest of these: doubling, so few are drawn far darker. */
    private final List<Double> levels = List.of(0.01, 0.02, 0.04, 0.08, 0.16, 0.32, 0.64, 0.9);
    private static final double FIELD = 48;
    private static final int PIXELS = 1080;

    /** A tree as grown, of {@code kind}. */
    public record Grown(Kind kind, Tree tree) {
    }

    /** A tree planted: which of the grown it is, by number, and what takes it to where it stands, {@code times} big. */
    public record Planted(int grown, Mat4 placed, double times) {
    }

    private final List<Planted> planted;
    private final List<Kind> kinds;
    private final List<StagedTree> trees = new ArrayList<>();
    private final Map<Kind, Looked> barks = new EnumMap<>(Kind.class);
    private final Map<Double, Looked> hazes = new LinkedHashMap<>();
    private final Staging staging = new Staging(FIELD, PIXELS);
    private final Optional<Stage> held;
    private final Stage[] at;

    /**
     * {@code planted} of {@code grown} on {@code card}, their layouts as {@code laid} says, each at the stage its
     * distance calls for, unless {@code -Dbench.trees.stage} holds them all at one.
     */
    public WoodDrawn(final Card card, final Laid laid, final List<Grown> grown, final List<Planted> planted) {
        this(card, laid, grown, planted, new Held(System.getProperty("bench.trees.stage", "")).stage());
    }

    /** A stage every tree is held at, as it is said: by its name, any case, spaces for underscores; blank, none. */
    public record Held(String said) {

        /** The stage said, if one is. */
        public Optional<Stage> stage() {
            return said.isBlank() ? Optional.empty()
                    : Optional.of(Stage.valueOf(said.trim().toUpperCase(Locale.ROOT).replace(' ', '_')));
        }
    }

    /** The same, every tree held at {@code held} where one is given. */
    public WoodDrawn(final Card card, final Laid laid, final List<Grown> grown, final List<Planted> planted,
                     final Optional<Stage> held) {
        this.planted = List.copyOf(planted);
        this.kinds = grown.stream().map(Grown::kind).toList();
        this.at = new Stage[planted.size()];
        this.held = held;
        try {
            for (final Grown tree : grown) {
                final Surface bark = new Shades().of(new Barks().of(tree.kind()));
                if (!barks.containsKey(tree.kind())) {
                    barks.put(tree.kind(), new Looked(card, new Look(laid, bark)));
                }
                trees.add(new StagedTree(card, laid, tree.tree(), bark, levels));
            }
            for (final double hides : levels) {
                final Surface haze = new Surface(new Tint().colour(), List.of(), new Colour("#000000"), 1);
                hazes.put(hides, new Looked(card, new Look(laid, haze.clear(1 - hides).hazy())));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /**
     * Every tree's wood, drawn by {@code pass} as {@code camera} sees it, each at the stage its distance calls for:
     * the same in every pass of a frame, so a tree casts the shadow of what is drawn of it.
     */
    public void drawn(final Pass pass, final Camera camera) {
        for (int tree = 0; tree < planted.size(); tree++) {
            final Planted one = planted.get(tree);
            at[tree] = held.isPresent() ? held.get() : staging.of(tree, distance(one, camera), one.times());
            barks.get(kinds.get(one.grown())).drawn(pass, trees.get(one.grown()).wood(at[tree]), one.placed(),
                    camera, 1);
        }
    }

    /** Every tree's clumps, drawn by {@code pass} as {@code camera} sees them, the furthest tree first. */
    public void seenThrough(final Pass pass, final Camera camera) {
        final List<Integer> order = IntStream.range(0, planted.size()).boxed()
                .sorted(Comparator.comparingDouble((Integer tree) -> distance(planted.get(tree), camera)).reversed())
                .toList();
        for (final int tree : order) {
            final Planted one = planted.get(tree);
            final Stage stage = at[tree] == null ? Stage.FINE : at[tree];
            final StagedTree staged = trees.get(one.grown());
            staged.hazes(stage).forEach((hides, mesh) ->
                    hazes.get(hides).drawn(pass, mesh, one.placed(), camera, 1, staged.hue(stage)));
        }
    }

    /** The stage each tree was last drawn at, in the order planted; a tree not yet drawn, as grown. */
    public List<Stage> stages() {
        return IntStream.range(0, at.length).mapToObj(tree -> at[tree] == null ? Stage.FINE : at[tree]).toList();
    }

    /** How far the middle of {@code tree}'s height is from {@code camera}'s eye. */
    private double distance(final Planted tree, final Camera camera) {
        final Mat4 placed = tree.placed();
        final double half = trees.get(tree.grown()).height() * tree.times() / 2;
        final Vec3 middle = new Vec3(placed.at(0, 3), placed.at(1, 3) + half, placed.at(2, 3));
        return middle.minus(camera.eye()).length();
    }

    @Override
    public void close() {
        hazes.values().forEach(Looked::close);
        hazes.clear();
        trees.reversed().forEach(StagedTree::close);
        trees.clear();
        barks.values().forEach(Looked::close);
        barks.clear();
    }
}
