package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.bone.Skeleton;
import com.example.fxsuite.life.body.bone.spine.Coccyx;
import com.example.fxsuite.life.body.bone.spine.Sacrum;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Bones as they are spoken of together, to put together: the skull, with its teeth, the middle ear's bones and the
 * hyoid; the spine, from the atlas down to the coccyx; the chest, its ribs, their cartilages and the sternum; the arms,
 * each its clavicle, scapula, humerus, ulna and radius; the hands; the pelvis, its hip bones, the sacrum and the
 * coccyx; the legs, each its femur, kneecap, tibia and fibula; the feet. A bone may be in more than one: the sacrum is
 * the spine's and the pelvis's.
 */
public enum Group {
    SKULL("Skull"),
    SPINE("Spine"),
    THORAX("Chest"),
    ARMS("Arms"),
    HANDS("Hands"),
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
        final Set<Bone> all = new LinkedHashSet<>(bones());
        for (final Group g : more) {
            all.addAll(g.bones());
        }
        return new Scope(all);
    }

    /** Its bones. */
    public List<Bone> bones() {
        final List<Bone> all = Skeleton.INSTANCE.bones();
        return switch (this) {
            case SKULL -> all.stream().filter(b -> b.region() == Region.SKULL || b.region() == Region.EAR
                    || b.region() == Region.THROAT).toList();
            case SPINE -> all.stream().filter(b -> b.region() == Region.SPINE).toList();
            case THORAX -> all.stream().filter(b -> b.region() == Region.THORAX).toList();
            case ARMS -> all.stream().filter(b -> b.region() == Region.SHOULDER || b.region() == Region.ARM).toList();
            case HANDS -> all.stream().filter(b -> b.region() == Region.HAND).toList();
            case PELVIS -> all.stream().filter(b -> b.region() == Region.HIP || b == Sacrum.INSTANCE
                    || b == Coccyx.INSTANCE).toList();
            case LEGS -> all.stream().filter(b -> b.region() == Region.LEG).toList();
            case FEET -> all.stream().filter(b -> b.region() == Region.FOOT).toList();
        };
    }
}
