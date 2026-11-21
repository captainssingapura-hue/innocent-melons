package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Which part each point of a body's flesh is shown with, a part shown alone: the part it follows most; but across a
 * limb's joints, the shoulder, the elbow, the wrist, the hip, the knee and the ankle, not along the ragged line where
 * its shares change from the one part to the other, but along a plane square to the limb there, {@link #UP} cm up from
 * the joint into the part above: what of the two lies below it the lower one's, above it the upper one's; so that each
 * ends in an even ring round the limb. The body as it was bound.
 */
public final class PartCut {

    /** How far up from a limb's joint into the part above it is cut, cm. */
    private static final double UP = 2;

    private final Set<Part> limbs = EnumSet.of(Part.RIGHT_UPPER_ARM, Part.RIGHT_FOREARM, Part.RIGHT_HAND,
            Part.LEFT_UPPER_ARM, Part.LEFT_FOREARM, Part.LEFT_HAND, Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT,
            Part.LEFT_THIGH, Part.LEFT_SHIN, Part.LEFT_FOOT);
    private final Map<Part, V3> joints = new EnumMap<>(Part.class);
    /** The way down the limb across each joint, square to the plane it is cut along. */
    private final Map<Part, V3> ways = new EnumMap<>(Part.class);

    /** Cut along the limbs as {@code bind} holds the body. */
    public PartCut(final Rig bind) {
        for (final Part p : limbs) {
            joints.put(p, bind.frame(p).at());
        }
        for (final Part p : limbs) {
            final Part child = limbs.stream().filter(c -> c.parent() == p).findFirst().orElse(null);
            ways.put(p, limbs.contains(p.parent()) ? joints.get(p).minus(joints.get(p.parent())).unit()
                    : joints.get(child).minus(joints.get(p)).unit());
        }
    }

    /** The part each point of {@code f} is shown with. */
    public Part[] of(final Flesh f) {
        final Part[] owner = new Part[f.size()];
        for (int i = 0; i < owner.length; i++) {
            owner[i] = owner(f.most(i), f.point(i));
        }
        return owner;
    }

    /**
     * The part a point at {@code x} following {@code most} most is shown with: a limb's part below it, if it lies
     * past that one's joint's plane; the part above, if it is a limb's and lies short of its own; else {@code most}.
     */
    private Part owner(final Part most, final V3 x) {
        for (final Part c : limbs) {
            if (c.parent() == most && past(c, x)) {
                return c;
            }
        }
        return limbs.contains(most) && !past(most, x) ? most.parent() : most;
    }

    /** Whether {@code x} lies past the plane across limb part {@code p}'s joint, on its side. */
    private boolean past(final Part p, final V3 x) {
        return x.x() * p.side() > 0 && x.minus(joints.get(p)).dot(ways.get(p)) > -UP;
    }
}
