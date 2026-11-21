package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The gait as a person's: the right foot ahead as it strikes, the right hip with it; the pelvis dropping on the side
 * swinging through; the arms against the legs; the head looking ahead; the swinging foot clear of the ground. Carried
 * on by its feet at the speed asked, its strides as often as a person's; the gun held, the hands on it.
 */
class GaitTest {

    private static final Proportions BODY = Proportions.PAPERS;

    private static Rig at(final double c, final double speed) {
        return new Rig(BODY, Gait.at(c, speed, 0, false));
    }

    @Test
    void theRightFootAndHipAheadAsItStrikes() {
        final Rig r = at(0, 140);
        assertTrue(r.frame(Part.RIGHT_FOOT).at().z() > r.frame(Part.LEFT_FOOT).at().z() + 30, "right foot ahead");
        assertTrue(r.frame(Part.RIGHT_THIGH).at().z() > r.frame(Part.LEFT_THIGH).at().z() + 0.5, "right hip ahead");
        final Rig l = at(0.5, 140);
        assertTrue(l.frame(Part.LEFT_FOOT).at().z() > l.frame(Part.RIGHT_FOOT).at().z() + 30, "left foot ahead");
    }

    @Test
    void thePelvisDropsOnTheSideSwingingThrough() {
        // the right foot down, the left swinging: the left hip lower
        final Rig r = at(0.15, 140);
        assertTrue(r.frame(Part.LEFT_THIGH).at().y() < r.frame(Part.RIGHT_THIGH).at().y() - 0.5, "left hip lower");
    }

    @Test
    void theArmsSwingAgainstTheLegs() {
        final Rig r = at(0.5, 140);
        assertTrue(r.place(Part.RIGHT_HAND, V3.ZERO).z() > r.place(Part.LEFT_HAND, V3.ZERO).z() + 10,
                "the right hand ahead as the left foot strikes");
    }

    @Test
    void theHeadLooksAheadAndTheSwingingFootClearsTheGround() {
        for (double c = 0; c < 1; c += 0.05) {
            for (final double speed : new double[] {140, 500}) {
                final Rig r = at(c, speed);
                final V3 face = r.frame(Part.HEAD).way(V3.Z);
                assertEquals(0, face.x(), 0.06, "looking ahead at " + c);
            }
        }
        // the right foot, mid-swing walking: every point of it off the ground
        final Rig r = at(0.8, 140);
        final List<V3> c = r.contacts();
        for (int k = 0; k < 5; k++) {
            assertTrue(c.get(k).y() > 0.8, "right foot clear: " + c.get(k));
        }
    }

    @Test
    void carriedOnByItsFeetAtTheSpeedAsked() {
        for (final double speed : new double[] {80, 140, 300, 500, 760}) {
            final Walker w = new Walker(BODY);
            final double seconds = 6;
            for (int k = 0; k < seconds * 120; k++) {
                w.step(1.0 / 120, speed, false);
            }
            assertEquals(speed, w.ahead() / seconds, speed * 0.08, "at " + speed);
        }
    }

    @Test
    void stridesAsOftenAsAPersons() {
        final Walker w = new Walker(BODY);
        final double walking = w.cadence(140) * 2 * 60;
        assertTrue(walking > 95 && walking < 130, "steps a minute walking: " + walking);
        final double running = w.cadence(500) * 2 * 60;
        assertTrue(running > 140 && running < 200, "steps a minute running: " + running);
    }

