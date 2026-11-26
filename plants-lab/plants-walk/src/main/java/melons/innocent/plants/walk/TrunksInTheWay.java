package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Bough;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * The trunks of a forest without end that are in the way near an eye: each tree standing in the squares some way
 * each way of the eye's own, as a block as thick as its trunk and as high, but those that are gone. Said for where
 * the eye is, and to be said again once it has strayed two squares from there.
 */
final class TrunksInTheWay {

    /** How many squares each way from the eye's own the trunks are in the way, and how far it goes before more. */
    private static final long POSTED = 6;
    private static final long STRAYED = 2;

    private final Endless endless;
    private final List<List<WoodDrawn.Planted>> planted;
    /** Each grown tree's trunk: kept, since asking a tree for its boughs grows it afresh. */
    private final List<Bough> trunks;
    /** Whether a tree standing at so far across and along is gone. */
    private final BiPredicate<Double, Double> gone;
    private long across;
    private long along;

    /** {@code endless}, its samples' trees {@code planted}, its trees {@code grown}, but those that are {@code gone}. */
    TrunksInTheWay(final Endless endless, final List<List<WoodDrawn.Planted>> planted,
                   final List<WoodDrawn.Grown> grown, final BiPredicate<Double, Double> gone) {
        this.endless = endless;
        this.planted = planted;
        this.trunks = grown.stream().map(tree -> tree.tree().boughs().getFirst()).toList();
        this.gone = gone;
    }

    /** The trunks in the way near {@code eye}, said for where it now is. */
    List<Block> posted(final Vec3 eye) {
        final double side = endless.samples().side();
        across = (long) Math.floor(eye.x() / side);
        along = (long) Math.floor(eye.z() / side);
        final List<Block> blocks = new ArrayList<>();
        for (long b = along - POSTED; b <= along + POSTED; b++) {
            for (long a = across - POSTED; a <= across + POSTED; a++) {
                for (final WoodDrawn.Planted tree : endless.placed(a, b, planted.get(endless.sample(a, b)))) {
                    final double thick = trunks.get(tree.grown()).thick().getFirst() * tree.times();
                    if (!gone.test(tree.placed().at(0, 3), tree.placed().at(2, 3))) {
                        blocks.add(new Block(tree.placed().at(0, 3), tree.placed().at(2, 3), thick, thick, 0, 0,
                                trunks.get(tree.grown()).ends().y() * tree.times()));
                    }
                }
            }
        }
        return blocks;
    }

    /** Whether {@code eye} has strayed far enough from where they were last said for them to be said again. */
    boolean strayed(final Vec3 eye) {
        final double side = endless.samples().side();
        return Math.abs(Math.floor(eye.x() / side) - across) >= STRAYED
                || Math.abs(Math.floor(eye.z() / side) - along) >= STRAYED;
    }
}
