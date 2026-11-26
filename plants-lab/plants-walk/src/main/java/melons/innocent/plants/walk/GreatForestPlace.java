package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Bough;
import melons.innocent.plants.tree.Kind;

import java.util.ArrayList;
import java.util.List;

/**
 * A great forest on level ground, to walk and fly in: two kilometres square, some 78,000 bare oaks, walnuts and white
 * birches seven metres apart, the avatar starting at its middle. Its trees are worked out cell by cell from where
 * each cell is ({@link Forested}); near the eye they are trees, and beyond they are meshes baked ahead
 * ({@link ForestDrawn}). Every trunk is in the avatar's way as a post; boughs are not. How big it is, how thick, and
 * its two trees of each kind are its author's.
 */
public final class GreatForestPlace implements Place {

    private static final double SIDE = 200_000;
    private static final double CELL = 5_000;
    private static final int EACH = 2;
    private static final long CHANCE = 20261124L;

    private final List<WoodDrawn.Grown> grown = new ArrayList<>();
    private ForestDrawn forest;

    /** The forest, on {@code card}, its layouts as {@code laid} says. */
    public GreatForestPlace(final Card card, final Laid laid) {
        for (final Kind kind : List.of(Kind.OAK, Kind.WALNUT, Kind.BIRCH)) {
            for (int number = 1; number <= EACH; number++) {
                grown.add(new WoodDrawn.Grown(kind, kind.tree(number)));
            }
        }
        this.forest = new ForestDrawn(card, laid, new Forested(SIDE, CELL, grown.size(), CHANCE), grown);
    }

    @Override
    public String said() {
        return forest.said();
    }

    @Override
    public Obstacles inTheWay() {
        final List<Bough> trunks = grown.stream().map(tree -> tree.tree().boughs().getFirst()).toList();
        final List<Block> posts = new ArrayList<>();
        for (final WoodDrawn.Planted tree : forest.trees()) {
            final Bough trunk = trunks.get(tree.grown());
            final double thick = trunk.thick().getFirst() * tree.times();
            posts.add(new Block(tree.placed().at(0, 3), tree.placed().at(2, 3), thick, thick, 0, 0,
                    trunk.ends().y() * tree.times()));
        }
        return new Obstacles(posts);
    }

    @Override
    public void readied(final Camera camera) {
        forest.readied(camera);
    }

    @Override
    public boolean settled() {
        return forest.settled();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        forest.drawn(pass, camera);
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        forest.seenThrough(pass, camera);
    }

    @Override
    public void close() {
        if (forest != null) {
            forest.close();
            forest = null;
        }
    }
}
