package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.baked.Recipe;
import com.example.fxsuite.baked.Resolution;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotDesign;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.wear.Armour;
import com.example.fxsuite.life.body.wear.Plate;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * How a shelled robot is made: the body shop's male model's skin, lean, 188 cm ({@link Proportions#MODEL}), and its
 * shell of plates shaped over it ({@link Shell}), its forearms' over the skin as its forearms are held; within, its
 * frame ({@link RobotParts}), its helmet in the shell's colour, its chest's cage left out, which is broader than the
 * chest and would stand through its plate. In riding boots in place of its feet's plates, shaped over its shins'.
 * Fine only, and dear: a minute and a half and more, so kept once made.
 */
public final class ShelledRobotRecipe implements Recipe {

    /** How finely its bones are made, cm. */
    private static final double BONES = 0.4;
    /** The parts the boots cover in place of the shell. */
    private static final Set<Part> BOOTED = Set.of(Part.RIGHT_FOOT, Part.LEFT_FOOT, Part.RIGHT_TOES, Part.LEFT_TOES);

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
        final Rig bound = pieces.bound();
        final Flesh skin = pieces.skin(told);
        told.accept("the shelled robot's shell");
        final List<Plate> plates = Shell.INSTANCE.of(pieces.skins().beneath(skin), pieces.skins().beneath(skin,
                new Rig(Proportions.MODEL, Armour.INSTANCE.held(), false)), bound).stream()
                .filter(p -> !BOOTED.contains(p.part())).toList();
        final List<RobotParts.Piece> shelled = new ArrayList<>(RobotParts.INSTANCE.of(bound, RobotDesign.DEFAULT,
                Paint.SHELL, new Detail(BONES)).stream().filter(piece -> piece.part() != Part.CHEST).toList());
        plates.forEach(plate -> shelled.add(new RobotParts.Piece(plate.part(), Paint.SHELL, plate.mesh())));
        shelled.addAll(pieces.boots(plates.stream().filter(p -> p.part() == Part.RIGHT_SHIN
                || p.part() == Part.LEFT_SHIN).toList(), told));
        return pieces.kept(shelled);
    }
}
