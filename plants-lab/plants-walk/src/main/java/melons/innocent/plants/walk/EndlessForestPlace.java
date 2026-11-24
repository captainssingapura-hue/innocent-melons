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
 * A forest without end, in winter, made of a set of samples ({@link Samples}, {@link Endless}) and drawn from them
 * alone ({@link SampledDrawn}): oaks, walnuts and white birches, two grown of each. The run says how many samples
 * there are and how big each is, {@code -Dforest.samples=} (32) and {@code -Dforest.sample=} metres (25); how far
 * each level reaches ({@link Reaches}); and within how many metres of the eye its squares cast shadows by rays,
 * {@code -Dforest.shadows=} (300).
 *
 * <p>The forest goes on wherever the eye goes. What is in the avatar's way does not, as things are: a walk asks a
 * place once for what is in the way, so the trunks are posts within a kilometre of the world's middle each way, and
 * beyond that the trees are walked through. The level ground a walk draws ends two kilometres out.
 */
public final class EndlessForestPlace implements Place {

    private static final double METRE = 100;
    private static final int EACH = 2;
    private static final long CHANCE = 20261124L;
    /** How far from the world's middle, each way, the trunks are in the way, in centimetres. */
    private static final double POSTED = 100_000;

    private final List<WoodDrawn.Grown> grown = new ArrayList<>();
    private final Endless endless;
    private final SampledDrawn forest;

    /** The forest on {@code card}, as the run says it. */
    public EndlessForestPlace(final Card card, final Laid laid) {
        this(card, laid, Integer.getInteger("forest.samples", 32),
                Double.parseDouble(System.getProperty("forest.sample", "25")), new Reaches(),
                Double.parseDouble(System.getProperty("forest.shadows", "300")));
    }

    /**
     * The forest on {@code card}, of {@code samples} samples {@code side} metres each way, casting shadows by rays
     * within {@code shadows} metres of the eye: none, for nothing.
     */
    public EndlessForestPlace(final Card card, final Laid laid, final int samples, final double side,
                              final Reaches reaches, final double shadows) {
        for (final Kind kind : Kind.values()) {
            for (int number = 1; number <= EACH; number++) {
                grown.add(new WoodDrawn.Grown(kind, kind.tree(number)));
            }
        }
        this.endless = new Endless(new Samples(samples, side * METRE, grown.size(), CHANCE), CHANCE);
        this.forest = new SampledDrawn(card, laid, endless, grown, reaches, shadows * METRE);
    }

    @Override
    public String said() {
        return forest.said();
    }

    @Override
    public Obstacles inTheWay() {
        final List<Bough> trunks = grown.stream().map(tree -> tree.tree().boughs().getFirst()).toList();
        final List<Block> posts = new ArrayList<>();
        final long reach = (long) Math.ceil(POSTED / endless.samples().side());
        for (long along = -reach; along < reach; along++) {
            for (long across = -reach; across < reach; across++) {
                for (final WoodDrawn.Planted tree : endless.trees(across, along)) {
                    final Bough trunk = trunks.get(tree.grown());
                    final double thick = trunk.thick().getFirst() * tree.times();
                    posts.add(new Block(tree.placed().at(0, 3), tree.placed().at(2, 3), thick, thick, 0, 0,
                            trunk.ends().y() * tree.times()));
                }
            }
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

    /** How many triangles the squares readied last come to. */
    public long triangles() {
        return forest.triangles();
    }

    @Override
    public void close() {
        forest.close();
    }
}
