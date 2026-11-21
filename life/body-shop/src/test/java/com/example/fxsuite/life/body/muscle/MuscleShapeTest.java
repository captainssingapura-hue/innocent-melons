package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.BoundPose;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The muscles' shapes: every muscle on either side a closed surface facing out, of some bulk; the left the right's
 * mirror; each named once; each in the region of the body its bones are in.
 */
class MuscleShapeTest {

    private final Rig rig = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);

    @Test
    void everyMuscleIsAClosedSurfaceFacingOutEitherSide() {
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            for (final boolean left : new boolean[] {false, true}) {
                final double v = volume(MuscleShape.INSTANCE.mesh(m, rig, left, 1));
                assertTrue(v > 0, m.name() + (left ? " left" : " right") + " faces in: " + v + " cm3");
            }
        }
    }

    @Test
    void theLeftIsTheRightMirrored() {
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            for (final Section s : m.sections()) {
                final V3 r = s.at().in(rig, false);
                final V3 l = s.at().in(rig, true);
                assertEquals(0, l.minus(new V3(-r.x(), r.y(), r.z())).length(), 1e-6, m.name());
            }
        }
    }

    @Test
    void eachIsNamedOnceAndLiesWhereItsRegionSays() {
        final Set<String> names = new HashSet<>();
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            assertTrue(names.add(m.name()), m.name() + " twice");
            final double x = m.sections().stream().mapToDouble(s -> s.at().in(rig, false).x()).average().orElseThrow();
            final double y = m.sections().stream().mapToDouble(s -> s.at().in(rig, false).y()).average().orElseThrow();
            final boolean there = switch (m.region()) {
                case HEAD -> y > 56;
                case NECK -> y > 46 && y < 64;
                case CHEST, ABDOMEN, BACK -> y > 0 && y < 60 && x < 16;
                case SHOULDER, UPPER_ARM, FOREARM, HAND -> x > 10 && y > -15;
                case HIP, THIGH, LOWER_LEG, FOOT -> y < 12;
            };
            assertTrue(there, m.name() + " at " + x + ", " + y);
        }
    }

    private double volume(final Mesher.Mesh mesh) {
        final float[] p = mesh.points();
        final int[] t = mesh.triangles();
        double v = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = new V3(p[3 * t[i]], p[3 * t[i] + 1], p[3 * t[i] + 2]);
            final V3 b = new V3(p[3 * t[i + 1]], p[3 * t[i + 1] + 1], p[3 * t[i + 1] + 2]);
            final V3 c = new V3(p[3 * t[i + 2]], p[3 * t[i + 2] + 1], p[3 * t[i + 2] + 2]);
            v += a.dot(b.cross(c)) / 6;
        }
        return v;
    }
}
