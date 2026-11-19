package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The avatar's pose read onto the humanoid's joints: a leg swung back bends its knee, its foot kept level; an arm
 * swung and an elbow bent as the pose has them; the head turned to the right turning the humanoid's to its right.
 */
class HumanoidFigureTest {

    private static com.example.fxsuite.avatar.look.Pose pose(final double leftLeg, final double rightLeg,
                                                             final double rightArm, final double rightElbow,
                                                             final double headYaw) {
        return new com.example.fxsuite.avatar.look.Pose(0, 0, 0, 0, rightArm, 0, 0, 0, rightElbow, leftLeg, rightLeg,
                0, headYaw, 0, 0, 0, 0);
    }

    @Test
    void aLegSwungBackBendsItsKneeItsFootLevel() {
        final Pose p = HumanoidFigure.read(pose(-15, 25, 0, 0, 0));
        assertEquals(-15, p.get(Part.LEFT_THIGH, Dof.HIP_FORWARD), 1e-9);
        assertTrue(p.get(Part.LEFT_SHIN, Dof.KNEE) > 15, "swung back, its knee bent");
        assertTrue(p.get(Part.RIGHT_SHIN, Dof.KNEE) < 10, "swung ahead, its knee nearly straight");
        for (final Part foot : new Part[] {Part.LEFT_FOOT, Part.RIGHT_FOOT}) {
            final Part shin = foot == Part.LEFT_FOOT ? Part.LEFT_SHIN : Part.RIGHT_SHIN;
            final Part thigh = foot == Part.LEFT_FOOT ? Part.LEFT_THIGH : Part.RIGHT_THIGH;
            // level: its toes up by the hip's swing, down by the knee's bend and the ankle's
            assertEquals(0, -p.get(thigh, Dof.HIP_FORWARD) + p.get(shin, Dof.KNEE) + p.get(foot, Dof.ANKLE), 1e-9,
                    foot + " level");
        }
    }

    @Test
    void itsArmsAndHeadAsThePoseHasThem() {
        final Pose p = HumanoidFigure.read(pose(0, 0, 40, 70, 30));
        assertEquals(40, p.get(Part.RIGHT_UPPER_ARM, Dof.RAISE_FORWARD), 1e-9);
        assertEquals(70, p.get(Part.RIGHT_FOREARM, Dof.ELBOW), 1e-9);
        // turned to the right: the humanoid turns right with a turn less than nothing
        assertEquals(-15, p.get(Part.NECK, Dof.NECK_TURN), 1e-9);
        assertEquals(-15, p.get(Part.HEAD, Dof.NECK_TURN), 1e-9);
        final Rig r = new Rig(Proportions.PAPERS, p);
        final V3 face = r.frame(Part.HEAD).way(V3.Z);
        assertTrue(face.x() > 0.4, "its face to its right");
    }
}
