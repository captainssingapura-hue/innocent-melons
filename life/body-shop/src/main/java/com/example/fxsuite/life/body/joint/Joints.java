package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.joint.arm.ArmJoints;
import com.example.fxsuite.life.body.joint.foot.FootJoints;
import com.example.fxsuite.life.body.joint.hand.HandJoints;
import com.example.fxsuite.life.body.joint.leg.LegJoints;
import com.example.fxsuite.life.body.joint.pelvis.PelvisJoints;
import com.example.fxsuite.life.body.joint.skull.SkullJoints;
import com.example.fxsuite.life.body.joint.spine.SpineJoints;
import com.example.fxsuite.life.body.joint.thorax.ThoraxJoints;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;
import java.util.stream.Stream;

/**
 * Every joint of the skeleton, each once, a paired one once for both sides: the skull's, the spine's, the chest's, the
 * arms', the hands', the pelvis's, the legs', the feet's. What a scope of bones finds its joints among ({@link Scope}).
 * Two of the body's meetings are not joints of bone and are not here: the shoulder blade gliding on the chest, and the
 * hyoid hung from the skull by muscle; put together with the rest, the hyoid is reached by none.
 */
public record Joints() implements StatelessFunctionalObject {

    public static final Joints INSTANCE = new Joints();

    /** Every joint. */
    public List<Complex> all() {
        return Stream.of(SkullJoints.INSTANCE.joints(), SpineJoints.INSTANCE.joints(), ThoraxJoints.INSTANCE.joints(),
                ArmJoints.INSTANCE.joints(), HandJoints.INSTANCE.joints(), PelvisJoints.INSTANCE.joints(),
                LegJoints.INSTANCE.joints(), FootJoints.INSTANCE.joints()).flatMap(List::stream).toList();
    }
}
