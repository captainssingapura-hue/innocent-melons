package com.example.fxsuite.life.body.bone;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.bone.foot.Talus;
import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.skull.Skull;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bones, each its own: two hundred and six of them, each where it belongs, each named once; each a closed surface;
 * the joints in the bones that turn on them; a woman's hip bone broader than a man's.
 */
class BoneTest {

    private final List<Bone> bones = Skeleton.INSTANCE.bones();

    @Test
    void twoHundredAndSixBonesEachWhereItBelongs() {
        assertEquals(206, Skeleton.INSTANCE.counted());
        final Map<Region, Integer> by = new EnumMap<>(Region.class);
        for (final Bone b : bones) {
            if (b.kind() == Kind.BONE) {
                by.merge(b.region(), b.count(), Integer::sum);
            }
        }
        assertEquals(Map.of(Region.SKULL, 22, Region.EAR, 6, Region.THROAT, 1, Region.SPINE, 26, Region.THORAX, 25,
                Region.SHOULDER, 4, Region.ARM, 6, Region.HAND, 54, Region.HIP, 2, Region.LEG, 8), without(by));
        assertEquals(52, by.get(Region.FOOT));
        assertEquals(bones.size(), bones.stream().map(Bone::name).distinct().count(), "every bone named once");
        assertEquals(bones.size(), bones.stream().distinct().count(), "every bone listed once");
    }

    @Test
    void everyBoneIsAClosedSurface() {
        for (final Bone b : bones) {
            final Mesher.Mesh m = Mesher.of(new Field(Drawn.INSTANCE.shape(b, 0).elements()), Math.min(0.6,
                    b.coarsest()));
            assertTrue(m.triangleCount() > 20, b.name() + " has a surface");
            // where two sheets pass through one cell they share its point: a few edges not shared by just two
            assertTrue(open(m) <= Math.max(4, m.triangleCount() / 200), b.name() + " is closed: " + open(m) + " open");
        }
    }

    @Test
    void jointsSitInTheBonesThatTurnOnThem() {
        // the femur's head round the hip joint, the humerus's round the shoulder's, the talus under the ankle's
        assertTrue(inside(Femur.INSTANCE, V3.ZERO));
        assertTrue(inside(Humerus.INSTANCE, V3.ZERO));
        assertTrue(inside(Talus.INSTANCE, new V3(0, -0.6, 0.3)));
        // the skull's condyles on the atlas, at the joint the head nods on
        assertTrue(inside(Skull.INSTANCE, new V3(1.3, 0.05, -0.6)));
        // the hip joint and the shoulder joint each in their socket's hollow, not in its bone
        assertFalse(inside(HipBone.INSTANCE, HipBone.JOINT));
        assertFalse(inside(Scapula.INSTANCE, Scapula.JOINT.minus(new V3(2, 45, 6.5))));
    }

    @Test
    void aWomansHipBoneIsBroader() {
        final double man = widest(HipBone.INSTANCE, 0);
        final double woman = widest(HipBone.INSTANCE, 1);
        assertTrue(woman > man + 0.5, "a woman's iliac crest " + woman + " cm out, a man's " + man);
    }

    private Map<Region, Integer> without(final Map<Region, Integer> by) {
        final Map<Region, Integer> rest = new HashMap<>(by);
        rest.remove(Region.FOOT);
        return rest;
    }

    private boolean inside(final Bone bone, final V3 at) {
        final Field f = new Field(Drawn.INSTANCE.shape(bone, 0).elements());
        return f.at(at.x(), at.y(), at.z(), IntStream.range(0, f.elements().size()).toArray()) < 0;
    }

    private double widest(final Bone bone, final double female) {
        final float[] p = Mesher.of(new Field(Drawn.INSTANCE.shape(bone, female).elements()), 0.4).points();
        double most = 0;
        for (int i = 0; i < p.length; i += 3) {
            most = Math.max(most, Math.abs(p[i]));
        }
        return most;
    }

    /** How many of {@code m}'s edges are not shared by exactly two triangles. */
    private int open(final Mesher.Mesh m) {
        final Map<Long, Integer> edges = new HashMap<>();
        final int[] t = m.triangles();
        for (int i = 0; i < t.length; i += 3) {
            for (int k = 0; k < 3; k++) {
                final long a = t[i + k];
                final long b = t[i + (k + 1) % 3];
                edges.merge(Math.min(a, b) << 32 | Math.max(a, b), 1, Integer::sum);
            }
        }
        return (int) edges.values().stream().filter(n -> n != 2).count();
    }
}
