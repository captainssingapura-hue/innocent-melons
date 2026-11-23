package melons.innocent.terrain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A ground surveyed into a topographic map: its heights taken once on a grid over a stretch, side by side, and from
 * them the loops each level runs along ({@link Marching}), each made no finer than need be, a point kept only where
 * leaving it out would move the line more than so far (Douglas and Peucker's); and inside a loop with nothing marked
 * in it, the top it rises to or the bottom it sinks to. How a map is drafted from a ground worked out some other
 * way: what it makes is data, and the map stands on its own after.
 */
public final class Surveyor {

    private final double[][] heights;
    private final Extent over;
    private final double every;

    /** {@code ground} surveyed over {@code over}, a height every {@code every} centimetres each way. */
    public Surveyor(final Ground ground, final Extent over, final double every) {
        if (!(every > 0)) {
            throw new IllegalArgumentException("heights some way apart: " + every);
        }
        this.over = over;
        this.every = every;
        final int across = (int) Math.floor(over.across() / every) + 1;
        final int along = (int) Math.floor(over.along() / every) + 1;
        this.heights = new double[along][across];
        IntStream.range(0, along).parallel().forEach(j -> {
            for (int i = 0; i < across; i++) {
                heights[j][i] = ground.height(over.x0() + i * every, over.z0() + j * every);
            }
        });
    }

    /**
     * The loops {@code level} runs along, each no further than {@code tolerance} from where it was traced, and none
     * going round less ground than {@code least} square centimetres.
     */
    public List<Loop> loops(final double level, final double tolerance, final double least) {
        final List<Loop> loops = new ArrayList<>();
        for (final List<Spot> traced : new Marching(heights, over.x0(), over.z0(), every).loops(level)) {
            final List<Spot> kept = simplified(traced, tolerance);
            if (kept.size() >= 3) {
                final Loop loop = new Loop(kept);
                if (loop.area() >= least) {
                    loops.add(loop);
                }
            }
        }
        return loops;
    }

    /**
     * The top the ground rises to inside {@code loop}, traced at {@code level}, or the bottom it sinks to, whichever
     * way most of the ground inside it lies from the level; if that is more than {@code beyond} past the level.
     */
    public Optional<SpotHeight> extreme(final Loop loop, final double level, final double beyond) {
        double top = Double.NEGATIVE_INFINITY;
        double bottom = Double.POSITIVE_INFINITY;
        Spot highest = null;
        Spot lowest = null;
        int above = 0;
        int below = 0;
        for (int j = 0; j < heights.length; j++) {
            for (int i = 0; i < heights[j].length; i++) {
                final double x = over.x0() + i * every;
                final double z = over.z0() + j * every;
                if (loop.boxed(x, z) && loop.inside(x, z)) {
                    final double h = heights[j][i];
                    if (h > top) {
                        top = h;
                        highest = new Spot(x, z);
                    }
                    if (h < bottom) {
                        bottom = h;
                        lowest = new Spot(x, z);
                    }
                    if (h > level) {
                        above++;
                    } else {
                        below++;
                    }
                }
            }
        }
        if (above >= below && highest != null && top - level > beyond) {
            return Optional.of(new SpotHeight(highest, top));
        }
        if (below > above && lowest != null && level - bottom > beyond) {
            return Optional.of(new SpotHeight(lowest, bottom));
        }
        return Optional.empty();
    }

    /** How high the ground is at the grid point nearest ({@code x}, {@code z}), held to the grid. */
    public double height(final double x, final double z) {
        final int i = (int) Math.clamp(Math.round((x - over.x0()) / every), 0, heights[0].length - 1);
        final int j = (int) Math.clamp(Math.round((z - over.z0()) / every), 0, heights.length - 1);
        return heights[j][i];
    }

    /** {@code loop}, closed round, kept to the points without which it would move more than {@code tolerance}. */
    private List<Spot> simplified(final List<Spot> loop, final double tolerance) {
        final int n = loop.size();
        int far = 0;
        for (int i = 1; i < n; i++) {
            if (loop.get(i).to(loop.getFirst()) > loop.get(far).to(loop.getFirst())) {
                far = i;
            }
        }
        final boolean[] keep = new boolean[n + 1];
        keep[0] = true;
        keep[far] = true;
        keep[n] = true;
        final Deque<int[]> pieces = new ArrayDeque<>(List.of(new int[] {0, far}, new int[] {far, n}));
        while (!pieces.isEmpty()) {
            final int[] piece = pieces.pop();
            final Spot a = loop.get(piece[0] % n);
            final Spot b = loop.get(piece[1] % n);
            int worst = -1;
            double most = tolerance;
            for (int i = piece[0] + 1; i < piece[1]; i++) {
                final double off = off(loop.get(i % n), a, b);
                if (off > most) {
                    most = off;
                    worst = i;
                }
            }
            if (worst >= 0) {
                keep[worst] = true;
                pieces.push(new int[] {piece[0], worst});
                pieces.push(new int[] {worst, piece[1]});
            }
        }
        final List<Spot> kept = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (keep[i]) {
                kept.add(loop.get(i));
            }
        }
        return kept;
    }

    /** How far {@code p} is from the line from {@code a} to {@code b}, its ends included. */
    private double off(final Spot p, final Spot a, final Spot b) {
        final double dx = b.x() - a.x();
        final double dz = b.z() - a.z();
        final double square = dx * dx + dz * dz;
        final double t = square == 0 ? 0 : Math.clamp(((p.x() - a.x()) * dx + (p.z() - a.z()) * dz) / square, 0, 1);
        return Math.hypot(p.x() - a.x() - t * dx, p.z() - a.z() - t * dz);
    }
}
