package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The body shop's body, against the acceptance tests of the body model handover (Papers/Body) as far as they reach
 * it: posed, every bone keeps its length and every joint its limits; at rest it stands on its feet, its centre of mass
 * on its middle line between 53 and 58 per cent of its height up, balanced; a part dragged turns at its own joint after
 * the mouse; every piece of it a closed skin facing outward.
 */
class BodyTest {

    private static final Proportions P = Proportions.PAPERS;
    private static final double E = 1e-6;

    @Test
    void atRestItStandsOnItsFeetItsHipsAsHighAsItsLegsAreLong() {
        final Rig r = new Rig(P, Pose.REST);
        assertEquals(P.legs() * P.stature(), r.lift(), 1.0, "its hip joints as high as said");
        assertEquals(10, r.touching().size(), "every point of both feet on the ground");
        assertEquals(0, r.contacts().stream().mapToDouble(V3::y).min().orElseThrow(), E);
    }

    @Test
    void atRestItsCentreOfMassIsOnItsMiddleLineAsHighAsAPersonsAndItIsBalanced() {
        final Rig r = new Rig(P, Pose.REST);
        final V3 c = r.centreOfMass();
        assertEquals(0, c.x(), 1e-6, "on its middle line");
        assertTrue(c.y() > 0.53 * P.stature() && c.y() < 0.58 * P.stature(),
                "between 53% and 58% of its height up, not " + c.y() / P.stature());
        assertTrue(r.margin() > 0, "balanced");
    }

    @Test
    void itsSidesAreEachOthersInAMirror() {
        final Rig r = new Rig(P, Pose.REST);
        for (final Part p : Part.values()) {
            if (p.side() > 0) {
                final V3 right = r.place(p, new V3(1, -5, 2));
                final V3 left = r.place(p.other(), new V3(1, -5, 2));
                assertEquals(right.x(), -left.x(), 1e-9, p.toString());
                assertEquals(right.y(), left.y(), 1e-9, p.toString());
                assertEquals(right.z(), left.z(), 1e-9, p.toString());
            }
        }
    }

    @Test
    void posedEveryBoneKeepsItsLengthAndEveryJointItsLimits() {
        final Rig rest = new Rig(P, Pose.REST, false);
        for (final Pose.Named n : Pose.PRESETS) {
            final Rig r = new Rig(P, n.pose(), false);
            for (final Part p : Part.values()) {
                if (p.parent() != null && p != Part.RIGHT_HAND && p != Part.LEFT_HAND) {
                    final double was = rest.frame(p).at().minus(rest.frame(p.parent()).at()).length();
                    final double is = r.frame(p).at().minus(r.frame(p.parent()).at()).length();
                    assertEquals(was, is, 1e-6, n.name() + ", " + p);
                }
                for (final Dof d : p.dofs()) {
                    final double v = n.pose().get(p, d);
                    assertTrue(v >= d.min() && v <= d.max(), n.name() + ", " + p + " " + d);
                }
            }
        }
    }

    @Test
    void aJointGoesNoFurtherThanItMay() {
        assertEquals(140, Pose.REST.with(Part.RIGHT_SHIN, Dof.KNEE, 200).get(Part.RIGHT_SHIN, Dof.KNEE));
        assertEquals(0, Pose.REST.with(Part.RIGHT_SHIN, Dof.KNEE, -30).get(Part.RIGHT_SHIN, Dof.KNEE));
        assertThrows(IllegalArgumentException.class, () -> Pose.REST.with(Part.RIGHT_SHIN, Dof.ELBOW, 10));
    }

    @Test
    void theBodysPresetsBendAsTheyShould() {
        final Pose kick = Pose.PRESETS.stream().filter(n -> n.name().equals("Front kick")).findFirst().orElseThrow()
                .pose();
        final Rig r = new Rig(P, kick);
        assertTrue(r.frame(Part.RIGHT_FOOT).at().z() > 60, "kicking, the right foot well ahead");
        assertTrue(r.frame(Part.RIGHT_FOOT).at().y() > 50, "kicking, the right foot high");
        final Pose t = Pose.PRESETS.get(1).pose();
        final Rig tr = new Rig(P, t);
        assertEquals(tr.frame(Part.RIGHT_UPPER_ARM).at().y(), tr.frame(Part.RIGHT_HAND).at().y(), 3,
                "in a T, the arms level");
        assertTrue(tr.frame(Part.RIGHT_HAND).at().x() > 60, "in a T, the right hand out to its right");
        final Rig squat = new Rig(P, Pose.PRESETS.get(2).pose());
        assertTrue(squat.lift() < 0.7 * new Rig(P, Pose.REST).lift(), "squatting, lower");
    }

    @Test
    void everyPresetStandsAsThePapersExpect() {
        for (final Pose.Named n : Pose.PRESETS) {
            assertTrue(new Rig(P, n.pose()).margin() > 0, n.name() + ": balanced");
        }
        // single whip, a held stance: both feet flat, comfortably balanced
        final Rig whip = new Rig(P, Pose.PRESETS.get(7).pose());
        assertEquals(10, whip.touching().size(), "single whip on both feet, flat");
        assertTrue(whip.margin() > 5, "single whip comfortably balanced");
    }

