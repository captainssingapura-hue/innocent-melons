package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.baked.Baker;
import com.example.fxsuite.baked.Recipe;
import com.example.fxsuite.baked.Resolution;
import com.example.fxsuite.baked.TreeShelf;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.life.body.robot.RobotRecipes;
import com.example.fxsuite.life.body.vulkan.Sheet;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The line-up's first batch, made away from the screen as the robots' recipes say ({@link RobotRecipes}), fine: two
 * robots built as the body shop's male model is, 188 cm ({@link Proportions#MODEL}). The framed robot, its frame bare:
 * made in a moment. The shelled robot, its frame within a shell of plates shaped over the model's skin: dear to make,
 * a minute and a half, so taken from the shelf if it is baked there, made by the body shop as it is now
 * ({@link Baker}), else made and baked. Each piece of what it is of, its shell, its frame or another ({@link Paint}),
 * drawn as each figure's paintwork has that ({@link Paintwork}).
 */
public final class AvatarMaker {

    private final RobotRecipes recipes = new RobotRecipes();
    private final Baker baker;

    /** A maker looking for what is dear on the user's shelf, or the one {@code -Dbaked.dir} says. */
    public AvatarMaker() {
        this(new Baker(new TreeShelf()));
    }

    /** A maker looking for what is dear as {@code baker} does. */
    public AvatarMaker(final Baker baker) {
        this.baker = baker;
    }

    /** The kinds of figure it makes, in the order it makes them. */
    public List<Leaf<?>> batch() {
        return recipes.all().stream().<Leaf<?>>map(Recipe::leaf).toList();
    }

    /** Each of the batch made or taken as baked, in turn, and given to {@code made} as it is, each step told. */
    public void made(final Consumer<String> told, final Consumer<Avatar> made) {
        final Rig bound = new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false);
        for (final Recipe r : recipes.all()) {
            made.accept(new Avatar(r.leaf(), bound,
                    pieces(recipes.pieces().made(baker.pieces(r, Resolution.FINE, told)))));
        }
    }

    /** {@code made}, each piece as the card draws it; any with no triangle left out. */
    List<Avatar.Piece> pieces(final List<RobotParts.Piece> made) {
        final List<Avatar.Piece> pieces = new ArrayList<>();
        for (final RobotParts.Piece p : made) {
            final Mesher.Mesh m = p.mesh();
            final var mesh = new Sheet(m.points(), m.triangles()).mesh();
            if (!mesh.corners().isEmpty()) {
                pieces.add(new Avatar.Piece(p.part(), p.paint(), mesh));
            }
        }
        return pieces;
    }

    /**
     * What {@code kind}'s pieces are of, each drawn in a coat of its own ({@link Paintwork}), in the order they are
     * offered: a shelled robot's shell first; every robot's panels, visor, frame, housings and rods.
     */
    public List<Paint> roles(final Leaf<?> kind) {
        final List<Paint> robot = List.of(Paint.PANEL, Paint.VISOR, Paint.FRAME, Paint.HOUSING, Paint.ROD);
        if (kind instanceof Figures.ShelledRobot) {
            final List<Paint> shelled = new ArrayList<>(List.of(Paint.SHELL));
            shelled.addAll(robot);
            return shelled;
        }
        return robot;
    }
}
