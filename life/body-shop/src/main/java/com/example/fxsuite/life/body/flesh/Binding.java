package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * How a body's flesh is shared among its parts at its joints, as it is held to be bound: each point follows the part
 * whose bone is nearest it, of the chain it is on; near a joint, it shades smoothly into the part on the other side, as
 * far as it lies along the joint's way into that part, from wholly the one a joint's reach before it to wholly the
 * other a reach beyond. So whichever part a point is nearer, it is shared alike: no seam where the nearer part changes.
 * At a hip or a shoulder, where a limb meets the trunk, only as near the joint as the joint reaches, so that one hip
 * does not reach the other's buttock. The joints: the waist, the chest's, the neck's; each hip, knee, shoulder and
 * elbow; their reaches set from the size of the joint, in the drawn figure's centimetres.
 */
public final class Binding {

    /** A joint: the parts either side, its middle, its way into the child, how far either side it shades, gated. */
    private record Joint(Part parent, Part child, V3 centre, V3 axis, double reach, double off, boolean gated) {
    }

    private final Map<Part, V3[]> bones = new EnumMap<>(Part.class);
    private final List<Joint> joints = new ArrayList<>();

    /** The joints and the bones of the body {@code bound} holds, {@code scale} times the drawn figure. */
    public Binding(final Rig bound, final double scale) {
        bone(bound, Part.PELVIS, Part.ABDOMEN);
        bone(bound, Part.ABDOMEN, Part.CHEST);
        bone(bound, Part.CHEST, Part.NECK);
        bone(bound, Part.NECK, Part.HEAD);
        joint(bound, Part.PELVIS, Part.ABDOMEN, Part.CHEST, 6 * scale, 0, false);
        joint(bound, Part.ABDOMEN, Part.CHEST, Part.NECK, 6 * scale, 0, false);
        joint(bound, Part.CHEST, Part.NECK, Part.HEAD, 4 * scale, 0, false);
        for (final boolean right : new boolean[] {true, false}) {
            final Part thigh = right ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final Part shin = right ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            final Part foot = right ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            final Part arm = right ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
            final Part forearm = right ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM;
            final Part hand = right ? Part.RIGHT_HAND : Part.LEFT_HAND;
            bone(bound, thigh, shin);
            bone(bound, shin, foot);
            bone(bound, arm, forearm);
            bone(bound, forearm, hand);
            joint(bound, Part.PELVIS, thigh, shin, 7 * scale, 0, true);
            joint(bound, thigh, shin, foot, 5 * scale, 0, false);
            joint(bound, Part.CHEST, arm, forearm, 6 * scale, -2 * scale, true);
            joint(bound, arm, forearm, hand, 4 * scale, 0, false);
        }
    }

    /** How much {@code v}, on the chain of {@code chain}'s parts, follows each of {@code parts}, into {@code w}. */
    public void shares(final V3 v, final List<Part> chain, final List<Part> parts, final double[] w) {
        Part primary = chain.getFirst();
        double nearest = Double.MAX_VALUE;
        for (final Part p : chain) {
            final double d = toBone(v, bones.get(p));
            if (d < nearest) {
                nearest = d;
                primary = p;
            }
        }
        double kept = 1;
        for (final Joint j : joints) {
            if (j.parent() != primary && j.child() != primary) {
                continue;
            }
            final double s = v.minus(j.centre()).dot(j.axis()) - j.off();
            final double t = smooth((s + j.reach()) / (2 * j.reach()));
            final double g = j.gated() ? 1 - smooth((v.minus(j.centre()).length() - 1.5 * j.reach()) / j.reach()) : 1;
            final double moved = j.parent() == primary ? g * t : g * (1 - t);
            final Part other = j.parent() == primary ? j.child() : j.parent();
            w[parts.indexOf(other)] += moved;
            kept -= moved;
        }
        w[parts.indexOf(primary)] += Math.max(kept, 0);
    }

    /** {@code p}'s bone: from its joint to its child's. */
    private void bone(final Rig bound, final Part p, final Part child) {
        bones.put(p, new V3[] {bound.frame(p).at(), bound.frame(child).at()});
    }

    /** The joint between {@code parent} and {@code child}, its way the child's bone's, toward {@code next}. */
    private void joint(final Rig bound, final Part parent, final Part child, final Part next, final double reach,
                       final double off, final boolean gated) {
        final V3 centre = bound.frame(child).at();
        joints.add(new Joint(parent, child, centre, bound.frame(next).at().minus(centre).unit(), reach, off, gated));
    }

    /** How far {@code v} is from the bone from {@code b[0]} to {@code b[1]}. */
    private double toBone(final V3 v, final V3[] b) {
        final V3 d = b[1].minus(b[0]);
        final double t = Math.clamp(v.minus(b[0]).dot(d) / d.dot(d), 0, 1);
        return v.minus(b[0].plus(d.times(t))).length();
    }

    /** {@code x} eased from 0 to 1, flat at either end. */
    private double smooth(final double x) {
        final double t = Math.clamp(x, 0, 1);
        return t * t * (3 - 2 * t);
    }
}
