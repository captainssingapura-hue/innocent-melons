package melons.innocent.terrain.bench;

import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.Tiling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * What a tiled land keeps, level by level, the finest last: how far apart each level's points are; how many of its
 * tiles are shown, wanted ahead and drawn; how many have their data built and their meshes made, and about how much
 * room those take; how many are being built; and how many are on the card, and how much room they take there. Then
 * all of them together, and the card's budget; and how many tiles have been put on the card, and how long each took.
 * Told by the card's thread, read on the console's.
 */
public final class Cached {

    private final List<Row> rows;
    private final long budget;
    private final long put;
    private final long putNanos;

    /** What {@code tiling}'s land keeps: built as {@code stock} says, on the card as {@code carded}, and so on. */
    public Cached(final Tiling tiling, final Map<Integer, TileBuilder.Stock> stock, final Collection<TileDrawn> carded,
                  final List<Chosen> shown, final List<Chosen> ahead, final List<TileDrawn> drawn, final long budget,
                  final long put, final long putNanos) {
        this.rows = rows(tiling, stock, carded, shown, ahead, drawn);
        this.budget = budget;
        this.put = put;
        this.putNanos = putNanos;
    }

    /** How many tiles have been put on the card all told. */
    public long put() {
        return put;
    }

    /** How long, on the average, putting a tile on the card has taken, in milliseconds. */
    public double putting() {
        return put == 0 ? 0 : putNanos / 1e6 / put;
    }

    /** Each level's, the finest last. */
    public List<Row> rows() {
        return rows;
    }

    /** How much room the card may give the tiles, in bytes. */
    public long budget() {
        return budget;
    }

    /**
     * What is kept of one level.
     *
     * @param level     which level
     * @param spacing   how far apart its points are, in centimetres
     * @param shown     how many of its tiles are shown
     * @param ahead     how many are wanted ahead
     * @param drawn     how many are drawn
     * @param data      how many have their data built
     * @param dataBytes about how much room that takes
     * @param meshes    how many have their meshes made
     * @param meshBytes about how much room those take
     * @param building  how many are being built
     * @param carded    how many are on the card
     * @param cardBytes about how much room they take there
     */
    public record Row(int level, double spacing, int shown, int ahead, int drawn, int data, long dataBytes,
                      int meshes, long meshBytes, int building, int carded, long cardBytes) {
    }

    /** All of them together, the level and spacing of the finest. */
    public Row total() {
        Row sum = new Row(-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        for (final Row row : rows) {
            sum = new Row(row.level(), row.spacing(), sum.shown() + row.shown(), sum.ahead() + row.ahead(),
                    sum.drawn() + row.drawn(), sum.data() + row.data(), sum.dataBytes() + row.dataBytes(),
                    sum.meshes() + row.meshes(), sum.meshBytes() + row.meshBytes(), sum.building() + row.building(),
                    sum.carded() + row.carded(), sum.cardBytes() + row.cardBytes());
        }
        return sum;
    }

    private List<Row> rows(final Tiling tiling, final Map<Integer, TileBuilder.Stock> stock,
                                  final Collection<TileDrawn> carded, final List<Chosen> shown,
                                  final List<Chosen> ahead, final List<TileDrawn> drawn) {
        final Map<Integer, long[]> counts = new TreeMap<>();
        stock.keySet().forEach(level -> counts.computeIfAbsent(level, l -> new long[5]));
        shown.forEach(tile -> counts.computeIfAbsent(tile.key().level(), l -> new long[5])[0]++);
        ahead.forEach(tile -> counts.computeIfAbsent(tile.key().level(), l -> new long[5])[1]++);
        drawn.forEach(tile -> counts.computeIfAbsent(tile.chosen().key().level(), l -> new long[5])[2]++);
        for (final TileDrawn on : carded) {
            final long[] count = counts.computeIfAbsent(on.chosen().key().level(), l -> new long[5]);
            count[3]++;
            count[4] += on.carded();
        }
        final List<Row> rows = new ArrayList<>();
        counts.forEach((level, count) -> {
            final TileBuilder.Stock built = stock.getOrDefault(level, new TileBuilder.Stock(0, 0, 0, 0, 0));
            rows.add(new Row(level, tiling.spacing(level), (int) count[0], (int) count[1], (int) count[2],
                    built.data(), built.dataBytes(), built.meshes(), built.meshBytes(), built.building(),
                    (int) count[3], count[4]));
        });
        return List.copyOf(rows);
    }
}
