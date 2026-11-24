package com.example.fxsuite.life.body.bone;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.bone.thorax.ThoraxForm;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * A bone's shape as it draws itself: in its part's frame as on the right side, a bone drawn in the body's frame taken
 * there by where its part stands at rest, the papers' figure's, hanging free. A bone of the head bent as the skull is
 * to be as men's are measured ({@link SkullForm}); the sternum and the clavicles moved as the chest is, its ribs
 * measured ({@link ThoraxForm}).
 */
public record Drawn() implements StatelessFunctionalObject {

    public static final Drawn INSTANCE = new Drawn();

    /** {@code bone}'s shape, a woman's as far as {@code female} says. */
    public Field.Compound shape(final Bone bone, final double female) {
        final V3 origin = bone.inBody() ? ThoraxForm.INSTANCE.drawnFrom(bone,
                new Rig(Proportions.DRAWN, Pose.REST, false).frame(bone.part()).at()) : V3.ZERO;
        final Sketch s = new Sketch(origin, 1);
        bone.draw(s, female);
        if (bone.part() == Part.HEAD) {
            return SkullForm.INSTANCE.shaped(s.done());
        }
        return moved(bone) ? ThoraxForm.INSTANCE.shaped(s.done(), origin) : s.done();
    }

    /** Whether {@code bone} is moved with the chest as its ribs are measured: the sternum, a clavicle. */
    private boolean moved(final Bone bone) {
        return bone == Sternum.INSTANCE || bone == Clavicle.INSTANCE;
    }
}
