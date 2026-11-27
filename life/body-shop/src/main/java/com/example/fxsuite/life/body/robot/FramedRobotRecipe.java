package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.baked.Piece;
import com.example.fxsuite.baked.Recipe;
import com.example.fxsuite.baked.Resolution;
import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotDesign;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;
import java.util.function.Consumer;

/**
 * How a framed robot is made: its frame bare ({@link RobotParts}), on the body shop's male model as it is bound,
 * 188 cm ({@link Proportions#MODEL}), its design the default, its helmet in the frame's metal. Coarse, its bones made
 * a centimetre fine; fine, four millimetres. Cheap at either, a second or two: made each time, never kept.
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
        return false;
    }

    @Override
    public String madeBy() {
        return pieces.madeBy(leaf().title());
    }

    @Override
    public List<Piece> made(final Resolution resolution, final Consumer<String> told) {
        told.accept("the framed robot, " + resolution.named());
        final Rig bound = new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false);
        return pieces.kept(RobotParts.INSTANCE.of(bound, RobotDesign.DEFAULT, Paint.FRAME,
                new Detail(resolution == Resolution.COARSE ? COARSE : FINE)));
    }
}
