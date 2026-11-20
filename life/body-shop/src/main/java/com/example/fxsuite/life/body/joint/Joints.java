package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.joint.foot.FootJoints;
import com.example.fxsuite.life.body.joint.leg.LegJoints;
import com.example.fxsuite.life.body.joint.pelvis.PelvisJoints;
import com.example.fxsuite.life.body.joint.spine.SpineJoints;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;
import java.util.stream.Stream;

/**
 * Every joint of the skeleton there is so far, each once, a paired one once for both sides: the spine's, the pelvis's,
 * the legs', the feet's. What a scope of bones finds its joints among ({@link Scope}).
 */
public record Joints() implements StatelessFunctionalObject {

    public static final Joints INSTANCE = new Joints();

    /** Every joint. */
    public List<Complex> all() {
        return Stream.of(SpineJoints.INSTANCE.joints(), PelvisJoints.INSTANCE.joints(), LegJoints.INSTANCE.joints(),
                FootJoints.INSTANCE.joints()).flatMap(List::stream).toList();
    }
}
