package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * One bone of a skeleton: a bone, on its side; a bone down the middle once, a paired one twice, its right and its left.
 *
 * @param bone what bone
 * @param side which side
 */
public record BoneAt(Bone bone, Side side) implements Scalar {

    /** In words: "left femur", "sacrum". */
    public String said() {
        return (side == Side.MIDDLE ? "" : side.name().toLowerCase(java.util.Locale.ROOT) + " ") + bone.name();
    }
}
