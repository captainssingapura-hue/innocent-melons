package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.baked.Recipe;
import com.example.fxsuite.baked.Resolution;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.RobotDesign;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * How a framed robot is made: its frame bare ({@link RobotParts}), on the body shop's male model as it is bound,
 * 188 cm ({@link Proportions#MODEL}), its design the default, its helmet in the frame's metal; in riding boots, shaped
 * over the model's skin; its hands mechanical, their fingers turning on their own ({@link RobotHands}). Coarse, its
 * bones made a centimetre fine; fine, four millimetres. Its boots wanting the skin made first, dear at either: kept
 * once made.
 */
public final class FramedRobotRecipe implements Recipe {

    private static final double COARSE = 1.0;
    private static final double FINE = 0.4;

    private final RobotPieces pieces;

    /** Its pieces kept and read as {@code pieces} has them. */
    public FramedRobotRecipe(final RobotPieces pieces) {
        this.pieces = pieces;
    }

    @Override
    public Taxonomy tree() {
        return Figures.TREE;
    }

    @Override
    public Leaf<?> leaf() {
        return new Figures.FramedRobot();
    }

    @Override
    public List<Resolution> offered() {
        return List.of(Resolution.COARSE, Resolution.FINE);
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
        told.accept("the framed robot, " + resolution.named());
        final RobotHands hands = new RobotHands();
        final RobotHands.Made held = hands.of(pieces.bound(), Paint.FRAME);
        final List<RobotParts.Piece> made = new ArrayList<>(hands.handless(RobotParts.INSTANCE.of(pieces.bound(),
                RobotDesign.DEFAULT, Paint.FRAME, new Detail(resolution == Resolution.COARSE ? COARSE : FINE))));
        made.addAll(held.palms());
        made.addAll(pieces.boots(List.of(), told));
        final List<Piece> kept = new ArrayList<>(pieces.kept(made));
        kept.addAll(hands.kept(held.fingers()));
        return kept;
    }
}
