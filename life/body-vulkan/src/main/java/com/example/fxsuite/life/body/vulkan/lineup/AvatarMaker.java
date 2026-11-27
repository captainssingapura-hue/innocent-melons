package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotDesign;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.life.body.fat.FatPattern;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.robot.Shell;
import com.example.fxsuite.life.body.skin.Skins;
import com.example.fxsuite.life.body.vulkan.Sheet;
import com.example.fxsuite.life.body.wear.Armour;
import com.example.fxsuite.life.body.wear.Plate;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The line-up's first batch, made away from the screen: two robots built as the body shop's male model is, 188 cm
 * ({@link Proportions#MODEL}), its design the default ({@link RobotDesign#DEFAULT}). The framed robot, its frame bare
 * ({@link RobotParts}): made in a moment. The shelled robot, its frame within a shell of plates shaped over the
 * model's skin ({@link Shell}), its helmet in the shell's colour, its chest's cage left out, which is broader than the
 * chest and would stand through its plate: made in a minute or two, the skin first. Each painted
 * as the body shop paints a robot ({@link Paint}): its frame steel, its housings gunmetal, its shell, its panels and
 * its rings a lacquer of their colours, its visor and its core lit glass.
 */
public final class AvatarMaker {

    /** How finely its bones are made, cm. */
    private static final double BONES = 0.4;

    /** The kinds of figure it makes, in the order it makes them. */
    public List<Leaf<?>> batch() {
        return List.of(new Figures.FramedRobot(), new Figures.ShelledRobot());
    }

    /** Each of the batch made in turn and given to {@code made} as it is, each step told to {@code told}. */
    public void made(final Consumer<String> told, final Consumer<Avatar> made) {
        final Proportions p = Proportions.MODEL;
        final Rig bound = new Rig(p, BoundPose.INSTANCE.pose(), false);
        final Detail detail = new Detail(BONES);
        told.accept("the framed robot");
        made.accept(new Avatar(new Figures.FramedRobot(), bound,
                pieces(RobotParts.INSTANCE.of(bound, RobotDesign.DEFAULT, Paint.FRAME, detail))));
        told.accept("the skin the shelled robot's shell is shaped over");
        final Skins skins = new Skins(FatPattern.LEAN);
        final Flesh skin = skins.of(p);
        told.accept("the shelled robot's shell");
        final List<Plate> plates = Shell.INSTANCE.of(skins.beneath(skin),
                skins.beneath(skin, new Rig(p, Armour.INSTANCE.held(), false)), bound);
        final List<RobotParts.Piece> shelled = new ArrayList<>(RobotParts.INSTANCE.of(bound, RobotDesign.DEFAULT,
                Paint.SHELL, detail).stream().filter(piece -> piece.part() != Part.CHEST).toList());
        plates.forEach(plate -> shelled.add(new RobotParts.Piece(plate.part(), Paint.SHELL, plate.mesh())));
        made.accept(new Avatar(new Figures.ShelledRobot(), bound, pieces(shelled)));
    }

    /** {@code made}, each piece as the card draws it, in what its paint is drawn in; any with no triangle left out. */
    List<Avatar.Piece> pieces(final List<RobotParts.Piece> made) {
        final List<Avatar.Piece> pieces = new ArrayList<>();
        for (final RobotParts.Piece p : made) {
            final Mesher.Mesh m = p.mesh();
            final var mesh = new Sheet(m.points(), m.triangles()).mesh();
            if (!mesh.corners().isEmpty()) {
                pieces.add(new Avatar.Piece(p.part(), material(p.paint()), colour(p.paint()), mesh));
            }
        }
        return pieces;
    }

    /**
     * What {@code paint} is drawn in: lit glass if it glows; a bare metal, gunmetal for a housing and steel for the
     * rest, if it is a metal; a lacquer of its colour else.
     */
    Material material(final Paint paint) {
        return switch (paint.finish()) {
            case GLOW -> new Materials.LitGlass();
            case METAL -> paint == Paint.HOUSING ? new Materials.Gunmetal() : new Materials.Steel();
            case GLOSS, MATTE -> new Materials.GlossPaint();
        };
    }

    /** {@code paint}'s colour, as much red, green and blue as it has. */
    Vec3 colour(final Paint paint) {
        final int rgb = Integer.parseInt(paint.colour().substring(1), 16);
        return new Vec3((rgb >> 16 & 0xff) / 255.0, (rgb >> 8 & 0xff) / 255.0, (rgb & 0xff) / 255.0);
    }
}
