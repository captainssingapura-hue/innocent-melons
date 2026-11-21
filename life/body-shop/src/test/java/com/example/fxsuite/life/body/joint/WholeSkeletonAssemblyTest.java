package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Skeleton;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.hand.Capitate;
import com.example.fxsuite.life.body.bone.hand.LittleFingerDistalPhalanx;
import com.example.fxsuite.life.body.bone.skull.Hyoid;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.joint.arm.Elbow;
import com.example.fxsuite.life.body.joint.arm.Glenohumeral;
import com.example.fxsuite.life.body.joint.hand.Wrist;
import com.example.fxsuite.life.body.joint.skull.Temporomandibular;
import com.example.fxsuite.life.body.joint.thorax.FifthCostovertebral;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The whole skeleton put together from its joints, every group named: every bone reached on each side but the hyoid,
 * which hangs by muscle alone; each at rest where it is drawn, from the sacrum, a fingertip or the jaw; every ring
 * closing; an elbow bending and its forearm twisting, its bones meeting still; a wrist bending toward the palm; the jaw
 * opening on both its joints at once; the left arm the right's mirror; a rib raised, its ring told open; every posture
 * to start from closing its rings, but the ribs' where the spine bends under them.
 */
class WholeSkeletonAssemblyTest {

    private final Scope all = Group.SKULL.with(Group.values());
    private final Rig rest = new Rig(Proportions.DRAWN, Pose.REST.unturned(), false);
    private final BoneAt sacrum = new BoneAt(Sacrum.INSTANCE, Side.MIDDLE);

    @Test
    void everyBoneIsReachedOnEachSideButTheHyoid() {
        final Assembly.Assembled a = build(sacrum, Posture.REST);
        final int bones = Skeleton.INSTANCE.bones().stream().mapToInt(b -> b.paired() ? 2 : 1).sum();
        assertEquals(bones - 1, a.places().size(), "every bone on each side but the hyoid");
        assertFalse(a.places().containsKey(new BoneAt(Hyoid.INSTANCE, Side.MIDDLE)), "hung by muscle alone");
        assertTrue(a.places().containsKey(new BoneAt(LittleFingerDistalPhalanx.INSTANCE, Side.LEFT)));
    }

    @Test
    void atRestEachBoneIsWhereItIsDrawnAndEveryRingCloses() {
        for (final BoneAt root : new BoneAt[] {sacrum, new BoneAt(LittleFingerDistalPhalanx.INSTANCE, Side.LEFT),
            new BoneAt(Mandible.INSTANCE, Side.MIDDLE)}) {
            final Assembly.Assembled a = build(root, Posture.REST);
            a.places().forEach((b, p) -> {
                assertEquals(0, p.at().minus(rest(b).at()).length(), 1e-9,
                        b.said() + " where it is drawn, from the " + root.said());
                assertEquals(0, turned(p.turn()), 1e-4, b.said() + " unturned");
            });
            assertEquals(0, a.widest(), 1e-9, "every ring closed: " + a.gaps());
        }
    }

    @Test
    void anElbowBendsAndItsForearmTwistsItsBonesMeetingStill() {
        final Posture p = Posture.REST.with(Elbow.INSTANCE, Side.RIGHT, Movement.FLEXION, 90)
                .with(Elbow.INSTANCE, Side.RIGHT, Movement.TWIST, 90);
        final Map<BoneAt, Place> at = build(sacrum, p).places();
        final V3 ulna = at.get(new BoneAt(Ulna.INSTANCE, Side.RIGHT)).turn().apply(new V3(0, -1, 0));
        assertTrue(ulna.z() > 0.95, "the forearm bent forward, level: " + ulna);
        final Rot hand = at.get(new BoneAt(Capitate.INSTANCE, Side.RIGHT)).turn();
        final Rot forearm = at.get(new BoneAt(Ulna.INSTANCE, Side.RIGHT)).turn();
        final V3 palm = forearm.back().then(hand).apply(V3.Z);
        assertTrue(palm.x() < -0.9, "the palm turned in, pronated, as against the ulna: " + palm);
        assertEquals(0, build(sacrum, p).widest(), 1e-9, "the elbow's and the forearm's rings close");
    }

    @Test
    void aWristBendsTowardThePalm() {
        final Posture p = Posture.REST.with(Wrist.INSTANCE, Side.RIGHT, Movement.FLEXION, 60);
        final Assembly.Assembled a = build(sacrum, p);
        final V3 down = a.places().get(new BoneAt(Capitate.INSTANCE, Side.RIGHT)).turn().apply(new V3(0, -1, 0));
        assertTrue(down.z() > 0.8, "the hand turned toward the palm: " + down);
        assertEquals(0, a.widest(), 1e-9, "the wrist's ring closes: " + a.gaps());
    }

