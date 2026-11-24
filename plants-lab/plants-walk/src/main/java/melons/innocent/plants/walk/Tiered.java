package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * A forest's cells gathered into a tree of areas, three tiers deep, and each area's part in a frame chosen by how
 * far it is from the eye: a tile of {@value #WIDE} cells a side is one mesh of trunks alone while the eye is more
 * than its rings' furthest from it ({@link Rings}); nearer, it opens into tiles of {@value #MIDDLING} cells a side,
 * each one
 * mesh at limbs while the eye is further than their second; nearer, into its cells, each
 * trees or a mesh as its own ring says ({@link Ringed}). So far forest is a few large meshes and near forest many
 * small ones, and every cell is drawn by exactly one area: its owner. An area that has opened closes again only a
 * tenth further off. The distance is to the area's nearest edge, through the air. No card.
 *
 * <p>Areas are numbered: the cells first, as the forest numbers them; then the middling tiles; then the wide.
 */
public final class Tiered {

    private static final int MIDDLING = 4;
    private static final int WIDE = 8;
    private static final double METRE = 100;
    private static final double BACK = 0.9;
    private static final double HALF = 750;

    private final Forested forest;
    private final Ringed ringed;
    private final Rings set;
    private Rings rings;
    private final int along;
    private final Stage[] want;
    private final boolean[] open;
    private final int[] owner;

    /** The areas of {@code forest}, whose cells along a side are a multiple of {@value #WIDE}. */
    public Tiered(final Forested forest) {
        this(forest, new Rings());
    }

    /** The same, its rings reaching as {@code rings} says. */
    public Tiered(final Forested forest, final Rings rings) {
        if (forest.along() % WIDE != 0) {
            throw new IllegalArgumentException("a forest's cells along a side are a multiple of " + WIDE + ", not "
                    + forest.along());
        }
        this.forest = forest;
        this.ringed = new Ringed(forest, rings);
        this.set = rings;
        this.rings = rings;
        this.along = forest.along();
        this.want = new Stage[areas()];
        this.open = new boolean[areas()];
        this.owner = new int[forest.cells()];
    }

    /** Its rings drawn in to {@code quality} of how far they are set to reach, all by the same share. */
    public void drawnIn(final double quality) {
        this.rings = set.scaled(quality);
        ringed.ringed(rings);
    }

    /** How many areas there are, of all three tiers. */
    public int areas() {
        return forest.cells() + first(WIDE) - forest.cells() + (along / WIDE) * (along / WIDE);
    }

    /** The number of the first tile {@code wide} cells a side. */
    private int first(final int wide) {
        return wide == MIDDLING ? forest.cells() : forest.cells() + (along / MIDDLING) * (along / MIDDLING);
    }

    /** How many cells a side area number {@code area} is. */
    public int wide(final int area) {
        return area < first(MIDDLING) ? 1 : area < first(WIDE) ? MIDDLING : WIDE;
    }

    /** The cells of area number {@code area}, by their numbers. */
    public List<Integer> cells(final int area) {
        final int wide = wide(area);
        if (wide == 1) {
            return List.of(area);
        }
        final int at = area - first(wide);
        final int across = at % (along / wide) * wide;
        final int deep = at / (along / wide) * wide;
        final List<Integer> cells = new ArrayList<>();
        for (int row = deep; row < deep + wide; row++) {
            for (int cell = across; cell < across + wide; cell++) {
                cells.add(row * along + cell);
            }
        }
        return cells;
    }

    /** The areas cell number {@code cell} lies in: itself, its middling tile and its wide one. */
    public List<Integer> over(final int cell) {
        return List.of(cell, tile(cell, MIDDLING), tile(cell, WIDE));
    }

    private int tile(final int cell, final int wide) {
        return first(wide) + cell / along / wide * (along / wide) + cell % along / wide;
    }

    /** The middle of area number {@code area}, on the ground. */
    public Vec3 middle(final int area) {
        final List<Integer> cells = cells(area);
        return forest.middle(cells.getFirst()).plus(forest.middle(cells.getLast())).times(0.5);
    }

    /** How far across area number {@code area} is, in centimetres. */
    public double side(final int area) {
        return wide(area) * forest.side() / along;
    }

    /** Every area's part chosen for a frame seen from {@code eye}. */
    public void chosen(final Vec3 eye) {
        java.util.Arrays.fill(want, null);
        for (int wide = first(WIDE); wide < areas(); wide++) {
            if (!opened(wide, eye, rings.limbs())) {
                owned(wide, Stage.WHOLE);
                continue;
            }
            for (final int corner : corners(wide)) {
                final int middling = tile(corner, MIDDLING);
                if (!opened(middling, eye, rings.boughs())) {
                    owned(middling, off(middling, eye) >= rings.limbs() ? Stage.WHOLE : Stage.LIMBS);
                    continue;
                }
                for (final int cell : cells(middling)) {
                    want[cell] = ringed.wanted(cell, eye).orElse(null);
                    owner[cell] = cell;
                }
            }
        }
    }

    /** One cell of each middling tile within wide tile number {@code wide}. */
    private List<Integer> corners(final int wide) {
        return cells(wide).stream().filter(cell -> cell % along % MIDDLING == 0 && cell / along % MIDDLING == 0)
                .toList();
    }

    private void owned(final int area, final Stage level) {
        want[area] = level;
        cells(area).forEach(cell -> owner[cell] = area);
    }

    /** Whether area number {@code area} is open: nearer the eye than {@code opens}, or not yet a tenth beyond. */
    private boolean opened(final int area, final Vec3 eye, final double opens) {
        open[area] = off(area, eye) < (open[area] ? opens / BACK : opens);
        return open[area];
    }

    /** How far {@code eye} is from the nearest edge of area number {@code area}, in metres. */
    private double off(final int area, final Vec3 eye) {
        final double across = middle(area).plus(new Vec3(0, HALF, 0)).minus(eye).length();
        return Math.max(0, across - side(area) * Math.sqrt(0.5)) / METRE;
    }

    /** The level area number {@code area} is drawn as one mesh at; none, if it is not drawn as a mesh. */
    public Stage want(final int area) {
        return want[area];
    }

    /** The area that draws cell number {@code cell}. */
    public int owner(final int cell) {
        return owner[cell];
    }

    /** Whether cell number {@code cell} is drawn as trees. */
    public boolean trees(final int cell) {
        return owner[cell] == cell && want[cell] == null;
    }
}
