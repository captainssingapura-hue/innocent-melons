package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.terrain.Ground;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileKey;
import melons.innocent.terrain.tiles.Tiling;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The land on the card in tiles: the tiles shown, drawn as they are built, so many a frame, an old one kept until
 * all that replace it are built and then swapped for them at once, so no hole opens; and besides them the tiles
 * wanted ahead, built but not shown, against the eye's coming there. Each layer's look built once for every tile. On
 * the card's thread only. Closed, its tiles, its looks and its building threads are all gone.
 */
public final class TiledLand implements AutoCloseable {

    /** How many tiles are put on the card each frame, at most, so a frame is never held up long. */
    private static final int EACH_FRAME = 24;

    private final Card card;
    private final Laid laid;
    private final Painting painting;
    private final double up;
    private final TileBuilder builder;
    private final Looks looks;
    private final Map<Chosen, TileDrawn> drawn = new LinkedHashMap<>();
    private List<Chosen> shown = List.of();

    /** The land of {@code ground}, tiled as {@code tiling} says, painted as {@code painting} says. */
    public TiledLand(final Card card, final Laid laid, final Painting painting, final Tiling tiling,
                     final Ground ground, final double up) {
        this.card = card;
        this.laid = laid;
        this.painting = painting;
        this.up = up;
        this.builder = new TileBuilder(tiling, ground, painting,
                Math.max(1, Runtime.getRuntime().availableProcessors() - 2));
        this.looks = new Looks(card, laid, painting);
    }

    /** {@code shown} to be shown, nearest first, and {@code ahead} built besides, the soonest wanted first. */
    public void want(final List<Chosen> shown, final List<TileKey> ahead) {
        this.shown = List.copyOf(shown);
        final Set<TileKey> keys = new LinkedHashSet<>();
        shown.forEach(tile -> keys.add(tile.key()));
        keys.addAll(ahead);
        builder.want(new ArrayList<>(keys));
    }

    /**
     * The tiles on the card made the ones shown, without a hole: a tile no longer shown is kept until every tile shown
     * over its ground is built, and then they are put on together and it is closed; a tile shown over ground no old
     * tile is on is put on as soon as it is built. No more than so many a frame put on, but for those swapped in.
     */
    public void kept() {
        final Set<Chosen> wanted = Set.copyOf(shown);
        for (final Chosen old : List.copyOf(drawn.keySet())) {
            if (!wanted.contains(old)) {
                final List<Chosen> over = shown.stream().filter(tile -> overlaps(tile.key(), old.key())).toList();
                if (over.stream().allMatch(tile -> drawn.containsKey(tile) || builder.built(tile.key()).isPresent())) {
                    over.forEach(this::put);
                    drawn.remove(old).close();
                }
            }
        }
        int put = 0;
        for (final Chosen tile : shown) {
            if (put < EACH_FRAME && !drawn.containsKey(tile) && builder.built(tile.key()).isPresent()
                    && drawn.keySet().stream().noneMatch(on -> overlaps(on.key(), tile.key()))) {
                put(tile);
                put++;
            }
        }
    }

    /** {@code tile} put on the card, if it is not on it already. */
    private void put(final Chosen tile) {
        if (!drawn.containsKey(tile)) {
            drawn.put(tile, new TileDrawn(card, laid, builder.built(tile.key()).orElseThrow(), tile, up,
                    painting.waterLayer()));
        }
    }

    /** Whether {@code one}'s ground and {@code other}'s overlap: whether one of them lies within the other. */
    private boolean overlaps(final TileKey one, final TileKey other) {
        TileKey deeper = one.level() >= other.level() ? one : other;
        final TileKey upper = deeper == one ? other : one;
        while (deeper.level() > upper.level()) {
            deeper = deeper.parent();
        }
        return deeper.equals(upper);
    }

    /** Its tiles drawn in {@code pass}, as {@code flight} has the land, seen by {@code camera}. */
    public void drawn(final Pass pass, final Flight flight, final Camera camera) {
        drawn.values().forEach(tile -> tile.drawn(pass, looks, flight, camera));
    }

    /** How many tiles are to be shown. */
    public int shown() {
        return shown.size();
    }

    /** How many tiles are built and kept, shown or ahead. */
    public int built() {
        return builder.done();
    }

    /** How many tiles are on the card. */
    public int drawn() {
        return drawn.size();
    }

    /** How many tiles to be shown are not on the card, nor any old tile over their ground: holes in the land. */
    public int gaps() {
        return (int) shown.stream().filter(tile -> !drawn.containsKey(tile)
                && drawn.keySet().stream().noneMatch(on -> overlaps(on.key(), tile.key()))).count();
    }

    /** How many tiles to be shown are not on the card yet, an old tile standing in for each: detail not yet come. */
    public int stale() {
        return (int) shown.stream().filter(tile -> !drawn.containsKey(tile)).count() - gaps();
    }

    /** The finest level among the tiles shown, or the coarsest. */
    public int level(final boolean finest) {
        return shown.stream().mapToInt(tile -> tile.key().level()).reduce(finest ? Math::max : Math::min).orElse(0);
    }

    @Override
    public void close() {
        drawn.values().forEach(TileDrawn::close);
        drawn.clear();
        looks.close();
        builder.close();
    }
}
