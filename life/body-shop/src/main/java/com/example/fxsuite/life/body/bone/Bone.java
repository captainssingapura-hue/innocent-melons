package com.example.fxsuite.life.body.bone;

import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * A bone, or what goes with the bones (the teeth, the ribs' cartilage): each its own stateless object, saying what it
 * is, where in the body, the part of the body it moves with, whether there is one on each side, and how it is drawn:
 * its shape, in the papers' figure's centimetres, as on the right side, in its part's own frame, or in the body's as it
 * stands at rest where the part stands unturned there.
 */
public interface Bone extends StatelessFunctionalObject {

    /** What it is called, not saying which side. */
    String name();

    /** Where in the body it is. */
    Region region();

    /** The part of the body it moves with: on a side, the right one. */
    Part part();

    /** Whether there is one on each side: on a part that has a side, always; down the middle, if it says so. */
    default boolean paired() {
        return part().side() != 0;
    }

    /** Whether it is drawn in the body's frame at rest, not its part's. */
    default boolean inBody() {
        return false;
    }

    /** What it is: a bone, counted among the body's 206, or teeth or cartilage. */
    default Kind kind() {
        return Kind.BONE;
    }

    /** How it moves with its part. */
    default Moves moves() {
        return Moves.WITH_PART;
    }

    /** What it is coloured. */
    default Paint paint() {
        return Paint.IVORY;
    }

    /** The coarsest grid its surface may be found on and keep its shape, cm. */
    double coarsest();

    /** Itself drawn on {@code s}, as on the right side, a woman's as far as {@code female} says (0 a man's). */
    void draw(Sketch s, double female);
}
