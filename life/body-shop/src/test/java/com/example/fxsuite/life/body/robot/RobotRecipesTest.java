package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Baker;
import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.baked.Resolution;
import com.example.fxsuite.baked.TreeShelf;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.taxonomy.Leaf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The robots' recipes: one for every robot of the figures' tree, the shelled one dear and so kept, the framed one
 * cheap and made each time, never kept. A framed robot coarse has fewer triangles than fine; its pieces kept, read
 * back as they were made, each its part, its paint and its surface.
 */
class RobotRecipesTest {

    private final RobotRecipes recipes = new RobotRecipes();

    @TempDir
    private Path root;

    @Test
    void everyRobotHasARecipeTheShelledOneDearTheFramedOneCheapAndNeverKept() {
        for (final Leaf<?> robot : Figures.TREE.under(new Figures.Robot())) {
            assertEquals(robot, recipes.of(robot).leaf());
        }
        assertTrue(recipes.of(new Figures.ShelledRobot()).dear(Resolution.FINE));
        new Baker(new TreeShelf(root)).pieces(recipes.of(new Figures.FramedRobot()), Resolution.COARSE, s -> { });
        assertFalse(Files.exists(root.resolve("figures")), "nothing kept of a framed robot");
    }

    @Test
    void aFramedRobotCoarseHasFewerTrianglesAndItsPiecesAreReadBackAsMade() {
        final FramedRobotRecipe framed = new FramedRobotRecipe(recipes.pieces());
        final List<Piece> coarse = framed.made(Resolution.COARSE, s -> { });
        final List<Piece> fine = framed.made(Resolution.FINE, s -> { });
        assertTrue(triangles(coarse) < triangles(fine), triangles(coarse) + " coarse, " + triangles(fine) + " fine");
        final List<RobotParts.Piece> made = recipes.pieces().made(coarse);
        final List<Piece> again = recipes.pieces().kept(made);
        assertEquals(coarse.size(), made.size());
        for (int i = 0; i < coarse.size(); i++) {
            assertEquals(coarse.get(i).labels(), List.of(made.get(i).part().name(), made.get(i).paint().name()));
            assertArrayEquals(coarse.get(i).points(), again.get(i).points());
            assertArrayEquals(coarse.get(i).triangles(), again.get(i).triangles());
        }
    }

    /** How many triangles {@code pieces} have between them. */
    private long triangles(final List<Piece> pieces) {
        return pieces.stream().mapToLong(p -> p.triangles().length / 3).sum();
    }
}
