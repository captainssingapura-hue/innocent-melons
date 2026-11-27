package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.baked.Recipe;
import com.example.fxsuite.baked.Resolution;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotDesign;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.life.body.fat.FatPattern;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.skin.Skins;
import com.example.fxsuite.life.body.wear.Armour;
import com.example.fxsuite.life.body.wear.Plate;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * How a shelled robot is made: the body shop's male model's skin, lean, 188 cm ({@link Proportions#MODEL}), and its
 * shell of plates shaped over it ({@link Shell}), its forearms' over the skin as its forearms are held; within, its
 * frame ({@link RobotParts}), its helmet in the shell's colour, its chest's cage left out, which is broader than the
 * chest and would stand through its plate. Fine only, and dear: a minute and a half, so kept once made.
 */
public final class ShelledRobotRecipe implements Recipe {

    /** How finely its bones are made, cm. */
    private static final double BONES = 0.4;

    private final RobotPieces pieces;

    /** Its pieces kept and read as {@code pieces} has them. */
    public ShelledRobotRecipe(final RobotPieces pieces) {
        this.pieces = pieces;
    }

    @Override
    public Taxonomy tree() {
        return Figures.TREE;
    }

    @Override
    public Leaf<?> leaf() {
        return new Figures.ShelledRobot();
    }

    @Override
    public List<Resolution> offered() {
        return List.of(Resolution.FINE);
    }

    @Override
    public boolean dear(final Resolution resolution) {
        return true;
    }

    @Override
    public String madeBy() {
        return pieces.madeBy(leaf().title());
    }

    @Override
    public List<Piece> made(final Resolution resolution, final Consumer<String> told) {
        final Proportions p = Proportions.MODEL;
        final Rig bound = new Rig(p, BoundPose.INSTANCE.pose(), false);
        told.accept("the skin the shelled robot's shell is shaped over");
        final Skins skins = new Skins(FatPattern.LEAN);
        final Flesh skin = skins.of(p);
        told.accept("the shelled robot's shell");
        final List<Plate> plates = Shell.INSTANCE.of(skins.beneath(skin),
                skins.beneath(skin, new Rig(p, Armour.INSTANCE.held(), false)), bound);
        final List<RobotParts.Piece> shelled = new ArrayList<>(RobotParts.INSTANCE.of(bound, RobotDesign.DEFAULT,
                Paint.SHELL, new Detail(BONES)).stream().filter(piece -> piece.part() != Part.CHEST).toList());
        plates.forEach(plate -> shelled.add(new RobotParts.Piece(plate.part(), Paint.SHELL, plate.mesh())));
        return pieces.kept(shelled);
    }
}
