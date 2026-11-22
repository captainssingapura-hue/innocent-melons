package com.example.fxsuite.life.body.bone;

import com.example.fxsuite.life.body.bone.arm.ArmBones;
import com.example.fxsuite.life.body.bone.foot.FootBones;
import com.example.fxsuite.life.body.bone.hand.HandBones;
import com.example.fxsuite.life.body.bone.hip.HipBones;
import com.example.fxsuite.life.body.bone.leg.LegBones;
import com.example.fxsuite.life.body.bone.skull.SkullBones;
import com.example.fxsuite.life.body.bone.spine.SpineBones;
import com.example.fxsuite.life.body.bone.thorax.ThoraxBones;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;
import java.util.stream.Stream;

/**
 * The skeleton's bones, each once, by region: the head's and the throat's, the spine's, the chest's, the shoulder
 * girdle's and the arm's, the hand's, the pelvis's, the leg's and the foot's; a bone with one on each side once for
 * both. A list, no more: how they are put together is not said here.
 */
public record Skeleton() implements StatelessFunctionalObject {

    public static final Skeleton INSTANCE = new Skeleton();

    /** Every bone, and the teeth and cartilage with them, region by region. */
    public List<Bone> bones() {
        return Stream.of(SkullBones.INSTANCE.bones(), SpineBones.INSTANCE.bones(), ThoraxBones.INSTANCE.bones(),
                ArmBones.INSTANCE.bones(), HandBones.INSTANCE.bones(), HipBones.INSTANCE.bones(),
                LegBones.INSTANCE.bones(), FootBones.INSTANCE.bones()).flatMap(List::stream).toList();
    }

    /** How many of the body's bones they are: a paired one two. */
    public int counted() {
        return bones().stream().filter(b -> b.kind() == Kind.BONE).mapToInt(Bone::count).sum();
    }
}
