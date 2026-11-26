package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A tree's coarser stages. Each stage begins where the thinnest bough it drops is a pixel across: for the game's view
 * on a picture 1080 pixels high, at 12, 49, 194 and 388 metres; and a tree gone coarser comes back finer only a tenth
 * nearer. Every bough is kept at a stage or held by one of its clumps, each clump hides something and not all, no
 * stage keeps more wood than the one before, and from the boughs' stage on each has fewer triangles in all (a kind
 * whose twigs are thick, as the walnut's, drops little at the first). A cloud of points lies the way it is spread.
 */
class StageTest {

    private final Staging staging = new Staging(48, 1080);
    private final List<Double> levels = List.of(0.01, 0.02, 0.04, 0.08, 0.16, 0.32, 0.64, 0.9);

    @Test
    void eachStageBeginsWhereTheThinnestBoughItDropsIsAPixelAcross() {
        final double[] begins = {1210, 4850, 19400, 38800};
        for (int stage = 1; stage < Stage.values().length; stage++) {
            final double at = begins[stage - 1];
            assertEquals(Stage.values()[stage], staging.coarsest(at * 1.01, 1), "just beyond " + at + " cm");
            assertEquals(Stage.values()[stage - 1], staging.coarsest(at * 0.99, 1), "just within " + at + " cm");
        }
        assertEquals(Stage.TWIGS_GONE, staging.coarsest(1210 * 1.5 * 1.01, 1.5), "a bigger tree's twigs are thicker");
        assertEquals(Stage.FINE, staging.coarsest(1210 * 1.5 * 0.99, 1.5));
    }

    @Test
    void aTreeGoneCoarserComesBackFinerOnlyATenthNearer() {
        assertEquals(Stage.BOUGHS, staging.of(7, 5000, 1));
        assertEquals(Stage.BOUGHS, staging.of(7, 4700, 1), "a little nearer, it stays");
        assertEquals(Stage.TWIGS_GONE, staging.of(7, 4300, 1), "a tenth nearer, it comes back");
        assertEquals(Stage.FINE, staging.of(8, 1000, 1), "another tree has its own");
    }

    @Test
    void everyBoughIsKeptOrHeldAndEachClumpHidesSomethingAndNotAll() {
        for (final Kind kind : Kind.values()) {
            final Tree tree = kind.tree(1);
            for (final Stage stage : Stage.values()) {
                final Pruned pruned = new Pruned(tree, stage);
                assertEquals(tree.boughs().size(), pruned.kept().size() + pruned.dropped().size());
                final List<Clump> clumps = pruned.clumps();
                assertEquals(stage == Stage.FINE, clumps.isEmpty(), kind + " " + stage);
                for (final Bough bough : pruned.dropped()) {
                    for (final Vec3 point : bough.points()) {
                        assertTrue(clumps.stream().anyMatch(clump -> clump.holds(point)),
                                kind + " " + stage + ": a dropped point outside every clump");
                    }
                }
                clumps.forEach(clump -> assertTrue(clump.hides() > 0 && clump.hides() < 1, "hides " + clump.hides()));
                for (final Bough bough : pruned.kept()) {
                    assertTrue(bough.forks() == 0 || bough.thick().getFirst() >= stage.least());
                }
            }
        }
    }

    @Test
    void eachStageOfEveryKindHasFewerTrianglesThanTheOneBefore() {
        for (final Kind kind : Kind.values()) {
            final Tree tree = kind.tree(1);
            final List<String> said = new ArrayList<>();
            int woodBefore = Integer.MAX_VALUE;
            int before = Integer.MAX_VALUE;
            for (final Stage stage : Stage.values()) {
                final Pruned pruned = new Pruned(tree, stage);
                final int wood = pruned.wood().triangles();
                final int haze = pruned.hazes(levels).values().stream().mapToInt(mesh -> mesh.triangles()).sum();
                assertTrue(wood <= woodBefore, kind + " " + stage + ": kept wood " + wood + " after " + woodBefore);
                // a kind whose twigs are thick drops little of them at the first stage, and gains its clumps
                assertTrue(stage.ordinal() < Stage.BOUGHS.ordinal() || wood + haze < before,
                        kind + " " + stage + ": " + (wood + haze) + " after " + before);
                woodBefore = wood;
                before = wood + haze;
                said.add(String.format(Locale.ROOT, "%s %,d + %d clumps %,d", stage, wood, pruned.clumps().size(),
                        haze));
            }
            System.out.println(kind + ": " + String.join("; ", said));
        }
    }

    @Test
    void aCloudOfPointsLiesTheWayItIsSpread() {
        final Vec3 along = new Vec3(1, 2, 0.5).unit();
        final List<Vec3> points = new ArrayList<>();
        for (int i = -10; i <= 10; i++) {
            points.add(along.times(10 * i).plus(new Vec3(0, 0, i % 2 == 0 ? 1 : -1)));
        }
        final List<Vec3> ways = new Lying(points).ways();
        assertEquals(1, Math.abs(ways.get(0).dot(along)), 1e-3, "the way it reaches");
        assertEquals(0, ways.get(0).dot(ways.get(1)), 1e-9);
        assertEquals(0, ways.get(1).dot(ways.get(2)), 1e-9);
    }
}
