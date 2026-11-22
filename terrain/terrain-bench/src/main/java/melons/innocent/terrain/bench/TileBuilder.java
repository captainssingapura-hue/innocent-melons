package melons.innocent.terrain.bench;

import melons.innocent.terrain.Ground;
import melons.innocent.terrain.Sample;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileData;
import melons.innocent.terrain.tiles.TileKey;
import melons.innocent.terrain.tiles.Tiling;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * The tiles wanted, built away from the card's thread, on threads of their own, in the order they are wanted, the
 * nearest first: each sampled from the ground, each point sorted into the painting's topmost layer that lies there.
 * Built, a tile is kept while it is wanted and dropped once it is not; the card's thread takes what is built when it
 * will. Closed, its threads stop, a tile being built left unfinished.
 */
public final class TileBuilder implements AutoCloseable {

    private final Tiling tiling;
    private final Ground ground;
    private final Painting painting;
    /** How many layers the painting has, its first among them. */
    private final int layers;
    private final ExecutorService workers;
    private final Map<TileKey, TileData> built = new ConcurrentHashMap<>();
    private final Set<TileKey> building = ConcurrentHashMap.newKeySet();
    private volatile Set<TileKey> wanted = Set.of();

    /** Tiles of {@code tiling} of {@code ground}, painted as {@code painting} says, on {@code threads} threads. */
    public TileBuilder(final Tiling tiling, final Ground ground, final Painting painting, final int threads) {
        this.tiling = tiling;
        this.ground = ground;
        this.painting = painting;
        this.layers = painting.legend().size();
        this.workers = Executors.newFixedThreadPool(threads, work -> Thread.ofPlatform().daemon().name("tiles")
                .unstarted(work));
    }

    /** {@code chosen} wanted, in its order: those not yet built set building; any no longer wanted dropped. */
    public void want(final List<Chosen> chosen) {
        wanted = chosen.stream().map(Chosen::key).collect(Collectors.toUnmodifiableSet());
        built.keySet().retainAll(wanted);
        for (final Chosen tile : chosen) {
            final TileKey key = tile.key();
            if (!built.containsKey(key) && building.add(key)) {
                workers.submit(() -> build(key));
            }
        }
    }

    /** {@code key}'s tile, if it is built. */
    public Optional<TileData> built(final TileKey key) {
        return Optional.ofNullable(built.get(key));
    }

    /** How many of the tiles wanted are built. */
    public int done() {
        return built.size();
    }

    private void build(final TileKey key) {
        try {
            if (wanted.contains(key)) {
                final TileData tile = new TileData(tiling, key, ground, this::sort, painting::water);
                if (wanted.contains(key)) {
                    built.put(key, tile);
                }
            }
        } finally {
            building.remove(key);
        }
    }

    @Override
    public void close() {
        workers.shutdownNow();
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
