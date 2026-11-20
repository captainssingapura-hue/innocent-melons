package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.bone.spine.SpineBones;
import com.example.fxsuite.life.body.bone.spine.T7;
import com.example.fxsuite.life.body.joint.spine.IntervertebralL1L2;
import com.example.fxsuite.life.body.joint.spine.IntervertebralL2L3;
import com.example.fxsuite.life.body.joint.spine.IntervertebralL3L4;
import com.example.fxsuite.life.body.joint.spine.IntervertebralL4L5;
import com.example.fxsuite.life.body.joint.spine.IntervertebralL5S1;
import com.example.fxsuite.life.body.joint.spine.SpineJoints;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The spine put together from its joints: every bone reached, each at rest exactly where it is drawn, whichever bone it
 * is put together from; bent, as far as its joints have moved, the same whichever bone it is held by; no joint moved
 * past its range.
 */
class SpineAssemblyTest {

    private final List<Complex> joints = SpineJoints.INSTANCE.joints();
    private final Scope spine = Group.SPINE.with();
    private final Rig rest = new Rig(Proportions.DRAWN, Pose.REST, false);

    @Test
    void everyBoneIsReachedOnce() {
        final Assembly.Assembled a = from(Sacrum.INSTANCE, Posture.REST);
        assertEquals(26, a.places().size());
        assertEquals(SpineBones.INSTANCE.bones().size(), a.places().size());
        assertEquals(25, joints.size());
        assertEquals(List.of(), a.gaps(), "a chain: no joint met twice");
    }

    @Test
    void atRestEachBoneIsWhereItIsDrawnFromAnyBone() {
        for (final Bone root : List.of(Sacrum.INSTANCE, T7.INSTANCE, L1.INSTANCE)) {
            final Assembly.Assembled a = from(root, Posture.REST);
            for (final Bone b : SpineBones.INSTANCE.bones()) {
                final Place p = a.places().get(new BoneAt(b, Side.MIDDLE));
                assertEquals(0, p.at().minus(rest.frame(b.part()).at()).length(), 1e-9,
                        b.name() + " where it is drawn, put together from the " + root.name());
                assertEquals(0, turned(p), 1e-4, b.name() + " unturned");
            }
        }
    }

    @Test
    void theLumbarSpineBendsAsFarAsItsJointsTogether() {
        Posture bent = Posture.REST;
        for (final Complex j : List.<Complex>of(IntervertebralL5S1.INSTANCE, IntervertebralL4L5.INSTANCE,
                IntervertebralL3L4.INSTANCE, IntervertebralL2L3.INSTANCE, IntervertebralL1L2.INSTANCE)) {
            bent = bent.with(j, Side.MIDDLE, Movement.FLEXION, 5);
        }
        final Place l1 = from(Sacrum.INSTANCE, bent).places().get(new BoneAt(L1.INSTANCE, Side.MIDDLE));
        final V3 up = l1.turn().apply(V3.Y);
        assertEquals(25, Math.toDegrees(Math.acos(up.y())), 1e-4, "five discs, five degrees each");
        assertTrue(up.z() > 0, "bent forward");
        // held by L1 where it went, the sacrum comes back to where it stood
        final Assembly.Assembled held = Assembly.INSTANCE.build(spine, new BoneAt(L1.INSTANCE, Side.MIDDLE), l1, bent);
        final Place sacrum = held.places().get(new BoneAt(Sacrum.INSTANCE, Side.MIDDLE));
        assertEquals(0, sacrum.at().length(), 1e-9);
        assertEquals(0, turned(sacrum), 1e-4);
    }

    @Test
    void noJointMovesPastItsRange() {
        assertThrows(IllegalArgumentException.class,
                () -> Posture.REST.with(IntervertebralL5S1.INSTANCE, Side.MIDDLE, Movement.TWIST, 10));
        assertThrows(IllegalArgumentException.class,
                () -> Posture.REST.with(IntervertebralL1L2.INSTANCE, Side.MIDDLE, Movement.FLEXION, -6));
    }

    /** Put together from {@code root}, it where it is drawn at rest. */
    private Assembly.Assembled from(final Bone root, final Posture posture) {
        return Assembly.INSTANCE.build(spine, new BoneAt(root, Side.MIDDLE),
                new Place(rest.frame(root.part()).at(), com.example.fxsuite.life.body.Rot.NONE), posture);
    }

    /** How far {@code p} is turned, degrees. */
    private double turned(final Place p) {
        final double cos = Math.clamp((p.turn().xx() + p.turn().yy() + p.turn().zz() - 1) / 2, -1, 1);
        return Math.toDegrees(Math.acos(cos));
    }
}
