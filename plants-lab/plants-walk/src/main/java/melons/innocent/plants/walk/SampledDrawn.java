package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Copies;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.layout.Filling;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A forest without end on the card, drawn from its set of samples alone. Each sample is baked once at each level it
 * is wanted at, one mesh a kind of tree, and every square of the forest that is that sample at that level is one
 * copy of it: so the whole forest in sight is a draw for each sample, level and kind, and what the card holds goes
 * by how many samples there are and not by how far the forest reaches. A larger stretch of forest is nothing but its
 * samples stood side by side.
 *
 * <p>Every sample's trunks are on the card from the start, so no square is ever missing: a square whose level has
 * not come yet is drawn at the finest coarser one that has. Told to, it bakes every level in use for every sample
 * before it is first drawn, and then nothing is baked or sent while the eye moves; otherwise finer levels are baked
 * away from the card's thread as squares come to want them and sent a few thousandths of a second a frame, and a
 * level no square has used for a while goes when the card is over what it may hold.
 *
 * <p>For rays, every square within the shadows' reach stands as its sample's boughs, whatever level it is drawn
 * at: twigs cast no shadow, and so the finest meshes, far the heaviest, keep no triangles for rays at all.
 */
public final class SampledDrawn implements AutoCloseable {

    private static final int AT_ONCE = 4;
    private static final int BAKERS = 3;
    private static final long SENDING = 4_000_000;
    /** Half a tall tree's height, in centimetres: a square's middle is taken to be so far up. */
    private static final double HALF = 750;
    /** Within so far of the eye a square is drawn whether in view or not, in centimetres. */
    private static final double CLOSE = 6_000;
    /** A level no square has used for so many frames may go when the card is over what it may hold. */
    private static final int UNUSED = 240;
    private static final String COPIED = "copied";

    /** A sample at a level. */
    private record Key(int sample, Stage level) {
    }

    /** A sample's meshes at a level, come back from being baked, or why they did not. */
    private record Made(Key key, Map<Kind, Mesh> meshes, Optional<RuntimeException> failed) {
    }

    /** A sample at a level on the card: its meshes, and this frame's copies of it, drawn and standing for rays. */
    private final class Kept {
        private final Map<Kind, Shaped> shapes = new EnumMap<>(Kind.class);
        private final Copies shown = card.copies(laid, 16);
        private final Copies cast = card.copies(laid, 16);
        private final List<Filling> showing = new ArrayList<>();
        private final List<Filling> casting = new ArrayList<>();
        private long triangles;
        private long used;

        private void close() {
            shapes.values().forEach(Shaped::close);
            shown.close();
            cast.close();
        }
    }

    private final Card card;
    private final Laid laid;
    private final Endless endless;
    private final Reaches reaches;
    private final double shadows;
    /** Whether every level in use was baked before it was first drawn: then nothing is ever let go. */
    private final boolean beforehand;
    private final Budgeted budget = new Budgeted();
    private final Baked baked;
    private final Map<Kind, Looked> barks = new EnumMap<>(Kind.class);
    private final Map<Key, Kept> kept = new LinkedHashMap<>();
    private final Set<Key> asked = new HashSet<>();
    private final Queue<Made> made = new ConcurrentLinkedQueue<>();
    private final ExecutorService workers = Executors.newFixedThreadPool(BAKERS, work -> {
        final Thread thread = new Thread(work, "a forest sample's baker");
        thread.setDaemon(true);
        return thread;
    });
    private final long[] squares = new long[Stage.values().length];
    private Vec3 anchor = new Vec3(0, 0, 0);
    private long frame;
    private long drawn;
    private int flying;
    private int wanting;

