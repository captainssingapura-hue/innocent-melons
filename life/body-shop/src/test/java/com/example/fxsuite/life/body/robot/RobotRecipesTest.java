package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.baked.Resolution;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.RobotDesign;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.taxonomy.Leaf;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The robots' recipes: one for every robot of the figures' tree, each dear at every resolution it is made at, its
 * boots wanting the model's skin made first. A robot's pieces kept, read back as they were made, each its part, its
 * paint and its surface.
 */
class RobotRecipesTest {

    private final RobotRecipes recipes = new RobotRecipes();

    @Test
    void everyRobotHasARecipeDearAtEveryResolution() {
        for (final Leaf<?> robot : Figures.TREE.under(new Figures.Robot())) {
            assertEquals(robot, recipes.of(robot).leaf());
            for (final Resolution r : recipes.of(robot).offered()) {
                assertTrue(recipes.of(robot).dear(r), robot.title() + ", " + r.named());
            }
        }
    }

    @Test
    void aRobotsPiecesAreReadBackAsMade() {
        final RobotPieces pieces = recipes.pieces();
        final List<RobotParts.Piece> made = RobotParts.INSTANCE.of(pieces.bound(), RobotDesign.DEFAULT, Paint.FRAME,
                new Detail(1.0));
        final List<Piece> kept = pieces.kept(made);
        final List<RobotParts.Piece> again = pieces.made(kept);
        assertEquals(made.size(), again.size());
        for (int i = 0; i < made.size(); i++) {
            assertEquals(made.get(i).part(), again.get(i).part());
            assertEquals(made.get(i).paint(), again.get(i).paint());
            assertArrayEquals(made.get(i).mesh().points(), again.get(i).mesh().points());
            assertArrayEquals(made.get(i).mesh().triangles(), again.get(i).mesh().triangles());
        }
    }
}
