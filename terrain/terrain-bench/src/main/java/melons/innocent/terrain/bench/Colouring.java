package melons.innocent.terrain.bench;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * The ground coloured layer by layer, away from the card's thread: at first every point in the painting's first
 * layer, the land's raw shape; then each later layer in turn, laid over those before wherever it lies, read from the
 * grid's heights and slopes, its rows worked out side by side. After each layer what it has come to so far is told,
 * for the card's thread to take when it will.
 */
public final class Colouring {

    private final Grid grid;
    private final Painting painting;
    private final AtomicReference<Coloured> latest;

    /** {@code grid}'s ground, to be coloured as {@code painting} says. */
    public Colouring(final Grid grid, final Painting painting) {
        this.grid = grid;
        this.painting = painting;
        this.latest = new AtomicReference<>(new Coloured(new int[grid.across() * grid.along()], 1));
    }

    /** Begun on a thread of its own, which ends with the bench. */
    public void start() {
        Thread.ofPlatform().daemon().name("colouring").start(this::coloured);
    }

    /** The grid it colours. */
    public Grid grid() {
        return grid;
    }

    /** What it has come to so far. */
    public Coloured latest() {
        return latest.get();
    }

    /** How many layers there are to lay, the first among them. */
    public int layers() {
        return painting.legend().size();
    }

    private void coloured() {
        final int[] layers = new int[grid.across() * grid.along()];
        for (int layer = 1; layer < layers(); layer++) {
            final int laying = layer;
            IntStream.range(0, grid.along()).parallel().forEach(j -> {
                for (int i = 0; i < grid.across(); i++) {
                    if (painting.covers(laying, grid.sample(i, j))) {
                        layers[j * grid.across() + i] = laying;
                    }
                }
            });
            latest.set(new Coloured(layers.clone(), layer + 1));
        }
    }

    /**
     * What the colouring has come to: each point's layer, row by row from the nearest, and how many layers have been
     * laid, the first among them.
     *
     * @param layers each point's layer
     * @param laid   how many layers have been laid
     */
    public record Coloured(int[] layers, int laid) {
    }
}