    @Test
    void standingOnOneLegItsWeightIsOverThatFoot() {
        final Rig r = new Rig(P, Pose.PRESETS.get(5).pose());
        final long right = r.contacts().subList(0, 5).stream().filter(c -> c.y() < Rig.TOUCHING).count();
        final long left = r.contacts().subList(5, 10).stream().filter(c -> c.y() < Rig.TOUCHING).count();
        assertTrue(right > 0 && left == 0, "on the right foot alone");
    }

    @Test
    void aPartDraggedTurnsAtItsOwnJointAfterTheMouse() {
        // the right upper arm, held at the elbow, drawn out and up to its right: raised sideways
        final Rig free = new Rig(P, Pose.REST, false);
        final V3 elbow = new V3(0, -31, 0);
        final V3 to = free.frame(Part.RIGHT_UPPER_ARM).at().plus(new V3(25, 10, 0));
        final Pose raised = Drag.toward(P, Pose.REST, Part.RIGHT_UPPER_ARM, elbow, to, false);
        assertTrue(raised.get(Part.RIGHT_UPPER_ARM, Dof.RAISE_SIDEWAYS) > 90, "raised past level");
        final V3 now = new Rig(P, raised, false).place(Part.RIGHT_UPPER_ARM, elbow);
        final V3 dir = to.minus(free.frame(Part.RIGHT_UPPER_ARM).at()).unit();
        final V3 got = now.minus(free.frame(Part.RIGHT_UPPER_ARM).at()).unit();
        assertTrue(dir.dot(got) > 0.99, "the elbow where it was drawn to, as near as it comes");
        assertEquals(Pose.REST.get(Part.RIGHT_SHIN, Dof.KNEE), raised.get(Part.RIGHT_SHIN, Dof.KNEE), "nothing else");

        // the shin, held at the ankle, drawn back: the knee bends, no further than it may; drawn forward, it stays
        final V3 ankle = new V3(0, -42, 0);
        final V3 knee = free.frame(Part.RIGHT_SHIN).at();
        final Pose bent = Drag.toward(P, Pose.REST, Part.RIGHT_SHIN, ankle, knee.plus(new V3(0, -30, -30)), false);
        assertEquals(45, bent.get(Part.RIGHT_SHIN, Dof.KNEE), 2, "bent back toward the mouse");
        final Pose straight = Drag.toward(P, Pose.REST, Part.RIGHT_SHIN, ankle, knee.plus(new V3(0, -30, 30)), false);
        assertEquals(0, straight.get(Part.RIGHT_SHIN, Dof.KNEE), E, "a knee does not bend forward");

        // twisted with the control key: the forearm about its length, and only that
        final V3 held = new V3(4, -20, 0);
        final V3 there = new Rig(P, Pose.REST.with(Part.RIGHT_FOREARM, Dof.FOREARM_TWIST, 20), false)
                .held(Part.RIGHT_FOREARM, held);
        final Pose twisted = Drag.toward(P, Pose.REST, Part.RIGHT_FOREARM, held, there, true);
        assertEquals(20, twisted.get(Part.RIGHT_FOREARM, Dof.FOREARM_TWIST), 2, "twisted palm forward");
        assertEquals(10, twisted.get(Part.RIGHT_FOREARM, Dof.ELBOW), E, "its elbow as it was");
    }

    @Test
    void everyPieceIsAClosedSkinFacingOutward() {
        for (final Pose.Named n : List.of(Pose.PRESETS.get(0), Pose.PRESETS.get(7))) {
            final Body body = new Body(P, n.pose());
            for (final Body.Piece piece : body.pieces(EnumSet.allOf(Body.Layer.class))) {
                final Surface s = piece.surface();
                assertTrue(s.volume() > 0, n.name() + ", " + s.name() + ": facing outward");
                assertTrue(s.points().stream().allMatch(V3::finite), s.name());
            }
        }
    }

    @Test
    void aMuscleSwellsAsItShortens() {
        final Pose bent = Pose.REST.with(Part.RIGHT_FOREARM, Dof.ELBOW, 120);
        final Body body = new Body(P, bent);
        assertTrue(body.bulges().get(1).biceps() > 1.1, "the right biceps swollen, the elbow bent");
        assertEquals(1, body.bulges().get(-1).biceps(), 0.05, "the left as it was");
    }

    @Test
    void itsBalanceMarginIsHowFarInsideItsFeetItsCentreFalls() {
        final List<double[]> square = Rig.hull(List.of(new double[] {0, 0}, new double[] {10, 0},
                new double[] {10, 10}, new double[] {0, 10}, new double[] {5, 5}));
        assertEquals(4, square.size());
        assertEquals(3, Rig.margin(new double[] {3, 5}, square), E);
        assertEquals(-2, Rig.margin(new double[] {12, 5}, square), E);
    }

    @Test
    void itIsNotMadeAsNoBodyIs() {
        assertThrows(IllegalArgumentException.class, () -> new Proportions(40, 1, 0.54, 0.44, 0.22, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new Proportions(170, 3, 0.54, 0.44, 0.22, 0.1));
    }
}
