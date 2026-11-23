package melons.innocent.terrain.bench;

import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileData;
import melons.innocent.terrain.tiles.TileKey;
import melons.innocent.terrain.tiles.Tiling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * The tiles wanted, built away from the card's thread, on threads of their own: each thread taking, whenever it is
 * free, the first tile in the latest order wanted that is not yet built nor being built, so what is wanted soonest is
 * built first however often the order changes. A tile built in two steps: its data, sampled from the ground its level
 * is seen as, each point sorted into the painting's topmost layer that lies there; then its meshes, ready to be put on
 * the card, for the edges it is wanted with. Both kept: the land does not change, so a tile no longer wanted is kept
 * against its being wanted again, till what is kept takes more than {@code budget} bytes, and then those wanted
 * longest ago are dropped first. What is kept is told level by level. The card's thread takes what is built when it
 * will. Closed, its threads stop, a tile being built left unfinished.
 */
public final class TileBuilder implements AutoCloseable {

    private final Tiling tiling;
    private final IntFunction<Ground> grounds;
    private final Painting painting;
    private final double up;
    private final long budget;
    /** How many layers the painting has, its first among them. */
    private final int layers;
    private final Map<TileKey, TileData> data = new ConcurrentHashMap<>();
    private final Map<Chosen, TileMeshes> meshes = new ConcurrentHashMap<>();
    /** When each tile was last wanted, by the count of wants. */
    private final Map<TileKey, Long> stamps = new ConcurrentHashMap<>();
    private final AtomicLong wants = new AtomicLong();
    private final Set<Chosen> building = ConcurrentHashMap.newKeySet();
    private final Set<TileKey> sampling = ConcurrentHashMap.newKeySet();
    private final List<Thread> threads = new ArrayList<>();
    private final Object waiting = new Object();
    private volatile List<Chosen> order = List.of();
    private volatile Set<TileKey> wanted = Set.of();
    private volatile boolean open = true;

    /**
     * Tiles of {@code tiling}, each of the ground {@code grounds} says its level is seen as, painted as
     * {@code painting} says, drawn {@code up} times as high, on {@code threads} threads, kept within {@code budget}
     * bytes.
     */
    public TileBuilder(final Tiling tiling, final IntFunction<Ground> grounds, final Painting painting,
                       final double up, final int threads, final long budget) {
        this.tiling = tiling;
        this.grounds = grounds;
        this.painting = painting;
        this.up = up;
        this.budget = budget;
        this.layers = painting.legend().size();
        for (int k = 0; k < threads; k++) {
            this.threads.add(Thread.ofPlatform().daemon().name("tiles").start(this::work));
        }
    }

    /** {@code tiles} wanted, in their order, the soonest first; what is kept kept within its budget. */
    public void want(final List<Chosen> tiles) {
        final long now = wants.incrementAndGet();
        tiles.forEach(tile -> stamps.put(tile.key(), now));
        wanted = tiles.stream().map(Chosen::key).collect(Collectors.toUnmodifiableSet());
        order = List.copyOf(tiles);
        dropped();
        synchronized (waiting) {
            waiting.notifyAll();
        }
    }

    /** Of the tiles kept and no longer wanted, those wanted longest ago dropped, till what is kept is in budget. */
    private void dropped() {
        long held = 0;
        for (final TileData tile : data.values()) {
            held += held(tile);
        }
        for (final TileMeshes made : meshes.values()) {
            held += made.held();
        }
        if (held <= budget) {
            return;
        }
        final List<TileKey> spare = new ArrayList<>(data.keySet());
        spare.removeAll(wanted);
        spare.sort(Comparator.comparingLong(key -> stamps.getOrDefault(key, 0L)));
        for (final TileKey key : spare) {
            if (held <= budget) {
                break;
            }
            final TileData tile = data.remove(key);
            held -= tile == null ? 0 : held(tile);
            for (final Chosen made : List.copyOf(meshes.keySet())) {
                if (made.key().equals(key)) {
                    held -= meshes.remove(made).held();
                }
            }
            stamps.remove(key);
        }
    }

    /** About how much room {@code tile}'s data takes, in bytes. */
    private long held(final TileData tile) {
        return 36L * tile.points() * tile.points() + 60L * tile.triangles().count() + 40L * tile.triangles().added();
    }

    /** {@code tile}'s meshes, if they are built. */
    public Optional<TileMeshes> meshes(final Chosen tile) {
        return Optional.ofNullable(meshes.get(tile));
    }

    /** How many of the tiles wanted have their meshes built. */
    public int done() {
        return (int) order.stream().filter(meshes::containsKey).count();
    }

    /** What is kept, level by level: how many tiles' data and how much room, how many meshes and how much room. */
    public Map<Integer, Stock> stock() {
        final Map<Integer, Stock> stock = new TreeMap<>();
        data.forEach((key, tile) -> stock.merge(key.level(), new Stock(1, held(tile), 0, 0, 0), Stock::plus));
        meshes.forEach((tile, made) -> stock.merge(tile.key().level(), new Stock(0, 0, 1, made.held(), 0),
                Stock::plus));
        building.forEach(tile -> stock.merge(tile.key().level(), new Stock(0, 0, 0, 0, 1), Stock::plus));
        return stock;
    }

    /**
     * What a builder keeps of a level.
     *
     * @param data      how many tiles' data
     * @param dataBytes about how much room it takes
     * @param meshes    how many tiles' meshes
     * @param meshBytes about how much room they take
     * @param building  how many are being built
     */
    public record Stock(int data, long dataBytes, int meshes, long meshBytes, int building) {

        /** This and {@code other} together. */
        Stock plus(final Stock other) {
            return new Stock(data + other.data, dataBytes + other.dataBytes, meshes + other.meshes,
                    meshBytes + other.meshBytes, building + other.building);
        }
    }

    @Override
    public void close() {
        open = false;
        synchronized (waiting) {
            waiting.notifyAll();
        }
        threads.forEach(Thread::interrupt);
    }

    /** A thread's work: the first tile wanted not yet built nor being built, built; or a wait for more. */
    private void work() {
        while (open) {
            final Chosen tile = next();
            if (tile == null) {
                synchronized (waiting) {
                    try {
                        waiting.wait(100);
                    } catch (final InterruptedException stopped) {
                        return;
                    }
                }
            } else {
                try {
                    final TileData sampled = data.computeIfAbsent(tile.key(), key -> new TileData(tiling, key,
                            grounds.apply(key.level()), this::sort, painting::water));
                    meshes.put(tile, new TileMeshes(sampled, tile, up, painting.waterLayer() >= 0));
                } finally {
                    sampling.remove(tile.key());
                    building.remove(tile);
                }
            }
        }
    }

    /** The first tile in the order wanted not yet built nor being built, taken to be built; none if none. */
    private Chosen next() {
        for (final Chosen tile : order) {
            if (meshes.containsKey(tile) || !building.add(tile)) {
                continue;
            }
            if (data.containsKey(tile.key()) || sampling.add(tile.key())) {
                return tile;
            }
            building.remove(tile);
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
