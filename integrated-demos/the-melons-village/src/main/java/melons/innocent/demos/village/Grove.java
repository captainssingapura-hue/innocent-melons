package melons.innocent.demos.village;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Bough;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A small wood of trees with no leaves, oaks, walnuts and white birches, on a piece of level ground so far across
 * and so deep, its middle at
 * {@code middle}. The trees are the plants lab's, as finely as they are grown. There are only a few different
 * trees in it, each standing many times over, turned a different way and a little bigger or smaller, so that the
 * wood is no more to send to the card than those few trees. They stand unevenly, about as far apart as the wood
 * has room for, and their crowns run into one another as a wood's do. Where each stands, which tree it is and how
 * it is turned are the same every time.
 *
 * <p>A tree's trunk is in the avatar's way, as a post; its boughs are not.
 */
public record Grove(Vec3 middle, double across, double deep, int trees) {

    /** How many different trees there are in it: of each kind, so many. */
    private static final int EACH = 2;
    private static final int KINDS = EACH * 3;
    /** How far a tree may stand from its place in the rows, of the distance between rows. */
    private static final double ASTRAY = 0.36;
    private static final double SMALLEST = 0.78;
    private static final double BIGGEST = 1.12;
    private static final double FULL = 360;
    private static final long CHANCE = 20261123L;

    public Grove {
        if (!(across > 0 && deep > 0 && trees > 0)) {
            throw new IllegalArgumentException("a wood has some ground and some trees: " + across + " by " + deep
                    + ", " + trees + " trees");
        }
    }

    /** One of the wood's different trees: its kind, and the tree itself. */
    public record Grown(Kind kind, Tree tree) {
    }

    /** One tree standing in the wood: where, which of the wood's trees it is, how far turned and how big. */
    public record Standing(Vec3 stands, int kind, double turned, double times) {

        /** What takes the tree, in its own frame, to where it stands: made as big, turned, and set down. */
        public Mat4 placed() {
            return new Mat4(stands).times(new Turned(new Vec3(0, 1, 0), turned).matrix())
                    .times(new Sized(times).matrix());
        }
    }

    /** The different trees of the wood: so many of each kind, each grown from a number of its own. */
    public List<Grown> kinds() {
        final List<Grown> kinds = new ArrayList<>();
        for (int kind = 0; kind < KINDS; kind++) {
            final Kind of = List.of(Kind.OAK, Kind.WALNUT, Kind.BIRCH).get(kind / EACH);
            kinds.add(new Grown(of, of.tree(kind % EACH + 1)));
        }
        return kinds;
    }

    /** Every tree standing in it. */
    public List<Standing> standing() {
        final Random chance = new Random(CHANCE);
        // rows about as far apart each way as gives every tree its share of the ground
        final double apart = Math.sqrt(across * deep / trees);
        final int along = (int) Math.ceil(across / apart);
        final int rows = (int) Math.ceil((double) trees / along);
        final List<Standing> standing = new ArrayList<>();
        for (int tree = 0; tree < trees; tree++) {
            final double x = (tree % along + 0.5) * across / along - across / 2
                    + (chance.nextDouble() * 2 - 1) * ASTRAY * apart;
            final double z = (tree / along + 0.5) * deep / rows - deep / 2
                    + (chance.nextDouble() * 2 - 1) * ASTRAY * apart;
            standing.add(new Standing(middle.plus(new Vec3(x, 0, z)), chance.nextInt(KINDS),
                    chance.nextDouble() * FULL, SMALLEST + chance.nextDouble() * (BIGGEST - SMALLEST)));
        }
        return standing;
    }

    /** What is in the way: every tree's trunk, as a post as thick and as high as the trunk is. */
    public List<Block> inTheWay() {
        // each kind of tree is grown once for its trunk, not once for every tree of the kind
        final List<Bough> trunks = kinds().stream().map(kind -> kind.tree().boughs().getFirst()).toList();
        final List<Block> posts = new ArrayList<>();
        for (final Standing tree : standing()) {
            final Bough trunk = trunks.get(tree.kind());
            final double thick = trunk.thick().getFirst() * tree.times();
            posts.add(new Block(tree.stands().x(), tree.stands().z(), thick, thick, 0, 0,
                    trunk.ends().y() * tree.times()));
        }
        return posts;
    }
}
