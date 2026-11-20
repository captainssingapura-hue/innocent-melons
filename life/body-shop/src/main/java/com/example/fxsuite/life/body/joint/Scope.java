package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;
import java.util.Set;

/**
 * Which bones to put together: any set of them; every joint between two of them is found among the skeleton's joints
 * ({@link Joints}) and walked, no other.
 *
 * @param bones the bones
 */
public record Scope(Set<Bone> bones) implements Scalar {

    public Scope {
        bones = Set.copyOf(bones);
    }

    /** Whether {@code bone} is among them. */
    public boolean holds(final Bone bone) {
        return bones.contains(bone);
    }

    /** The joints between two of them, as joints the body has: a complex one only as far as its bones are here. */
    public List<Complex> joints() {
        return Joints.INSTANCE.all().stream()
                .filter(c -> c.articulations().stream().anyMatch(j -> holds(j.from()) && holds(j.to()))).toList();
    }
}
