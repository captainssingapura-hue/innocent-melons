package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The kinds of tree, each grown as its kind is known to grow. The oak is the oak it was. The walnut is as broad as
 * the oak for how high it is, or broader, and ends in twigs far stouter than the oak's. The birch stands tall and
 * narrow on a slender trunk, one leader running on up through it, and many of its twigs hang. And the wood of any
 * of them says, at each point, how far round and how far along its bough the point is, and how thick the wood is
 * there, so that a bark's marks can follow the wood and change as it thins: the trunk's way along it beginning from
 * nothing, and the next bough's from a stretch of its own.
 */
class KindTest {

    @Test
    void theOakIsTheOakItWas() {
        assertEquals(new Tree().boughs(), Kind.OAK.tree(1).boughs());
    }

    @Test
    void theWalnutIsBroadAndBluntAndTheBirchTallAndNarrowWithHangingTwigs() {
        for (final Kind kind : Kind.values()) {
            final Tree tree = kind.tree(1);
            final List<Bough> boughs = tree.boughs();
            final long twigs = boughs.stream().filter(bough -> bough.thick().getLast() < kind.habit().twig()).count();
            final long hanging = boughs.stream().filter(bough -> bough.thick().getLast() < kind.habit().twig())
                    .filter(bough -> bough.ends().y() < bough.begins().y()).count();
            System.out.printf("KIND %s: %.1f m high, %.1f m reach, %d boughs, %d twigs, %d hanging, %d triangles%n",
                    kind.said(), tree.height() / 100, tree.reach() / 100, boughs.size(), twigs, hanging,
                    new Wooded(tree).formed().triangles());
        }
        final Tree oak = Kind.OAK.tree(1);
        final Tree walnut = Kind.WALNUT.tree(1);
        final Tree birch = Kind.BIRCH.tree(1);
        assertTrue(walnut.reach() / walnut.height() >= oak.reach() / oak.height() * 0.95, "the walnut is broad");
        assertTrue(Kind.WALNUT.habit().twig() > 2 * Kind.OAK.habit().twig(), "its twigs are stout");
        assertTrue(birch.height() > 2.2 * birch.reach(), "the birch is tall and narrow");
        assertTrue(birch.height() > oak.height(), "and taller than the oak");
        final List<Bough> twigs = birch.boughs().stream()
                .filter(bough -> bough.thick().getLast() < Kind.BIRCH.habit().twig()).toList();
        final long hanging = twigs.stream().filter(bough -> bough.ends().y() < bough.begins().y()).count();
        assertTrue(hanging > twigs.size() / 3, "many of its twigs hang: " + hanging + " of " + twigs.size());
    }

    @Test
    void theBirchsTrunkRunsOnUpAsALeaderThroughItsCrown() {
        final List<Bough> boughs = Kind.BIRCH.tree(1).boughs();
        // from the trunk, follow the thicker of each fork: it runs on nearly straight up for many forks
        int at = 0;
        int forks = 0;
        while (true) {
            final int from = at;
            final int next = IntStream.range(0, boughs.size()).filter(bough -> boughs.get(bough).from() == from)
                    .boxed().max((one, other) -> Double.compare(boughs.get(one).thick().getFirst(),
                            boughs.get(other).thick().getFirst())).orElse(-1);
            if (next < 0) {
                break;
            }
            final Vec3 way = boughs.get(next).ends().minus(boughs.get(next).begins()).unit();
            if (way.y() < 0.8) {
                break;
            }
            at = next;
            forks++;
        }
        assertTrue(forks >= 5, "the leader runs on up through " + forks + " forks");
    }

    @Test
    void theWoodSaysOfEachPointHowFarRoundAndAlongItsBoughItIsAndHowThick() {
        final Tree birch = Kind.BIRCH.tree(1);
        final Mesh trunk = new Wooded(List.of(birch.boughs().getFirst())).formed();
        double furthest = 0;
        double widest = 0;
        final List<Double> thick = birch.boughs().getFirst().thick();
        final double trunkThinnest = thick.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
        final double trunkThickest = thick.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
        for (int point = 0; point < trunk.points().size(); point++) {
            final Vec3 on = trunk.ons().get(point);
            assertTrue(on.z() >= trunkThinnest - 1e-9 && on.z() <= trunkThickest + 1e-9, "as thick as it is: " + on);
            furthest = Math.max(furthest, on.y());
            widest = Math.max(widest, on.x());
        }
        assertEquals(birch.boughs().getFirst().length(), furthest, 1e-6, "along it, as far as it runs");
        assertEquals(2 * Math.PI * birch.boughs().getFirst().thick().getFirst(), widest, 1e-6,
                "round it, the whole way round its bark where it is thickest");
        final Mesh two = new Wooded(birch.boughs().subList(0, 2)).formed();
        assertTrue(two.ons().subList(trunk.ons().size(), two.ons().size()).stream().allMatch(on -> on.y() > 100),
                "the second bough set along a stretch of its own");
    }
}
