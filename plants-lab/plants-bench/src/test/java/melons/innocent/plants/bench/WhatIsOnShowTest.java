package melons.innocent.plants.bench;

import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.worked.Picture;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.walk.WoodDrawn;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What the bench has on show, with no card. One tree stands alone at the middle; beside the same as grown, the one
 * as grown stands on the left held as grown, and the one on show on the right held as asked. A patch is sixteen trees
 * on its square, of the kinds asked for and no others, the same every time. The run's options are read by name, and
 * a name the bench has not is refused. And how dark a picture comes out is one less its brightness, each half apart.
 */
class WhatIsOnShowTest {

    @Test
    void oneTreeStandsAloneOrOnTheRightOfTheSameAsGrown() {
        final OnShow alone = new OnShow(Subject.TREE, Kinds.WALNUT, Optional.of(Stage.LIMBS), false);
        assertEquals(1, alone.planted().size());
        assertEquals(0, alone.planted().getFirst().placed().at(0, 3), 1e-9, "at the middle");
        assertEquals(Optional.of(Stage.LIMBS), alone.planted().getFirst().held());
        final List<WoodDrawn.Planted> beside = new OnShow(Subject.TREE, Kinds.WALNUT, Optional.of(Stage.LIMBS),
                true).planted();
        assertEquals(2, beside.size());
        assertTrue(beside.get(0).placed().at(0, 3) < 0 && beside.get(1).placed().at(0, 3) > 0, "left, then right");
        assertEquals(Optional.of(Stage.FINE), beside.get(0).held(), "the one on the left as grown");
        assertEquals(Optional.of(Stage.LIMBS), beside.get(1).held());
        assertEquals(beside.get(0).grown(), beside.get(1).grown(), "the same tree");
        assertEquals(Optional.empty(), new OnShow(Subject.TREE, Kinds.OAK, Optional.empty(), false).planted()
                .getFirst().held(), "none held, whatever its distance calls for");
    }

    @Test
    void aPatchIsSixteenTreesOfTheKindsAskedTheSameEveryTime() {
        final List<WoodDrawn.Grown> grown = Kinds.OAK.grown();
        final List<WoodDrawn.Planted> birches = Subject.PATCH.planted(Kinds.BIRCH,
                new Vec3(0, 0, 0), Optional.empty(), 4);
        assertEquals(16, birches.size());
        assertTrue(birches.stream().allMatch(tree -> grown.get(tree.grown()).kind() == Kind.BIRCH), "birches only");
        assertTrue(birches.stream().allMatch(tree -> Math.abs(tree.placed().at(0, 3)) < 1_250
                && Math.abs(tree.placed().at(2, 3)) < 1_250), "on its square");
        final Set<Kind> mixed = Subject.PATCH.planted(Kinds.MIXED, new Vec3(0, 0, 0),
                Optional.empty(), 4).stream().map(tree -> grown.get(tree.grown()).kind()).collect(Collectors.toSet());
        assertEquals(Set.of(Kind.OAK, Kind.WALNUT, Kind.BIRCH), mixed, "all three, mixed");
        assertEquals(where(Subject.PATCH.planted(Kinds.MIXED, new Vec3(0, 0, 0), Optional.empty(), 4)),
                where(Subject.PATCH.planted(Kinds.MIXED, new Vec3(0, 0, 0), Optional.empty(), 4)),
                "the same every time");
    }

    @Test
    void aPatchHasAsManyTreesAsAskedAndAsOneMeshOnlyTheSameAsGrownIsPlantedAsCopies() {
        for (int rows = 2; rows <= 8; rows++) {
            assertEquals(rows * rows, new OnShow(Subject.PATCH, Kinds.OAK, Optional.empty(), false, rows, Joined.APART)
                    .planted().size());
        }
        assertThrows(IllegalArgumentException.class,
                () -> new OnShow(Subject.PATCH, Kinds.OAK, Optional.empty(), false, 9, Joined.APART));
        final OnShow one = new OnShow(Subject.PATCH, Kinds.MIXED, Optional.of(Stage.LIMBS), true, 3, Joined.AS_ONE);
        assertEquals(9, one.planted().size(), "only the same as grown, on the left, as copies");
        assertTrue(one.planted().stream().allMatch(tree -> tree.placed().at(0, 3) < 0));
        assertEquals(9, one.merged().size(), "the one mesh made of nine");
        assertTrue(one.merged().stream().allMatch(tree -> tree.held().equals(Optional.of(Stage.LIMBS))),
                "at the stage held");
        assertTrue(one.where().x() > 0, "standing on the right");
    }

    @Test
    void theRunsOptionsAreReadByNameAndANameTheBenchHasNotIsRefused() {
        System.setProperty("bench.subject", "patch");
        System.setProperty("bench.kinds", "birch");
        System.setProperty("bench.stage", "twigs gone");
        System.setProperty("bench.beside", "false");
        try {
            assertEquals(new OnShow(Subject.PATCH, Kinds.BIRCH, Optional.of(Stage.TWIGS_GONE), false),
                    new Told().first());
            System.setProperty("bench.kinds", "pine");
            assertThrows(IllegalArgumentException.class, () -> new Told().first());
        } finally {
            Arrays.asList("bench.subject", "bench.kinds", "bench.stage", "bench.beside")
                    .forEach(System::clearProperty);
        }
        assertEquals(new OnShow(Subject.TREE, Kinds.OAK, Optional.empty(), true), new Told().first(), "unless said");
    }

    /** Where each tree stands and which it is, in a line each. */
    private List<String> where(final List<WoodDrawn.Planted> planted) {
        return planted.stream().map(tree -> tree.grown() + " at " + tree.placed().at(0, 3) + ", "
                + tree.placed().at(2, 3) + " times " + tree.times()).toList();
    }

    @Test
    void howDarkAPictureComesOutIsOneLessItsBrightnessEachHalfApart() {
        final int[] pixels = new int[4 * 2];
        Arrays.fill(pixels, 0xffffffff);
        pixels[0] = 0xff000000;
        pixels[4] = 0xff000000;
        final Darkness darkness = new Darkness(new Picture(4, pixels));
        assertEquals(0.5, darkness.left(), 1e-9, "one column of two black");
        assertEquals(0, darkness.right(), 1e-9, "white all over");
    }
}
