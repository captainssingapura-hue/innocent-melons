package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.bone.Skeleton;
import com.example.fxsuite.life.body.bone.spine.Coccyx;
import com.example.fxsuite.life.body.bone.spine.Sacrum;

import java.util.List;

/**
 * Bones as they are spoken of together, to put together: the spine, from the atlas down to the coccyx; the pelvis, its
 * hip bones, the sacrum and the coccyx; the legs, each its femur, kneecap, tibia and fibula; the feet. A bone may be in
 * more than one: the sacrum is the spine's and the pelvis's.
 */
public enum Group {
    SPINE("Spine"),
    PELVIS("Pelvis"),
    LEGS("Legs"),
    FEET("Feet");

    private final String said;

    Group(final String said) {
        this.said = said;
    }

    public String said() {
        return said;
    }

    /** Its bones and {@code more}'s, to put together. */
    public Scope with(final Group... more) {
        final java.util.Set<Bone> all = new java.util.LinkedHashSet<>(bones());
        for (final Group g : more) {
            all.addAll(g.bones());
        }
        return new Scope(all);
    }

    /** Its bones. */
    public List<Bone> bones() {
        final List<Bone> all = Skeleton.INSTANCE.bones();
        return switch (this) {
            case SPINE -> all.stream().filter(b -> b.region() == Region.SPINE).toList();
            case PELVIS -> all.stream().filter(b -> b.region() == Region.HIP || b == Sacrum.INSTANCE
                    || b == Coccyx.INSTANCE).toList();
            case LEGS -> all.stream().filter(b -> b.region() == Region.LEG).toList();
            case FEET -> all.stream().filter(b -> b.region() == Region.FOOT).toList();
        };
    }
}
