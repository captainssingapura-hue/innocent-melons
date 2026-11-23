package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Copies;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.layout.Filling;
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
 * sent once at every stage, and every tree of the wood planted as one of them. All the trees planted as one grown
 * tree that stand at one stage are drawn in one draw, each a copy ({@link Copies}): so a wood costs a few dozen draws
 * however many trees it has. Its wood, in its kind's bark, hides what is behind it and casts its shadow; its clumps
 * are seen through, so they are drawn apart, after all else, the coarsest stage first and within a stage the
 * furthest tree first (two trees at different stages overlapping are drawn in the order of their stages, which is
 * rough where they cross). One look serves every tree of a kind, and one look each level of hiding, the clumps'
 * colour told with each draw.
 *
 * <p>A tree further than {@value #KEPT} metres from the eye and wholly outside the widest view a picture could have
 * is not drawn, nor gathered for rays: so a tree well behind the eye casts no shadow into the view.
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
    /** Within so many metres of the eye every tree is drawn, in view or not. */
    private static final int KEPT = 60;
    /** The widest a picture is taken to be, for its height. */
    private static final double WIDEST = 2.4;
    private static final String COPIED = "copied";

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
    private final List<Map<Stage, Copies>> copies = new ArrayList<>();
    private final Staging staging = new Staging(FIELD, PIXELS);
    private final Optional<Stage> held;
    private final Stage[] at;
    /** Where the wood's middle is: every tree is a copy placed from there, so all are drawn as steadily. */
    private final Mat4 middle;
    private final List<Filling> fromTheMiddle = new ArrayList<>();

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
        this.middle = new Mat4(planted.stream().map(one -> new Vec3(one.placed().at(0, 3), one.placed().at(1, 3),
                one.placed().at(2, 3))).reduce(new Vec3(0, 0, 0), Vec3::plus).times(1.0 / Math.max(1, planted.size())));
        final Mat4 back = new Mat4(new Vec3(-middle.at(0, 3), -middle.at(1, 3), -middle.at(2, 3)));
        planted.forEach(one -> fromTheMiddle.add(new Filling(laid.copy()).with(COPIED,
                back.times(one.placed()).columns())));
        try {
            for (final Grown tree : grown) {
                final Surface bark = new Shades().of(new Barks().of(tree.kind()));
                if (!barks.containsKey(tree.kind())) {
                    barks.put(tree.kind(), new Looked(card, new Look(laid, bark)));
                }
                trees.add(new StagedTree(card, laid, tree.tree(), bark, levels));
                final Map<Stage, Copies> each = new EnumMap<>(Stage.class);
                copies.add(each);
                for (final Stage stage : Stage.values()) {
                    each.put(stage, card.copies(laid, 16));
                }
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
        sorted(camera);
        for (int grown = 0; grown < trees.size(); grown++) {
            for (final Stage stage : Stage.values()) {
                barks.get(kinds.get(grown)).drawn(pass, trees.get(grown).wood(stage), middle, camera, 1,
                        new Vec3(1, 1, 1), copies.get(grown).get(stage));
            }
        }
    }

    /** Each tree's stage chosen, and the copies of each grown tree at each stage filled, the furthest first. */
    private void sorted(final Camera camera) {
        final List<List<List<Filling>>> into = new ArrayList<>();
        for (int grown = 0; grown < trees.size(); grown++) {
            into.add(IntStream.range(0, Stage.values().length)
                    .mapToObj(stage -> (List<Filling>) new ArrayList<Filling>()).toList());
        }
        final double[] far = new double[planted.size()];
        IntStream.range(0, planted.size()).forEach(tree -> far[tree] = distance(planted.get(tree), camera));
        final List<Integer> order = IntStream.range(0, planted.size()).boxed()
                .sorted(Comparator.comparingDouble((Integer tree) -> far[tree]).reversed()).toList();
        for (final int tree : order) {
            final Planted one = planted.get(tree);
            at[tree] = held.isPresent() ? held.get() : staging.of(tree, far[tree], one.times());
            if (seen(one, far[tree], camera)) {
                into.get(one.grown()).get(at[tree].ordinal()).add(fromTheMiddle.get(tree));
            }
        }
        for (int grown = 0; grown < trees.size(); grown++) {
            for (final Stage stage : Stage.values()) {
                copies.get(grown).get(stage).filled(into.get(grown).get(stage.ordinal()));
            }
        }
    }

    /** Whether {@code tree}, {@code far} from the eye, is near it or may be in the widest view it could have. */
    private boolean seen(final Planted tree, final double far, final Camera camera) {
        if (far < KEPT * 100) {
            return true;
        }
        final double half = Math.atan(Math.tan(Math.toRadians(camera.fieldOfView()) / 2)
                * Math.sqrt(1 + WIDEST * WIDEST));
        final double reach = trees.get(tree.grown()).height() * tree.times();
        final Vec3 towards = middleOf(tree).minus(camera.eye());
        final double off = Math.acos(Math.clamp(towards.unit().dot(camera.ahead().unit()), -1, 1));
        return off < half + Math.asin(Math.min(1, reach / far));
    }

    /** Every tree's clumps, drawn by {@code pass} as {@code camera} sees them: as {@link #drawn} chose and sorted. */
    public void seenThrough(final Pass pass, final Camera camera) {
        for (final Stage stage : List.of(Stage.values()).reversed()) {
            for (int grown = 0; grown < trees.size(); grown++) {
                final StagedTree staged = trees.get(grown);
                final Copies these = copies.get(grown).get(stage);
                staged.hazes(stage).forEach((hides, mesh) ->
                        hazes.get(hides).drawn(pass, mesh, middle, camera, 1, staged.hue(stage), these));
            }
        }
    }

    /** The stage each tree was last drawn at, in the order planted; a tree not yet drawn, as grown. */
    public List<Stage> stages() {
        return IntStream.range(0, at.length).mapToObj(tree -> at[tree] == null ? Stage.FINE : at[tree]).toList();
    }

    /** How far the middle of {@code tree}'s height is from {@code camera}'s eye. */
    private double distance(final Planted tree, final Camera camera) {
        return middleOf(tree).minus(camera.eye()).length();
    }

    private Vec3 middleOf(final Planted tree) {
        final Mat4 placed = tree.placed();
        final double half = trees.get(tree.grown()).height() * tree.times() / 2;
        return new Vec3(placed.at(0, 3), placed.at(1, 3) + half, placed.at(2, 3));
    }

    @Override
    public void close() {
        hazes.values().forEach(Looked::close);
        hazes.clear();
        copies.reversed().forEach(each -> each.values().forEach(Copies::close));
        copies.clear();
        trees.reversed().forEach(StagedTree::close);
        trees.clear();
        barks.values().forEach(Looked::close);
        barks.clear();
    }
}
