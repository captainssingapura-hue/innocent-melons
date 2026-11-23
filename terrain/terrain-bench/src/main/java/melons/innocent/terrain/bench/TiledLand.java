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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * The land on the card in tiles. The tiles shown and the tiles wanted ahead are built away from the card, data and
 * meshes ({@link TileBuilder}); built, they are put on the card, the shown first, then those ahead, for no more than
 * {@link #PUTTING} of each frame, and kept there, drawn or not, till what is on the card takes more than its budget,
 * when those drawn longest ago are taken off first. Each frame only the tiles shown are drawn, and, where a tile shown
 * is not on the card yet, any old tile over its ground in its stead, till all the tiles shown over that ground are on
 * and are drawn at once, so no hole opens. Each layer's look built once for every tile. What is kept, here and on the
 * card, told level by level ({@link Cached}). On the card's thread only. Closed, its tiles, its looks and its building
 * threads are all gone.
 */
public final class TiledLand implements AutoCloseable {

    /** How much of a frame, at most, is spent putting tiles on the card, in nanoseconds. */
    private static final long PUTTING = 6_000_000;
    private static final long MB = 1L << 20;

    private final Card card;
    private final Laid laid;
    private final Painting painting;
    private final Tiling tiling;
    private final TileBuilder builder;
    private final Looks looks;
    private final long cardBudget;
    /** The tiles on the card. */
    private final Map<TileKey, TileDrawn> carded = new HashMap<>();
    /** The frame each tile on the card was last drawn in, or put on. */
    private final Map<TileKey, Long> drawnIn = new HashMap<>();
    private long frame;
    private long cardBytes;
    private Shown shown = new Shown(List.of());
    private List<Chosen> ahead = List.of();
    private List<TileDrawn> drawing = List.of();
    private int gaps;
    private int stale;
    /** How many tiles were put on the card all told, and how long that took, in nanoseconds. */
    private long putCount;
    private long putNanos;

    /** The land of {@code ground}, tiled as {@code tiling} says, painted as {@code painting} says. */
    public TiledLand(final Card card, final Laid laid, final Painting painting, final Tiling tiling,
                     final Ground ground, final double up) {
        this(card, laid, painting, tiling, level -> ground, up, 1_536 * MB, 512 * MB);
    }

    /**
     * The land tiled as {@code tiling} says, each tile of the ground {@code grounds} says its level is seen as,
     * painted as {@code painting} says, drawn {@code up} times as high; what is built kept within {@code held} bytes
     * here, and what is put on the card within {@code onCard} bytes there.
     */
    public TiledLand(final Card card, final Laid laid, final Painting painting, final Tiling tiling,
                     final IntFunction<Ground> grounds, final double up, final long held, final long onCard) {
        this.card = card;
        this.laid = laid;
        this.painting = painting;
        this.tiling = tiling;
        this.cardBudget = onCard;
        this.builder = new TileBuilder(tiling, grounds, painting, up,
                Math.max(1, Runtime.getRuntime().availableProcessors() - 2), held);
        this.looks = new Looks(card, laid, painting);
    }

    /** {@code shown} to be shown, nearest first, and {@code ahead} built and put on besides, the soonest first. */
    public void want(final List<Chosen> shown, final List<TileKey> ahead) {
        this.shown = new Shown(shown);
        final Set<TileKey> keys = new LinkedHashSet<>();
        shown.forEach(tile -> keys.add(tile.key()));
        final List<Chosen> besides = new ArrayList<>();
        for (final TileKey key : ahead) {
            if (keys.add(key)) {
                besides.add(new Chosen(key, 0));
            }
        }
        this.ahead = List.copyOf(besides);
        final List<Chosen> all = new ArrayList<>(shown);
        all.addAll(besides);
        builder.want(all);
    }

    /**
     * The card brought up to what is wanted, for this frame: tiles built put on it, the shown first, for no more than
     * so long; those drawn longest ago taken off, past its budget, but for any over ground shown; and what is drawn
     * chosen, the shown on the card, and old tiles in the stead of those not.
     */
    public void kept() {
        frame++;
        final long began = System.nanoTime();
        for (final List<Chosen> tiles : List.of(shown.tiles(), ahead)) {
            for (final Chosen tile : tiles) {
                if (System.nanoTime() - began > PUTTING) {
                    break;
                }
                final TileDrawn on = carded.get(tile.key());
                if (on == null || !on.chosen().equals(tile)) {
                    final long putting = System.nanoTime();
                    builder.meshes(tile).ifPresent(meshes -> {
                        put(new TileDrawn(card, laid, meshes, painting.waterLayer()));
                        putCount++;
                    });
                    putNanos += System.nanoTime() - putting;
                }
            }
        }
        takenOff();
        chosen();
    }

    /** {@code tile} put on the card, in place of any of the same tile there before. */
    private void put(final TileDrawn tile) {
        final TileDrawn before = carded.put(tile.chosen().key(), tile);
        drawnIn.put(tile.chosen().key(), frame);
        cardBytes += tile.carded();
        if (before != null) {
            cardBytes -= before.carded();
            before.close();
        }
    }

    /** Past its budget, the tiles drawn longest ago taken off the card, but for any over ground shown. */
    private void takenOff() {
        if (cardBytes <= cardBudget) {
            return;
        }
        final List<TileKey> spare = new ArrayList<>(carded.keySet());
        spare.removeIf(key -> !shown.over(key).isEmpty());
        spare.sort(Comparator.comparingLong(drawnIn::get));
        for (final TileKey key : spare) {
            if (cardBytes <= cardBudget) {
                break;
            }
            final TileDrawn off = carded.remove(key);
            drawnIn.remove(key);
            cardBytes -= off.carded();
            off.close();
        }
    }

    /**
     * What is drawn this frame: each tile shown that is on the card as it is wanted; and each other tile on the card
     * over ground shown where not all the tiles shown over it are on yet, in their stead, they not drawn till all are.
     */
    private void chosen() {
        final Set<Chosen> held = new LinkedHashSet<>();
        final List<TileDrawn> standing = new ArrayList<>();
        for (final TileDrawn on : carded.values()) {
            if (!shown.has(on.chosen())) {
                final List<Chosen> over = shown.over(on.chosen().key());
                if (!over.isEmpty() && !over.stream().allMatch(this::onCard)) {
                    standing.add(on);
                    held.addAll(over);
                }
            }
        }
        final List<TileDrawn> drawn = new ArrayList<>(standing);
        int missing = 0;
        int covered = 0;
        for (final Chosen tile : shown.tiles()) {
            if (held.contains(tile)) {
                covered++;
            } else if (onCard(tile)) {
                drawn.add(carded.get(tile.key()));
            } else {
                missing++;
            }
        }
        drawing = List.copyOf(drawn);
        drawing.forEach(tile -> drawnIn.put(tile.chosen().key(), frame));
        gaps = missing;
        stale = covered;
    }

    /** Whether {@code tile} is on the card as it is wanted, its edges as they are wanted. */
    private boolean onCard(final Chosen tile) {
        final TileDrawn on = carded.get(tile.key());
        return on != null && on.chosen().equals(tile);
    }

    /** Its tiles drawn this frame drawn in {@code pass}, as {@code flight} has the land, seen by {@code camera}. */
    public void drawn(final Pass pass, final Flight flight, final Camera camera) {
        drawing.forEach(tile -> tile.drawn(pass, looks, flight, camera));
    }

    /** How many tiles are to be shown. */
    public int shown() {
        return shown.tiles().size();
    }

    /** How many tiles wanted, shown or ahead, have their meshes built. */
    public int built() {
        return builder.done();
    }

    /** How many tiles are drawn this frame. */
    public int drawn() {
        return drawing.size();
    }

    /** How many tiles to be shown are neither drawn nor stood in for by an old tile: holes in the land. */
    public int gaps() {
        return gaps;
    }

    /** How many tiles to be shown are stood in for by an old tile: detail not yet come. */
    public int stale() {
        return stale;
    }

    /** The finest level among the tiles shown, or the coarsest. */
    public int level(final boolean finest) {
        return shown.tiles().stream().mapToInt(tile -> tile.key().level()).reduce(finest ? Math::max : Math::min)
                .orElse(0);
    }

    /** What is kept, level by level, built and on the card, and what is shown, wanted ahead and drawn. */
    public Cached cached() {
        return new Cached(tiling, builder.stock(), carded.values(), shown.tiles(), ahead, drawing, cardBudget, putCount,
                putNanos);
    }

    @Override
    public void close() {
        carded.values().forEach(TileDrawn::close);
        carded.clear();
        looks.close();
        builder.close();
    }
}
