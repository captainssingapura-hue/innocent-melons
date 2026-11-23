package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Bough;
import melons.innocent.plants.tree.Kind;

import java.util.ArrayList;
import java.util.List;

/**
 * A forest of four woods with no leaves, ahead of where the avatar starts, in two rows of two with a ride thirty
 * metres wide between them each way: on the left an oak wood, nearer, and a birch wood beyond it; on the right a
 * walnut wood, nearer, and a wood of all three mixed beyond it. Each is a hundred metres square, with so many trees
 * (250 unless said), and the four have only six different trees among them, two of each kind, each standing many
 * times over, so the forest is no more to send to the card than six trees at every stage. Each tree is drawn as
 * finely as its distance calls for ({@link WoodDrawn}). A trunk is in the avatar's way, as a post; boughs are not.
 */
public final class ForestPlace implements Place {

    /** How wide a wood is each way, how wide the rides between them are, and how far ahead the near two begin. */
    private static final double SIDE = 10_000;
    private static final double RIDE = 3_000;
    private static final double AHEAD = 4_000;
    private static final int TREES = 250;
    private static final long CHANCE = 20261124L;

    private final List<Woodland> woods;
    private final List<WoodDrawn.Grown> grown = new ArrayList<>();
    private final List<Woodland.Planted> planted = new ArrayList<>();
    private WoodDrawn wood;

    /** The forest, on {@code card}, its layouts as {@code laid} says. */
    public ForestPlace(final Card card, final Laid laid) {
        this(card, laid, TREES);
    }

    /** The forest with {@code each} trees in each of its woods. */
    public ForestPlace(final Card card, final Laid laid, final int each) {
        final double near = AHEAD + SIDE / 2;
        final double far = near + SIDE + RIDE;
        final double side = (SIDE + RIDE) / 2;
        this.woods = List.of(
                new Woodland(List.of(Kind.OAK), new Vec3(-side, 0, near), SIDE, SIDE, each, CHANCE),
                new Woodland(List.of(Kind.WALNUT), new Vec3(side, 0, near), SIDE, SIDE, each, CHANCE + 1),
                new Woodland(List.of(Kind.BIRCH), new Vec3(-side, 0, far), SIDE, SIDE, each, CHANCE + 2),
                new Woodland(List.of(Kind.values()), new Vec3(side, 0, far), SIDE, SIDE, each, CHANCE + 3));
        final int numbers = woods.getFirst().each();
        for (final Kind kind : Kind.values()) {
            for (int number = 1; number <= numbers; number++) {
                grown.add(new WoodDrawn.Grown(kind, kind.tree(number)));
            }
        }
        woods.forEach(woodland -> planted.addAll(woodland.standing()));
        this.wood = new WoodDrawn(card, laid, grown, planted.stream()
                .map(tree -> new WoodDrawn.Planted(grownOf(tree), tree.placed(), tree.times())).toList());
    }

    /** Which of the six grown trees {@code tree} is, by number. */
    private int grownOf(final Woodland.Planted tree) {
        return tree.kind().ordinal() * woods.getFirst().each() + tree.number() - 1;
    }

    /** The four woods, the oak's, the walnut's, the birch's and the mixed. */
    public List<Woodland> woods() {
        return woods;
    }

    /** How many trees stand in the forest. */
    public int trees() {
        return planted.size();
    }

    @Override
    public Obstacles inTheWay() {
        final List<Bough> trunks = grown.stream().map(tree -> tree.tree().boughs().getFirst()).toList();
        final List<Block> posts = new ArrayList<>();
        for (final Woodland.Planted tree : planted) {
            final Bough trunk = trunks.get(grownOf(tree));
            final double thick = trunk.thick().getFirst() * tree.times();
            posts.add(new Block(tree.stands().x(), tree.stands().z(), thick, thick, 0, 0,
                    trunk.ends().y() * tree.times()));
        }
        return new Obstacles(posts);
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        wood.drawn(pass, camera);
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        wood.seenThrough(pass, camera);
    }

    @Override
    public void close() {
        if (wood != null) {
            wood.close();
            wood = null;
        }
    }
}
