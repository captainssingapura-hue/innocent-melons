package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Puppet;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Moves;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A point on a bone, where a muscle is held or passes: as the bone is drawn ({@link Bone#draw}), in its part's frame or,
 * for a bone drawn in the body's, in the body's at rest; as on the right. So it goes wherever the bone goes: a longer
 * bone takes it with it, a joint turned turns it. On the radius, turned across the ulna as the radius is.
 *
 * @param bone the bone it is on
 * @param at   where on it, as the bone is drawn, cm
 */
public record Anchor(Bone bone, V3 at) implements Scalar {

    /** Where it is in its part's frame, as on the right. */
    public V3 local() {
        V3 v = bone.inBody() ? at.minus(new Rig(Proportions.DRAWN, Pose.REST, false).frame(bone.part()).at()) : at;
        if (bone.moves() == Moves.RADIUS) {
            v = Rig.twisted(v, Puppet.TWIST, 1);
        }
        return v;
    }

    /** The part it moves with: its bone's, on the right or the left; one down the middle, that one. */
    public Part part(final boolean left) {
        final Part p = bone.part();
        return p.side() == 0 || !left ? p : p.other();
    }

    /** Where it is on the body {@code rig} holds: on the right, or on the left, its mirror. */
    public V3 in(final Rig rig, final boolean left) {
        final Part p = bone.part();
        final V3 l = local();
        if (p.side() == 0) {
            return rig.place(p, left ? new V3(-l.x(), l.y(), l.z()) : l);
        }
        return rig.frame(left ? p.other() : p).place(l);
    }

    /**
     * {@code direction}, a way as the bone is drawn, on the body {@code rig} holds, on the right or the left; on the
     * radius, turned with it.
     */
    public V3 way(final V3 direction, final Rig rig, final boolean left) {
        final Part p = bone.part();
        final V3 way = bone.moves() == Moves.RADIUS
                ? Rig.twisted(direction, Puppet.TWIST, 1).minus(Rig.twisted(V3.ZERO, Puppet.TWIST, 1)) : direction;
        if (p.side() == 0) {
            return rig.frame(p).way(left ? new V3(-way.x(), way.y(), way.z()) : way);
        }
        return rig.frame(left ? p.other() : p).way(way);
    }
}
