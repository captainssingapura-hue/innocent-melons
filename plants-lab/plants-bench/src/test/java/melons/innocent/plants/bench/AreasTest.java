package melons.innocent.plants.bench;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.walk.Baked;
import melons.innocent.plants.walk.WoodDrawn;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Areas of forest with outlines of their own, with no card. Each outline holds what its shape does: a star its
 * points and not the hollows between them, a crescent its back and not its hollow. An area's trees all stand within
 * its outline, the same every time, and a tree stands the same under any outline that holds its spot. An area as one
 * mesh is made at its coarsest level unless a stage is held; merged with no card, its wood and its hazes come to a
 * few hundred triangles a tree; and too fine a level for its size is refused.
 */
class AreasTest {

    @Test
    void eachOutlineHoldsWhatItsShapeDoes() {
        assertTrue(Outline.CIRCLE.holds(0.7, 0.7) && !Outline.CIRCLE.holds(0.8, 0.8));
        assertTrue(Outline.SQUARE.holds(0.99, -0.99));
        assertTrue(Outline.OVAL.holds(0.9, 0) && !Outline.OVAL.holds(0, 0.9));
        assertTrue(Outline.STAR.holds(0, 0.95), "a point, straight along");
        final double hollow = Math.PI / 5;
        assertTrue(!Outline.STAR.holds(0.7 * Math.sin(hollow), 0.7 * Math.cos(hollow)), "the hollow beside it");
        assertTrue(Outline.STAR.holds(0.4 * Math.sin(hollow), 0.4 * Math.cos(hollow)), "within its inner corner");
        assertTrue(Outline.CRESCENT.holds(-0.8, 0) && !Outline.CRESCENT.holds(0.3, 0), "its back, not its hollow");
    }

    @Test
    void anAreasTreesStandWithinItsOutlineTheSameEveryTime() {
        final Vec3 middle = new Vec3(0, 0, 0);
        final List<WoodDrawn.Planted> square = new Area(Outline.SQUARE, 10_000).planted(Kinds.MIXED, middle,
                Optional.empty());
        assertEquals(14 * 14, square.size(), "a hundred metres at seven apart");
        for (final Outline outline : Outline.values()) {
            final List<WoodDrawn.Planted> trees = new Area(outline, 10_000).planted(Kinds.MIXED, middle,
                    Optional.empty());
            assertTrue(!trees.isEmpty() && trees.size() <= square.size(), outline + ": " + trees.size());
            for (final WoodDrawn.Planted tree : trees) {
                assertTrue(outline.holds(tree.placed().at(0, 3) / 5_000, tree.placed().at(2, 3) / 5_000),
                        outline + " holds each of its trees");
                assertTrue(square.stream().anyMatch(same -> same.grown() == tree.grown()
                        && same.placed().at(0, 3) == tree.placed().at(0, 3)
                        && same.placed().at(2, 3) == tree.placed().at(2, 3)), "the same tree as under a square");
            }
        }
        final int round = new Area(Outline.CIRCLE, 10_000).planted(Kinds.OAK, middle, Optional.empty()).size();
        assertEquals(Math.PI / 4 * square.size(), round, 12, "a circle's share of the square");
    }

    @Test
    void anAreaAsOneMeshIsMadeAtItsCoarsestAndTooFineALevelIsRefused() {
        final OnShow area = new OnShow(Subject.AREA, Kinds.MIXED, Optional.empty(), true, 4, Joined.AS_ONE,
                Outline.STAR, 100);
        assertEquals(Stage.WHOLE, area.level());
        assertTrue(area.planted().stream().allMatch(tree -> tree.held().isEmpty() && tree.placed().at(0, 3) < 0),
                "beside it, tree by tree at whatever stage its distance calls for, on the left");
        final Baked baked = new Baked(Kinds.OAK.grown(), List.of(0.01, 0.02, 0.04, 0.08, 0.16, 0.32, 0.64, 0.9));
        final Baked.Whole whole = baked.whole(area.merged(), area.level());
        final long triangles = whole.wood().values().stream().mapToLong(mesh -> mesh.triangles()).sum()
                + whole.hazes().stream().mapToLong(haze -> haze.mesh().triangles()).sum();
        assertTrue(triangles > 100L * area.merged().size() && triangles < 600L * area.merged().size(),
                "a few hundred triangles a tree: " + triangles + " for " + area.merged().size());
        assertTrue(!whole.hazes().isEmpty(), "its crowns' haze with it");
        final OnShow fine = new OnShow(Subject.AREA, Kinds.OAK, Optional.of(Stage.FINE), false, 4, Joined.AS_ONE,
                Outline.SQUARE, 200);
        assertThrows(IllegalArgumentException.class, () -> baked.whole(fine.merged(), fine.level()));
        assertThrows(IllegalArgumentException.class, () -> new OnShow(Subject.AREA, Kinds.OAK, Optional.empty(),
                false, 4, Joined.APART, Outline.CIRCLE, 5));
    }
}
