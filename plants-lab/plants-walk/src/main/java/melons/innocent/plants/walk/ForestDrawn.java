package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * A large forest on the card ({@link Forested}), drawn by a tree of areas ({@link Tiered}). The cells near the eye
 * are trees, each a copy at the stage its distance calls for, none coarser than boughs ({@link WoodDrawn}). Every
 * area beyond is one mesh baked ahead from its trees ({@link Baked}), each kind of tree a draw: a cell at boughs, a
 * tile of cells at limbs, a wider tile as trunks alone, so the far forest is a few large meshes and not thousands
 * of small ones. A mesh stands in the way of no ray, so casts no shadow by rays, and an area wholly outside the
 * widest view is passed over.
 *
 * <p>Baking is done on threads away from the card, the nearest cell first, a few at a time; what is made is sent to
 * the card between frames, for a few thousandths of a second a frame. Nothing is ever missing meanwhile: a cell no
 * mesh yet covers is drawn as its trees' trunks, an area whose mesh is no longer wanted keeps it until whatever
 * takes its place has come, and a cell that was trees stays trees until then. Which cells are trees is settled
 * afresh only every {@value #EVERY} frames; between, a cell may for a moment be drawn both ways, the one over the
 * other, which shows
 * nothing, since where they meet they are the same wood.
 */
public final class ForestDrawn implements AutoCloseable {

    private static final int EVERY = 15;
    /** How many cells are being baked at once, at most; and how long sending may take a frame, in nanoseconds. */
    private static final int AT_ONCE = 6;
    private static final long SENDING = 4_000_000;
    /** How far a cell reaches from its middle, at most: half its height, and across its corner. */
    private static final double HALF = 750;
    /** How a cell is among the trees: not at all; as trees, each at its own stage; or as trunks, standing in. */
    private static final int OUT = 0;
    private static final int TREES = 1;
    private static final int STANDING_IN = 2;

    /** A cell's mesh as made: which cell, at which level, a mesh a kind of tree; or why it could not be made. */
    private record Made(int cell, Stage level, Map<Kind, Mesh> meshes, Optional<RuntimeException> failed) {
    }

    private final Card card;
    private final Laid laid;
    private final Forested forest;
    private final Tiered tiered;
    private final Budgeted budget = new Budgeted();
    private final Baked baked;
    private final WoodDrawn wood;
    private final Map<Kind, Looked> barks = new EnumMap<>(Kind.class);
    private final List<List<WoodDrawn.Planted>> planted = new ArrayList<>();
    private final List<Map<Kind, Shaped>> shapes = new ArrayList<>();
    private final Stage[] want;
    private final Stage[] have;
    private final Stage[] asked;
    private final int[] among;
    private final long[] triangles;
    private final Queue<Made> made = new ConcurrentLinkedQueue<>();
    private final AtomicReference<RuntimeException> failed = new AtomicReference<>();
    private final ExecutorService workers = Executors.newFixedThreadPool(3, work -> {
        final Thread thread = new Thread(work, "a forest cell's baker");
        thread.setDaemon(true);
        return thread;
    });
    private int flying;
    private int since = EVERY;

    /** {@code forest} of {@code grown} trees, on {@code card}, its layouts as {@code laid} says. */
    public ForestDrawn(final Card card, final Laid laid, final Forested forest, final List<WoodDrawn.Grown> grown) {
        this.card = card;
        this.laid = laid;
        this.forest = forest;
        this.tiered = new Tiered(forest);
        this.baked = new Baked(grown, List.of(1.0));
        final int areas = tiered.areas();
        this.want = new Stage[areas];
        this.have = new Stage[areas];
        this.asked = new Stage[areas];
        this.among = new int[forest.cells()];
        this.triangles = new long[areas];
        for (int area = 0; area < areas; area++) {
            shapes.add(new EnumMap<>(Kind.class));
        }
        for (int cell = 0; cell < forest.cells(); cell++) {
            planted.add(forest.planted(cell));
        }
        this.wood = new WoodDrawn(card, laid, grown, List.of(), Optional.empty());
        wood.capped(Stage.BOUGHS);
        try {
            for (final Kind kind : Kind.values()) {
                barks.put(kind, new Looked(card, new Look(laid, new Shades().of(new Barks().of(kind)))));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** Every tree of the forest, where it stands: for what is in the way. */
    public List<WoodDrawn.Planted> trees() {
        return planted.stream().flatMap(List::stream).toList();
    }

    /**
     * Made ready for a frame seen by {@code camera}, outside any drawing: each cell's ring found, what has been
     * baked sent to the card, more set baking, and, every so many frames, which cells are trees settled afresh.
     */
    public void readied(final Camera camera) {
        if (failed.get() != null) {
            throw failed.get();
        }
        final Vec3 eye = camera.eye();
        // over what the card may hold, every ring draws in by the same share; well under, they come back out
        tiered.drawnIn(since + 1 >= EVERY ? budget.eased(held()) : budget.quality());
        tiered.chosen(eye);
        for (int area = 0; area < want.length; area++) {
            want[area] = tiered.want(area);
        }
        sent();
        // a mesh no longer wanted goes once whatever takes its place has come for every cell under it; or at once,
        // the card being full, when its trees' trunks stand in until there is room for what is wanted
        final boolean full = budget.full(held());
        for (int area = 0; area < want.length; area++) {
            if (have[area] != null && want[area] == null
                    && (full || tiered.cells(area).stream().allMatch(this::served))) {
                let(area);
            }
        }
        since = full ? EVERY : since;
        baking(eye);
        if (++since >= EVERY && changed()) {
            replanted();
            since = 0;
        }
    }

    /** What has been baked sent to the card, for as long as a frame can spare; what is no longer wanted, dropped. */
    private void sent() {
        final long began = System.nanoTime();
        Made done = made.poll();
        while (done != null) {
            flying--;
            final int area = done.cell();
            asked[area] = asked[area] == done.level() ? null : asked[area];
            if (done.failed().isPresent()) {
                failed.set(done.failed().get());
                return;
            }
            if (done.level() == want[area] && !budget.full(held())) {
                let(area);
                done.meshes().forEach((kind, mesh) -> {
                    shapes.get(area).put(kind, new Shaped(card, laid, mesh, false));
                    triangles[area] += mesh.triangles();
                });
                have[area] = done.level();
            }
            done = System.nanoTime() - began < SENDING ? made.poll() : null;
        }
    }

    /** How many megabytes are held on the card, of every kind, by all the run has put there. */
    private double held() {
        return card.memory().carded() + card.memory().reached();
    }

    /** The nearest areas whose mesh is not what is wanted set baking, a few at a time. */
    private void baking(final Vec3 eye) {
        if (flying >= AT_ONCE) {
            return;
        }
        final List<Integer> wanting = new ArrayList<>();
        for (int area = 0; area < want.length; area++) {
            if (want[area] != null && have[area] != want[area] && asked[area] != want[area]) {
                wanting.add(area);
            }
        }
        wanting.sort(Comparator.comparingDouble(area -> tiered.middle(area).minus(eye).length()));
        for (final int area : wanting.subList(0, Math.min(wanting.size(), AT_ONCE - flying))) {
            final Stage level = want[area];
            final Vec3 middle = tiered.middle(area);
            final Mat4 back = new Mat4(new Vec3(-middle.x(), -middle.y(), -middle.z()));
            final List<WoodDrawn.Planted> from = new ArrayList<>();
            tiered.cells(area).forEach(cell -> planted.get(cell).forEach(tree -> from.add(
                    new WoodDrawn.Planted(tree.grown(), back.times(tree.placed()), tree.times()))));
            asked[area] = level;
            flying++;
            workers.submit(() -> {
                try {
                    made.add(new Made(area, level, baked.wood(from, level), Optional.empty()));
                } catch (final RuntimeException refused) {
                    made.add(new Made(area, level, Map.of(), Optional.of(refused)));
                }
            });
        }
    }

    /** Whether cell number {@code cell} is drawn as it is wanted: as trees, or by its owner's mesh at its level. */
    private boolean served(final int cell) {
        final int owner = tiered.owner(cell);
        return tiered.trees(cell) ? among[cell] == TREES : have[owner] != null && have[owner] == want[owner];
    }

    /**
     * How cell number {@code cell} is to be among the trees: as trees, where it is wanted so, or was and nothing has
     * yet come to take its place; not at all, where a mesh covers it; and as trunks, standing in, where none does.
     */
    private int belongs(final int cell) {
        if (tiered.trees(cell)) {
            return TREES;
        }
        if (served(cell)) {
            return OUT;
        }
        if (among[cell] == TREES) {
            return TREES;
        }
        return tiered.over(cell).stream().anyMatch(area -> have[area] != null) ? OUT : STANDING_IN;
    }

    private boolean changed() {
        return IntStream.range(0, among.length).anyMatch(cell -> belongs(cell) != among[cell]);
    }

    /** Which cells are trees, and which stand in as trunks, settled afresh. */
    private void replanted() {
        final List<WoodDrawn.Planted> trees = new ArrayList<>();
        for (int cell = 0; cell < among.length; cell++) {
            among[cell] = belongs(cell);
            if (among[cell] == TREES) {
                trees.addAll(planted.get(cell));
            } else if (among[cell] == STANDING_IN) {
                planted.get(cell).forEach(tree -> trees.add(new WoodDrawn.Planted(tree.grown(), tree.placed(),
                        tree.times(), Optional.of(Stage.WHOLE))));
            }
        }
        wood.replanted(trees);
    }

    /** Area number {@code area}'s mesh gone from the card. */
    private void let(final int area) {
        shapes.get(area).values().forEach(Shaped::close);
        shapes.get(area).clear();
        triangles[area] = 0;
        have[area] = null;
    }

    /**
     * The forest's wood drawn by {@code pass} as {@code camera} sees it: its trees, and each area's mesh in view.
     * Where {@code pass} only gathers what stands for rays, the meshes are left out: they stand in the way of none.
     */
    public void drawn(final Pass pass, final Camera camera) {
        wood.drawn(pass, camera);
        if (pass.gathers()) {
            return;
        }
        final InView view = new InView(camera, 0);
        for (int area = 0; area < shapes.size(); area++) {
            if (shapes.get(area).isEmpty()) {
                continue;
            }
            final Vec3 middle = tiered.middle(area);
            if (view.holds(middle.plus(new Vec3(0, HALF, 0)), Math.hypot(tiered.side(area), HALF))) {
                shapes.get(area).forEach((kind, shape) ->
                        barks.get(kind).drawn(pass, shape, new Mat4(middle), camera, 1));
            }
        }
    }

    /** What of the forest is seen through, drawn by {@code pass}: its trees' clumps, where they are drawn at all. */
    public void seenThrough(final Pass pass, final Camera camera) {
        wood.seenThrough(pass, camera);
    }

    /** Whether everything wanted is on the card, nothing else is, and nothing is being baked. */
    public boolean settled() {
        return IntStream.range(0, want.length).allMatch(area -> want[area] == have[area])
                && flying == 0 && made.isEmpty() && !changed();
    }

    /** What is on the card, in a line: how many cells are trees, how many meshes there are, and of how much. */
    public String said() {
        return String.format(Locale.ENGLISH, "Forest: %d cells as trees, %d meshes of %,d triangles, %d being "
                + "baked; rings at %.0f%% for a budget of %,.0f MB",
                Arrays.stream(among).filter(how -> how == TREES).count(),
                shapes.stream().filter(each -> !each.isEmpty()).count(), Arrays.stream(triangles).sum(), flying,
                budget.quality() * 100, budget.budget());
    }

    @Override
    public void close() {
        workers.shutdownNow();
        IntStream.range(0, shapes.size()).forEach(this::let);
        barks.values().forEach(Looked::close);
        wood.close();
    }
}