    @Test
    void theHandsOnTheGun() {
        final GatlingHold hold = new GatlingHold(BODY);
        Pose arms = GatlingHold.ready(Pose.REST);
        for (double c = 0; c < 1; c += 0.1) {
            for (final double aim : new double[] {-10, 0, 25}) {
                final Pose p = GatlingHold.arms(Gait.at(c, 140, 0, true), arms);
                final GatlingHold.Held h = hold.hold(p, aim);
                arms = h.pose();
                final Rig r = new Rig(BODY, h.pose());
                final V3 rear = h.gun().place(GatlingHold.REAR_GRIP);
                // the left palm on the side handle, wherever along it
                final V3 d = r.held(Part.LEFT_HAND, GatlingHold.PALM).minus(h.gun().origin());
                final double along = d.dot(h.gun().ahead());
                final double off = Math.hypot(d.dot(h.gun().right()) - GatlingHold.CARRY_GRIP.x(),
                        d.dot(h.gun().up()) - GatlingHold.CARRY_GRIP.y());
                assertEquals(0, r.held(Part.RIGHT_HAND, GatlingHold.PALM).minus(rear).length(), 1.5,
                        "right hand at " + c + ", aim " + aim);
                assertTrue(off < 2.5 && along > GatlingHold.CARRY_FROM && along < GatlingHold.CARRY_TO,
                        "left hand at " + c + ", aim " + aim + ": " + off + " off, " + along + " along");
            }
        }
    }

    @Test
    void theGunBesideTheRightHipSquareToTheTorso() {
        final GatlingHold.Held h = new GatlingHold(BODY).hold(GatlingHold.ready(Pose.REST), 0);
        final Rig r = new Rig(BODY, h.pose());
        final V3 bears = h.gun().place(h.sling().bears());
        final V3 hip = r.frame(Part.RIGHT_THIGH).at();
        assertTrue(bears.x() > hip.x() + 5 && Math.abs(bears.y() - hip.y()) < 10 && Math.abs(bears.z() - hip.z()) < 10,
                "its side against the outside of the right hip: " + bears + ", the hip " + hip);
        // hung from over where it bears: its swivel straight above
        final V3 swivel = h.gun().place(h.sling().swivel());
        assertEquals(bears.z(), swivel.z(), 0.5, "its swivel over where it bears");
        // square to the torso: along the gun, nothing of the way up the torso
        final V3 torso = r.place(Part.CHEST, new V3(0, 20, 0)).minus(r.frame(Part.PELVIS).at()).unit();
        assertEquals(0, h.gun().ahead().dot(torso), 0.05, "square to the torso");
        // the left arm near straight, from the shoulder down to the side handle
        assertTrue(r.pose().get(Part.LEFT_FOREARM, Dof.ELBOW) < 35,
                "the left arm near straight: " + r.pose().get(Part.LEFT_FOREARM, Dof.ELBOW));
    }

    @Test
    void theStrapAndTheHipCarryTheGunTheHandsOnlySteadyIt() {
        final GatlingHold hold = new GatlingHold(BODY);
        Pose arms = GatlingHold.ready(Pose.REST);
        for (final double speed : new double[] {0, 140}) {
            for (double c = 0; c < 1; c += 0.125) {
                for (final double aim : new double[] {-10, 0, 25}) {
                    final GatlingHold.Held h = hold.hold(GatlingHold.arms(Gait.at(c, speed, 0, true), arms), aim);
                    arms = h.pose();
                    final GunStatics.Solved s = GunStatics.solve(BODY, h);
                    final String at = " at " + speed + " cm/s, " + c + " of a stride, aim " + aim;
                    // the hip pushing it out against the strap's pull in toward the middle
                    assertTrue(s.hip().x() > 50, "the hip pushing it out" + at + ": " + s.hip());
                    assertTrue(s.strap() > 100, "the strap carrying it" + at + ": " + s.strap());
                    assertTrue(!s.sliding(), "not sliding on the hip" + at);
                    // standing, under 2 kg-force each; walking, the pelvis tipping the gun as it goes, under 4
                    final double most = (speed > 0 ? 4 : 2) * GunStatics.G;
                    assertTrue(s.right().length() < most && s.left().length() < most,
                            "the hands holding little" + at + ": " + s.right() + ", " + s.left());
                }
            }
        }
    }
}
