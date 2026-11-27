package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Baker;
import com.example.fxsuite.baked.Recipe;
import com.example.fxsuite.baked.TreeShelf;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.List;
import java.util.Locale;

/**
 * The robots' recipes, one to each robot the figures' tree has: the framed one, the shelled one. And, run, every robot
 * dear to make baked ahead, on the shelf {@code -Dbaked.dir} says, else the user's, so that what shows one first finds
 * it made.
 */
public final class RobotRecipes {

    private static final double BILLION = 1e9;

    private final RobotPieces pieces = new RobotPieces();
    private final List<Recipe> all = List.of(new FramedRobotRecipe(pieces), new ShelledRobotRecipe(pieces));

    /** Every robot's recipe. */
    public List<Recipe> all() {
        return all;
    }

    /** The recipe for {@code kind}. */
    public Recipe of(final Leaf<?> kind) {
        return all.stream().filter(r -> r.leaf().equals(kind)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no recipe for " + kind.title()));
    }

    /** How a robot's pieces are kept and read. */
    public RobotPieces pieces() {
        return pieces;
    }

    /** Every robot dear to make baked ahead on the user's shelf, if not baked already, each step told. */
    public void bake() {
        final Baker baker = new Baker(new TreeShelf());
        for (final Recipe r : all) {
            final long began = System.nanoTime();
            final int made = baker.baked(r, step -> System.out.println("  " + step));
            System.out.printf(Locale.ROOT, "%s: %s%n", r.leaf().title(), made == 0 ? "nothing to bake"
                    : String.format(Locale.ROOT, "%d baked in %.1f s", made, (System.nanoTime() - began) / BILLION));
        }
    }
}
