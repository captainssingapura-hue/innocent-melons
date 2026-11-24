package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Copies;
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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
 * <p>Far off, a tree is drawn as its coarsest stage, its trunk and its crown's haze, and not sorted (its trunk hides
 * what is behind it, and one colour of haze over another is the same whichever is laid first), so a far wood costs
 * nothing tree by tree but copying; nor is it gathered for rays. Where each tree stands, when it turns far and how
 * the far trees are found in view are its planting's ({@link Planting}). The wood may be planted afresh, the trees
 * as grown kept on the card; and a tree may be planted held at one stage of its own, as a bench holds one beside
 * another to compare them.
 *
 * <p><b>The haze is not drawn for now</b> (the owner's word, 24 Nov 2026: it came out lighter than the wood it stands
 * for, and the more so the more trees), so a coarse stage is its kept wood alone; {@code -Dbench.trees.haze=true}
 * draws it again.
 *
 * <p>The stage is chosen as for a picture 1080 pixels high in the game's view of 48 degrees: on a lower picture a tree
 * is drawn finer than it need be, never coarser. {@code -Dbench.trees.stage=fine} (or any stage) holds every tree
 * at one stage, to see it from wherever.
 */
public final class WoodDrawn implements AutoCloseable {

    /** How much the clumps hide, each drawn at the nearest of these: doubling, so few are drawn far darker. */
    private final List<Double> levels = List.of(0.01, 0.02, 0.04, 0.08, 0.16, 0.32, 0.64, 0.9);

    /** A tree as grown, of {@code kind}. */
    public record Grown(Kind kind, Tree tree) {
    }

    /**
     * A tree planted: which of the grown it is, by number, what takes it to where it stands, {@code times} big;
     * and the stage it is held at, if it is held at one whatever its distance.
     */
    public record Planted(int grown, Mat4 placed, double times, Optional<Stage> held) {

        /** A tree planted to be drawn at whatever stage its distance calls for. */
        public Planted(final int grown, final Mat4 placed, final double times) {
            this(grown, placed, times, Optional.empty());
        }
    }

    private final Laid laid;
    private final List<Kind> kinds;
    private final List<StagedTree> trees = new ArrayList<>();
    private final Map<Kind, Looked> barks = new EnumMap<>(Kind.class);
    private final Map<Double, Looked> hazes = new LinkedHashMap<>();
    private final List<Map<Stage, Copies>> copies = new ArrayList<>();
    /** The far trees, as copies, by which grown tree each is: drawn, not gathered for rays. */
    private final List<Copies> far = new ArrayList<>();
    private final Optional<Stage> held;
    private Planting planting;
    /** The coarsest stage a tree left to its distance is drawn at. */
    private Stage coarsest = Stage.WHOLE;
    /** The finest stage any tree is drawn at, however near: the finest there is, unless a cap says another. */
    private Stage finest = Stage.FINE;
    /** The camera the trees were last chosen for. */
    private Camera last;

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
        this.laid = laid;
        this.kinds = grown.stream().map(Grown::kind).toList();
        this.held = held;
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
                far.add(card.copies(laid, 16).standing(false));
            }
            for (final double hides : levels) {
                final Surface haze = new Surface(new Tint().colour(), List.of(), new Colour("#000000"), 1);
                hazes.put(hides, new Looked(card, new Look(laid, haze.clear(1 - hides).hazy())));
            }
            replanted(planted);
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /**
     * No tree left to its distance is drawn coarser than {@code stage} from now on, however far off: for a wood
     * whose far parts are drawn by something else, which its trees must match where they meet it.
     */
    public void capped(final Stage stage) {
        this.coarsest = stage;
        this.last = null;
    }

    /**
     * Each tree told the stage it is drawn at from now on, in the order planted, whatever its distance: for a
     * forest drawn in squares, whose trees drawn one by one must change as the squares beside them do.
     */
    public void told(final List<Stage> stages) {
        if (planting.told(stages)) {
            this.last = null;
        }
    }

    /**
     * No tree is drawn finer than {@code stage} from now on, however near and whatever it is held at: a cap, for
     * finding by eye which change of stage it is that is seen. The finest there is caps nothing.
     */
    public void noFinerThan(final Stage stage) {
        if (stage != finest) {
            this.finest = stage;
            this.last = null;
        }
    }

    /** The wood planted afresh as {@code planted}: the trees as grown, and what is on the card, kept. */
    public void replanted(final List<Planted> planted) {
        this.planting = new Planting(planted, trees.stream().map(StagedTree::height).toList(), laid);
        this.last = null;
    }

    /**
     * Every tree's wood, drawn by {@code pass} as {@code camera} sees it, each at the stage its distance calls for:
     * the same in every pass of a frame, so a tree casts the shadow of what is drawn of it.
     */
    public void drawn(final Pass pass, final Camera camera) {
        chosen(camera);
        final Mat4 middle = planting.middle();
        for (int grown = 0; grown < trees.size(); grown++) {
            for (final Stage stage : Stage.values()) {
                barks.get(kinds.get(grown)).drawn(pass, trees.get(grown).wood(stage), middle, camera, 1,
                        new Vec3(1, 1, 1), copies.get(grown).get(stage));
            }
            // the far trees' trunks, their coarsest wood: it hides what is behind it, so needs no sorting
            barks.get(kinds.get(grown)).drawn(pass, trees.get(grown).wood(Stage.WHOLE), middle, camera, 1,
                    new Vec3(1, 1, 1), far.get(grown));
        }
    }

    /** Each tree chosen for {@code camera} and its copies filled: once for a camera, however many passes ask. */
    private void chosen(final Camera camera) {
        if (camera.equals(last)) {
            return;
        }
        last = camera;
        final Planting.Chosen chosen = planting.chosen(camera, held, trees.size(), coarsest, finest);
        for (int grown = 0; grown < trees.size(); grown++) {
            far.get(grown).filled(chosen.far().get(grown));
            for (final Stage stage : Stage.values()) {
                copies.get(grown).get(stage).filled(chosen.near().get(grown).get(stage.ordinal()));
            }
        }
    }

    /**
     * The far trees' crowns, and then every other tree's clumps, drawn by {@code pass} as {@code camera} sees them:
     * as {@link #drawn} chose and sorted, the far ones first.
     */
    public void seenThrough(final Pass pass, final Camera camera) {
        if (!Boolean.getBoolean("bench.trees.haze")) {
            return;
        }
        final Mat4 middle = planting.middle();
        for (int grown = 0; grown < trees.size(); grown++) {
            final StagedTree staged = trees.get(grown);
            final Copies these = far.get(grown);
            staged.hazes(Stage.WHOLE).forEach((hides, mesh) ->
                    hazes.get(hides).drawn(pass, mesh, middle, camera, 1, staged.hue(Stage.WHOLE), these));
        }
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
        return planting.stages();
    }

    /** How tall grown tree number {@code grown} stands, as grown, in centimetres. */
    public double height(final int grown) {
        return trees.get(grown).height();
    }

    @Override
    public void close() {
        far.forEach(Copies::close);
        far.clear();
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
