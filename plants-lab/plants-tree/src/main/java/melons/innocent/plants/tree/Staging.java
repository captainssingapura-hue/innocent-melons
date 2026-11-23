package melons.innocent.plants.tree;

import java.util.HashMap;
import java.util.Map;

/**
 * Which stage each of a wood's trees is drawn at, by how wide a pixel is where it stands: the coarsest whose thinnest
 * kept bough is still a pixel across. A pixel's width so far off is the distance times the picture's spread, which
 * is how wide the view is there for each pixel of the picture's height ({@code 2 tan(fov / 2) / pixels}).
 *
 * <p>Going coarser, a tree goes as soon as it may; going finer again, only once it is a tenth nearer than where it
 * went, as the engine's detail of buildings comes back, so a tree standing at a stage's edge does not flicker between
 * the two. Each tree is told apart by its number in the wood, and remembers its own stage.
 *
 * <p>The engine's deciding parts, which choose how finely the land and buildings are drawn, have no rule for trees;
 * this is that rule, for them to take over when the engine is rebuilt.
 */
public final class Staging {

    /** Gone coarser, a tree comes back finer this much nearer. */
    private static final double BACK = 0.9;

    private final double spread;
    private final Map<Integer, Stage> last = new HashMap<>();

    /** For a picture whose pixels are each {@code spread} of the distance across. */
    public Staging(final double spread) {
        this.spread = spread;
    }

    /** For a picture {@code pixels} high seen through a view {@code fieldOfView} degrees high. */
    public Staging(final double fieldOfView, final int pixels) {
        this(2 * Math.tan(Math.toRadians(fieldOfView) / 2) / pixels);
    }

    /** The coarsest stage a tree {@code times} its grown size may be drawn at {@code distance} off. */
    public Stage coarsest(final double distance, final double times) {
        final double pixel = distance * spread;
        Stage stage = Stage.FINE;
        for (final Stage next : Stage.values()) {
            if (2 * next.least() * times <= pixel) {
                stage = next;
            }
        }
        return stage;
    }

    /** The stage tree number {@code tree} of the wood, {@code times} its grown size, is drawn at {@code distance} off. */
    public Stage of(final int tree, final double distance, final double times) {
        final Stage was = last.get(tree);
        final Stage may = coarsest(distance, times);
        final Stage now;
        if (was == null || may.ordinal() >= was.ordinal()) {
            now = may;
        } else {
            final Stage back = coarsest(distance / BACK, times);
            now = back.ordinal() < was.ordinal() ? back : was;
        }
        last.put(tree, now);
        return now;
    }
}
