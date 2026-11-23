package melons.innocent.terrain.bench;

import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;
import melons.innocent.terrain.tiles.TileData;
import melons.innocent.terrain.tiles.TileKey;
import melons.innocent.terrain.tiles.Tiling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntFunction;

/**
 * The tiles wanted, built away from the card's thread, on threads of their own: each thread taking, whenever it is
 * free, the first tile in the latest order wanted that is neither built nor being built, so what is wanted soonest is
 * built first however often the order changes. Each tile sampled from the ground its level is seen as, each point
 * sorted into the painting's topmost layer that lies there. Built, a tile is kept: the land does not change, so a tile
 * no longer wanted is kept against its being wanted again, up to {@link #KEPT} of them, those wanted longest ago
 * dropped first. The card's thread takes what is built when it will. Closed, its threads stop, a tile being built
 * left unfinished.
 */
public final class TileBuilder implements AutoCloseable {

    /** How many tiles no longer wanted are kept, at most, against their being wanted again. */
    static final int KEPT = 1_500;

    private final Tiling tiling;
    private final IntFunction<Ground> grounds;
    private final Painting painting;
    /** How many layers the painting has, its first among them. */
    private final int layers;
    private final Map<TileKey, TileData> built = new ConcurrentHashMap<>();
    /** When each tile was last wanted, by the count of wants. */
    private final Map<TileKey, Long> stamps = new ConcurrentHashMap<>();
    private final AtomicLong wants = new AtomicLong();
    private final Set<TileKey> building = ConcurrentHashMap.newKeySet();
    private final List<Thread> threads = new ArrayList<>();
    private final Object waiting = new Object();
    private volatile List<TileKey> order = List.of();
    private volatile Set<TileKey> wanted = Set.of();
    private volatile boolean open = true;

    /** Tiles of {@code tiling} of {@code ground}, painted as {@code painting} says, on {@code threads} threads. */
    public TileBuilder(final Tiling tiling, final Ground ground, final Painting painting, final int threads) {
        this(tiling, level -> ground, painting, threads);
    }

    /**
     * Tiles of {@code tiling}, each of the ground {@code grounds} says its level is seen as, painted as
     * {@code painting} says, on {@code threads} threads.
     */
    public TileBuilder(final Tiling tiling, final IntFunction<Ground> grounds, final Painting painting,
                       final int threads) {
        this.tiling = tiling;
        this.grounds = grounds;
        this.painting = painting;
        this.layers = painting.legend().size();
        for (int k = 0; k < threads; k++) {
            this.threads.add(Thread.ofPlatform().daemon().name("tiles").start(this::work));
        }
    }

    /**
     * {@code keys} wanted, in their order, the soonest first; of those built and no longer wanted, the oldest
     * dropped.
     */
    public void want(final List<TileKey> keys) {
        final long now = wants.incrementAndGet();
        keys.forEach(key -> stamps.put(key, now));
        wanted = Set.copyOf(keys);
        order = List.copyOf(keys);
        dropped();
        synchronized (waiting) {
            waiting.notifyAll();
        }
    }

    /** Of the tiles built and no longer wanted, those wanted longest ago dropped, till no more are left than kept. */
    private void dropped() {
        final List<TileKey> spare = new ArrayList<>(built.keySet());
        spare.removeAll(wanted);
        if (spare.size() > KEPT) {
            spare.sort(Comparator.comparingLong(key -> stamps.getOrDefault(key, 0L)));
            for (final TileKey key : spare.subList(0, spare.size() - KEPT)) {
                built.remove(key);
                stamps.remove(key);
            }
        }
    }

    /** {@code key}'s tile, if it is built. */
    public Optional<TileData> built(final TileKey key) {
        return Optional.ofNullable(built.get(key));
    }

    /** How many of the tiles wanted are built. */
    public int done() {
        return (int) wanted.stream().filter(built::containsKey).count();
    }

    /** How many tiles are built and kept, wanted or not. */
    public int kept() {
        return built.size();
    }

    @Override
    public void close() {
        open = false;
        synchronized (waiting) {
            waiting.notifyAll();
        }
        threads.forEach(Thread::interrupt);
    }

    /** A thread's work: the first tile wanted that is neither built nor being built, built; or a wait for more. */
    private void work() {
        while (open) {
            final TileKey key = next();
            if (key == null) {
                synchronized (waiting) {
                    try {
                        waiting.wait(100);
                    } catch (final InterruptedException stopped) {
                        return;
                    }
                }
            } else {
                try {
                    built.put(key, new TileData(tiling, key, grounds.apply(key.level()), this::sort,
                            painting::water));
                } finally {
                    building.remove(key);
                }
            }
        }
    }

    /** The first tile in the order wanted that is neither built nor being built, taken to be built; none if none. */
    private TileKey next() {
        for (final TileKey key : order) {
            if (!built.containsKey(key) && building.add(key)) {
                return key;
            }
        }
        return null;
    }

    /** The painting's topmost layer that lies at {@code at}: its first, if no other does. */
    private int sort(final Sample at) {
        for (int layer = layers - 1; layer > 0; layer--) {
            if (painting.covers(layer, at)) {
                return layer;
            }
        }
        return 0;
    }
}
