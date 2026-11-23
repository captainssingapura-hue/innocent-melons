package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Surface;
import melons.innocent.plants.tree.Bough;
import melons.innocent.plants.tree.Pruned;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.tree.Tree;
import melons.innocent.plants.tree.Wooded;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One tree sent to the card at every stage, each worked out from the tree as grown: its kept wood, and its clumps
 * by how much they hide, with the colour its bark comes to over the wood they stand for ({@link Afar}). All of it is
 * sent once, when the tree is, so changing stage while frames are drawn sends nothing.
 */
final class StagedTree implements AutoCloseable {

    private final double height;
    private final Map<Stage, Shaped> woods = new EnumMap<>(Stage.class);
    private final Map<Stage, Map<Double, Shaped>> hazes = new EnumMap<>(Stage.class);
    private final Map<Stage, Vec3> hues = new EnumMap<>(Stage.class);
    private final List<Shaped> sent = new ArrayList<>();

    /** {@code tree}, wearing {@code bark}, on {@code card}, each clump at the nearest of {@code levels} of hiding. */
    StagedTree(final Card card, final Laid laid, final Tree tree, final Surface bark,
                final List<Double> levels) {
        final List<Bough> boughs = tree.boughs();
        this.height = boughs.stream().flatMap(bough -> bough.points().stream()).mapToDouble(Vec3::y).max().orElse(0);
        try {
            for (final Stage stage : Stage.values()) {
                final Pruned pruned = new Pruned(boughs, stage);
                woods.put(stage, kept(new Shaped(card, laid, pruned.wood())));
                final Map<Double, Shaped> levelled = new LinkedHashMap<>();
                pruned.hazes(levels).forEach((hides, mesh) -> levelled.put(hides, kept(new Shaped(card, laid, mesh))));
                hazes.put(stage, levelled);
                if (!pruned.dropped().isEmpty()) {
                    hues.put(stage, new Afar(bark).over(new Wooded(pruned.dropped()).formed()));
                }
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    private Shaped kept(final Shaped shaped) {
        sent.add(shaped);
        return shaped;
    }

    /** How high it stands, in its own frame. */
    double height() {
        return height;
    }

    /** Its kept wood at {@code stage}. */
    Shaped wood(final Stage stage) {
        return woods.get(stage);
    }

    /** Its clumps at {@code stage}, by how much those in each hide: none at a stage that drops nothing. */
    Map<Double, Shaped> hazes(final Stage stage) {
        return hazes.get(stage);
    }

    /** The colour of its clumps at {@code stage}: white, at a stage that has none. */
    Vec3 hue(final Stage stage) {
        return hues.getOrDefault(stage, new Vec3(1, 1, 1));
    }

    @Override
    public void close() {
        sent.reversed().forEach(Shaped::close);
        sent.clear();
    }
}