    /**
     * {@code endless} on {@code card}, of the grown trees {@code grown}, each level reaching as {@code reaches}
     * says, its squares standing for rays within {@code shadows} centimetres of the eye; {@code beforehand}, with
     * every level in use baked for every sample before it is first drawn.
     */
    public SampledDrawn(final Card card, final Laid laid, final Endless endless, final List<WoodDrawn.Grown> grown,
                        final Reaches reaches, final double shadows, final boolean beforehand) {
        this.card = card;
        this.laid = laid;
        this.endless = endless;
        this.reaches = reaches;
        this.shadows = shadows;
        this.beforehand = beforehand;
        this.baked = new Baked(grown, List.of(1.0));
        try {
            for (final Kind kind : Kind.values()) {
                barks.put(kind, new Looked(card, new Look(laid, new Shades().of(new Barks().of(kind)))));
            }
            final Set<Stage> first = new HashSet<>(beforehand ? reaches.levels() : Set.of());
            first.add(Stage.WHOLE);
            if (shadows > 0) {
                first.add(Stage.BOUGHS);
            }
            final List<Key> keys = new ArrayList<>();
            for (int sample = 0; sample < endless.samples().count(); sample++) {
                for (final Stage level : first) {
                    keys.add(new Key(sample, level));
                }
            }
            // baked a few at once, on every thread there is to bake on, and each put on the card here as it comes:
            // no more at once, since a sample's finest mesh is large while it is still being put together
            for (int from = 0; from < keys.size(); from += BAKERS) {
                final List<Key> few = keys.subList(from, Math.min(keys.size(), from + BAKERS));
                final List<Awaited<Map<Kind, Mesh>>> coming = few.stream().map(key -> new Awaited<>(workers.submit(
                        () -> baked.wood(endless.samples().planted(key.sample()), key.level())))).toList();
                for (int each = 0; each < few.size(); each++) {
                    carded(few.get(each), coming.get(each).come());
                }
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /**
     * {@code meshes} put on the card as {@code key}'s: a sample's boughs kept for rays as well, where shadows are
     * cast at all, and no other level.
     */
    private void carded(final Key key, final Map<Kind, Mesh> meshes) {
        final Kept one = new Kept();
        kept.put(key, one);
        meshes.forEach((kind, mesh) -> {
            one.shapes.put(kind, new Shaped(card, laid, mesh, shadows > 0 && key.level() == Stage.BOUGHS));
            one.triangles += mesh.triangles();
        });
        one.used = frame;
    }

    /**
     * Readied for a frame seen by {@code camera}, outside any drawing: what has been baked sent, every square in
     * reach given to the sample and level it is drawn as and, within the shadows' reach, to the boughs it stands
     * for rays as; what is wanted and missing asked for; and what is no longer used let go if the card is over
     * what it may hold.
     */
    public void readied(final Camera camera) {
        readied(camera, endless.seen(camera.eye(), reaches));
    }

    /** The same, of the squares {@code inReach} and no others: for a forest that is not everywhere. */
    public void readied(final Camera camera, final List<Endless.Seen> inReach) {
        frame++;
        sent();
        final Vec3 eye = camera.eye();
        final double side = endless.samples().side();
        anchor = new Vec3(Math.floor(eye.x() / side) * side, 0, Math.floor(eye.z() / side) * side);
        kept.values().forEach(one -> one.showing.clear());
        kept.values().forEach(one -> one.casting.clear());
        Arrays.fill(squares, 0);
        drawn = 0;
        final InView view = new InView(camera, 0);
        final List<Endless.Seen> missing = new ArrayList<>();
        for (final Endless.Seen seen : inReach) {
            final Vec3 middle = endless.middle(seen.across(), seen.along());
            final Filling copy = new Filling(laid.copy()).with(COPIED, new Mat4(middle.minus(anchor))
                    .times(endless.turned(seen.quarters())).columns());
            if (seen.away() < shadows) {
                kept.get(new Key(seen.sample(), Stage.BOUGHS)).casting.add(copy);
            }
            if (seen.away() > CLOSE && !view.holds(middle.plus(new Vec3(0, HALF, 0)), Math.hypot(side, HALF))) {
                continue;
            }
            if (!kept.containsKey(new Key(seen.sample(), seen.level()))) {
                missing.add(seen);
            }
            Stage level = seen.level();
            while (!kept.containsKey(new Key(seen.sample(), level))) {
                level = level.coarser();
            }
            final Kept one = kept.get(new Key(seen.sample(), level));
            one.used = frame;
            one.showing.add(copy);
            squares[level.ordinal()]++;
            drawn += one.triangles;
        }
        kept.values().forEach(one -> one.shown.filled(one.showing));
        kept.values().forEach(one -> one.cast.filled(one.casting));
        wanting = missing.size();
        baking(missing);
        eased();
    }

    /** What has been baked put on the card, for a few thousandths of a second at most, while there is room. */
    private void sent() {
        final long began = System.nanoTime();
        Made done = made.poll();
        while (done != null) {
            flying--;
            asked.remove(done.key());
            if (done.failed().isPresent()) {
                throw done.failed().get();
            }
            if (!kept.containsKey(done.key()) && !budget.full(held())) {
                carded(done.key(), done.meshes());
            }
            done = System.nanoTime() - began < SENDING ? made.poll() : null;
        }
    }

    /** The samples and levels {@code missing} squares want, asked for, a few at once. */
    private void baking(final List<Endless.Seen> missing) {
        if (flying >= AT_ONCE || budget.full(held())) {
            return;
        }
        // the coarsest missing first, which is the smallest and serves the most squares; then the nearest
        missing.sort(Comparator.comparing((Endless.Seen seen) -> seen.level()).reversed()
                .thenComparingDouble(Endless.Seen::away));
        for (final Endless.Seen seen : missing) {
            final Key key = new Key(seen.sample(), seen.level());
            if (flying < AT_ONCE && asked.add(key)) {
                flying++;
                workers.submit(() -> {
                    try {
                        made.add(new Made(key, baked.wood(endless.samples().planted(key.sample()), key.level()),
                                Optional.empty()));
                    } catch (final RuntimeException refused) {
                        made.add(new Made(key, Map.of(), Optional.of(refused)));
                    }
                });
            }
        }
    }

    /**
     * Over what the card may hold, each level no square has used for a while let go: but trunks, and boughs, and
     * nothing at all of what was baked beforehand, which is there so that nothing need be baked again.
     */
    private void eased() {
        if (beforehand || held() <= budget.budget()) {
            return;
        }
        final List<Key> idle = kept.entrySet().stream().filter(one -> one.getKey().level() != Stage.WHOLE
                && one.getKey().level() != Stage.BOUGHS && one.getValue().used < frame - UNUSED)
                .map(Map.Entry::getKey).toList();
        idle.forEach(key -> kept.remove(key).close());
    }

    /** How much the run holds for the card, in megabytes. */
    private double held() {
        return card.memory().carded() + card.memory().reached();
    }

    /**
     * Every square as it was readied, drawn by {@code pass} as {@code camera} sees it: a draw for each sample, level
     * and kind. A pass that only gathers what stands for rays is given the squares within the shadows' reach
     * instead, each as its sample's boughs.
     */
    public void drawn(final Pass pass, final Camera camera) {
        final Mat4 stands = new Mat4(anchor);
        kept.values().forEach(one -> {
            final Copies these = pass.gathers() ? one.cast : one.shown;
            if (these.count() > 0) {
                one.shapes.forEach((kind, shape) -> barks.get(kind).drawn(pass, shape, stands, camera, 1,
                        new Vec3(1, 1, 1), these));
            }
        });
    }

    /** Whether everything wanted is on the card, or as much of it as the card may hold. */
    public boolean settled() {
        return flying == 0 && made.isEmpty() && (wanting == 0 || budget.full(held()));
    }

    /** What it holds and draws, in a line of words. */
    public String said() {
        return String.format(Locale.ENGLISH, "Endless forest: %d samples of %.0f m; squares drawn: %d finest, %d "
                + "twigs gone, %d boughs, %d limbs, %d trunks; %,d triangles a frame; %d levels on the card, %d "
                + "being baked, %d squares coarser than wanted; budget %,.0f MB", endless.samples().count(),
                endless.samples().side() / 100, squares[0], squares[1], squares[2], squares[3], squares[4], drawn,
                kept.size(), flying, wanting, budget.budget());
    }

    /** How many triangles the squares readied last come to. */
    public long triangles() {
        return drawn;
    }

    @Override
    public void close() {
        workers.shutdownNow();
        kept.values().forEach(Kept::close);
        kept.clear();
        barks.values().forEach(Looked::close);
        barks.clear();
    }
}