    @Test
    void theJawOpensOnBothItsJointsAtOnce() {
        final Posture p = Posture.REST.with(Temporomandibular.INSTANCE, Side.MIDDLE, Movement.FLEXION, 25);
        final Assembly.Assembled a = build(sacrum, p);
        final Place jaw = a.places().get(new BoneAt(Mandible.INSTANCE, Side.MIDDLE));
        final V3 chin = jaw.apply(new V3(0, -4.9, 8.3));
        final V3 was = rest(new BoneAt(Mandible.INSTANCE, Side.MIDDLE)).apply(new V3(0, -4.9, 8.3));
        assertTrue(chin.y() < was.y() - 2.5, "the chin dropped: " + chin + " from " + was);
        assertEquals(0, a.widest(), 1e-9, "both its joints meet: " + a.gaps());
    }

    @Test
    void theLeftArmIsTheRightsMirror() {
        Posture raised = Posture.REST;
        for (final Side s : new Side[] {Side.RIGHT, Side.LEFT}) {
            raised = raised.with(Glenohumeral.INSTANCE, s, Movement.SIDE_BEND, 80)
                    .with(Glenohumeral.INSTANCE, s, Movement.FLEXION, 20).with(Elbow.INSTANCE, s, Movement.FLEXION, 45);
        }
        final Map<BoneAt, Place> p = build(sacrum, raised).places();
        for (final Bone b : new Bone[] {Humerus.INSTANCE, Radius.INSTANCE, Capitate.INSTANCE}) {
            final Place right = p.get(new BoneAt(b, Side.RIGHT));
            final Place left = p.get(new BoneAt(b, Side.LEFT));
            assertEquals(right.at().x(), -left.at().x(), 1e-9, b.name());
            final V3 r = right.turn().apply(new V3(0, -1, 0));
            final V3 l = left.turn().apply(new V3(0, -1, 0));
            assertEquals(0, new V3(-r.x(), r.y(), r.z()).minus(l).length(), 1e-9, b.name() + " alike");
        }
        final V3 arm = p.get(new BoneAt(Humerus.INSTANCE, Side.RIGHT)).turn().apply(new V3(0, -1, 0));
        assertTrue(arm.x() > 0.5, "raised out to the side: " + arm);
    }

    @Test
    void aRibRaisedOpensItsRingWhichIsToldNotMended() {
        final Posture breath = Posture.REST.with(FifthCostovertebral.INSTANCE, Side.RIGHT, Movement.FLEXION, 8);
        final Assembly.Assembled a = build(sacrum, breath);
        assertTrue(a.widest() > 0.1, "the rib turned, its cartilage not following");
    }

    @Test
    void everyPostureToStartFromClosesItsRingsButWhereTheSpineBendsUnderTheRibs() {
        final BoneAt fingertip = new BoneAt(LittleFingerDistalPhalanx.INSTANCE, Side.LEFT);
        for (final Postures.Named named : Postures.INSTANCE.all()) {
            final Assembly.Assembled a = build(sacrum, named.posture());
            final boolean bent = named.name().equals("A squat") || named.name().equals("Bent forward");
            assertEquals(bent, a.widest() > 0.1, named.name() + ": " + a.widest());
            final List<String> open = open(a);
            assertTrue(open.stream().allMatch(g -> g.contains("costochondral")),
                    named.name() + ": only a rib on its cartilage, a side's cartilages one piece: " + open);
            assertEquals(open, open(build(fingertip, named.posture())), "the same, held by a fingertip");
        }
    }

    /** The joints {@code a} did not close, in order. */
    private List<String> open(final Assembly.Assembled a) {
        return a.gaps().stream().filter(g -> g.apart() > 1e-6).map(Assembly.Gap::joint).sorted().toList();
    }

    private Assembly.Assembled build(final BoneAt root, final Posture posture) {
        return Assembly.INSTANCE.build(all, root, rest(root), posture);
    }

    /** Where {@code b} is drawn at rest: where its part stands, a left limb's on the left. */
    private Place rest(final BoneAt b) {
        final Bone bone = b.bone();
        final Part part = b.side() == Side.LEFT && bone.part().side() != 0 ? bone.part().other() : bone.part();
        return new Place(rest.frame(part).at(), Rot.NONE);
    }

    private double turned(final Rot r) {
        final double cos = Math.clamp((r.xx() + r.yy() + r.zz() - 1) / 2, -1, 1);
        return Math.toDegrees(Math.acos(cos));
    }
}
