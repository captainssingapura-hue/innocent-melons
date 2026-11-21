package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Patella;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.joint.foot.Subtalar;
import com.example.fxsuite.life.body.joint.leg.Ankle;
import com.example.fxsuite.life.body.joint.leg.Hip;
import com.example.fxsuite.life.body.joint.leg.Knee;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The pelvis, the legs and the feet put together from their joints, found by their scope alone: every bone reached on
 * both sides, each at rest where it is drawn, from the sacrum or a heel; every ring closing; a knee bending its shin
 * back and its kneecap with it; the left leg the right's mirror.
 */
class LowerBodyAssemblyTest {

    private final Scope lower = Group.PELVIS.with(Group.LEGS, Group.FEET);
    private final Rig rest = new Rig(Proportions.DRAWN, Pose.REST, false);
    private final BoneAt sacrum = new BoneAt(Sacrum.INSTANCE, Side.MIDDLE);

    @Test
    void itsJointsAreFoundAndEveryBoneReachedOnEachSide() {
        final Assembly.Assembled a = build(sacrum, Posture.REST);
        assertEquals(4 + 8 + 52, a.places().size(), "the pelvis's four, two legs' four each, two feet's twenty-six");
        assertEquals(80, a.articulations());
        assertTrue(a.places().containsKey(new BoneAt(Femur.INSTANCE, Side.LEFT)));
        assertTrue(a.places().containsKey(new BoneAt(Calcaneus.INSTANCE, Side.RIGHT)));
    }

    @Test
    void atRestEachBoneIsWhereItIsDrawnAndEveryRingCloses() {
        for (final BoneAt root : new BoneAt[] {sacrum, new BoneAt(Calcaneus.INSTANCE, Side.LEFT)}) {
            final Assembly.Assembled a = build(root, Posture.REST);
            a.places().forEach((b, p) -> {
                assertEquals(0, p.at().minus(rest(b).at()).length(), 1e-9, b.said() + " where it is drawn");
                assertEquals(0, turned(p.turn()), 1e-4, b.said() + " unturned");
            });
            assertTrue(a.gaps().size() > 0, "rings: the pelvis, the shins, the ankles, the midfeet");
            assertEquals(0, a.widest(), 1e-9, "every ring closed: " + a.gaps());
        }
    }

    @Test
    void aKneeBendsItsShinBackAndItsKneecapRidesDown() {
        final Posture bent = Posture.REST.with(Knee.INSTANCE, Side.RIGHT, Movement.FLEXION, 90)
                .with(Ankle.INSTANCE, Side.RIGHT, Movement.FLEXION, 30);
        final Assembly.Assembled a = build(sacrum, bent);
        final V3 shin = a.places().get(new BoneAt(Tibia.INSTANCE, Side.RIGHT)).turn().apply(new V3(0, -1, 0));
        assertEquals(0, shin.minus(new V3(0, 0, -1)).length(), 1e-9, "the shin swung back level");
        assertEquals(67.5, turned(a.places().get(new BoneAt(Patella.INSTANCE, Side.RIGHT)).turn()), 1e-6);
        assertEquals(0, a.widest(), 1e-9, "the ankle's ring closes, bent: " + a.gaps());
    }

    @Test
    void theLeftLegIsTheRightsMirror() {
        Posture swung = Posture.REST;
        for (final Side s : new Side[] {Side.RIGHT, Side.LEFT}) {
            swung = swung.with(Hip.INSTANCE, s, Movement.FLEXION, 30).with(Hip.INSTANCE, s, Movement.SIDE_BEND, 20)
                    .with(Hip.INSTANCE, s, Movement.TWIST, 15);
        }
        final Map<BoneAt, Place> p = build(sacrum, swung).places();
        final Place right = p.get(new BoneAt(Femur.INSTANCE, Side.RIGHT));
        final Place left = p.get(new BoneAt(Femur.INSTANCE, Side.LEFT));
        assertEquals(right.at().x(), -left.at().x(), 1e-9);
        final V3 r = right.turn().apply(new V3(0, -1, 0));
        final V3 l = left.turn().apply(new V3(0, -1, 0));
        assertEquals(0, new V3(-r.x(), r.y(), r.z()).minus(l).length(), 1e-9, "the thighs swung alike, out each side");
        assertTrue(r.x() > 0 && l.x() < 0, "out from the body, both");
        assertTrue(r.z() > 0, "forward");
    }

    @Test
    void anInvertedHeelOpensTheMidfootsRingWhichIsToldNotMended() {
        final Posture inverted = Posture.REST.with(Subtalar.INSTANCE, Side.RIGHT, Movement.FLEXION, 15);
        final Assembly.Assembled a = build(sacrum, inverted);
        assertTrue(a.widest() > 0.05, "the heel bone turned, the midfoot not following");
        assertTrue(a.gaps().stream().filter(g -> g.apart() > 1e-6).allMatch(g -> g.joint().contains("right")));
    }

    private Assembly.Assembled build(final BoneAt root, final Posture posture) {
        return Assembly.INSTANCE.build(lower, root, rest(root), posture);
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
