package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Puppet;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A man's and a woman's flesh sculpted over their bones: each tube a closed surface facing out, its rings' middles
 * inside it; a spoke held on
 * the bone, the knee's front, reaching just beyond the kneecap.
 */
class SculptorTest {

    private final Detail detail = new Detail(1.0);

    @Test
    void eachLimbIsClosedFacingOutItsBonesWithin() {
        for (final Physique who : Physique.values()) {
            for (final Tube t : who.tubes()) {
                final Mesher.Mesh m = Sculptor.INSTANCE.mesh(t, detail, who.female());
                final String said = who.said() + ": " + t.said();
                assertTrue(volume(m) > 100, said + " faces out, enclosing " + volume(m) + " cm3");
                for (final Ring r : t.rings()) {
                    assertTrue(inside(m, twisted(r)), said + ": " + r.said() + "'s middle within");
                }
            }
        }
    }

    @Test
    void theKneesFrontIsJustBeyondTheKneecap() {
        final Tube thigh = Physique.MAN.on(Part.RIGHT_THIGH).orElseThrow();
        final Ring knee = thigh.rings().getLast();
        final Field.Shape bones = PartBones.INSTANCE.shape(Part.RIGHT_THIGH);
        final Mesher.Mesh m = Sculptor.INSTANCE.mesh(thigh, detail, 0);
        double front = -1e9;
        final float[] p = m.points();
        for (int i = 0; i < p.length; i += 3) {
            if (Math.abs(p[i + 1] - knee.middle().y()) < 0.5 && Math.abs(p[i] - knee.middle().x()) < 0.5) {
                front = Math.max(front, p[i + 2]);
            }
        }
        double bone = knee.middle().z();
        for (double z = knee.middle().z(); z < knee.middle().z() + 15; z += 0.05) {
            if (bones.at(knee.middle().x(), knee.middle().y(), z) < 0) {
                bone = z;
            }
        }
        assertEquals(knee.spokes().getFirst().reach(), front - bone, 0.35, "skin over the kneecap");
    }

    /** {@code r}'s middle, twisted as far as the forearm is at rest, as its tube is. */
    private V3 twisted(final Ring r) {
        return Rig.twisted(r.middle(), Puppet.TWIST, r.twist());
    }

    /** The volume {@code m} encloses, positive if it faces out. */
    private double volume(final Mesher.Mesh m) {
        final float[] p = m.points();
        final int[] t = m.triangles();
        double v = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = at(p, t[i]);
            final V3 b = at(p, t[i + 1]);
            final V3 c = at(p, t[i + 2]);
            v += a.dot(b.cross(c)) / 6;
        }
        return v;
    }

    /**
     * Whether {@code q} is within {@code m}: a ray from it ahead crossing its surface an odd number of times; from a
     * hair off it, so as not to run along an edge down the middle.
     */
    private boolean inside(final Mesher.Mesh m, final V3 at) {
        final V3 q = at.plus(new V3(0.0137, 0.0071, 0));
        final float[] p = m.points();
        final int[] t = m.triangles();
        int crossed = 0;
        for (int i = 0; i < t.length; i += 3) {
            if (crosses(q, at(p, t[i]), at(p, t[i + 1]), at(p, t[i + 2]))) {
                crossed++;
            }
        }
        return crossed % 2 == 1;
    }

    /** Whether a ray from {@code q} along z crosses the triangle {@code a b c}. */
    private boolean crosses(final V3 q, final V3 a, final V3 b, final V3 c) {
        final double d = (b.x() - a.x()) * (c.y() - a.y()) - (c.x() - a.x()) * (b.y() - a.y());
        if (Math.abs(d) < 1e-12) {
            return false;
        }
        final double u = ((q.x() - a.x()) * (c.y() - a.y()) - (c.x() - a.x()) * (q.y() - a.y())) / d;
        final double v = ((b.x() - a.x()) * (q.y() - a.y()) - (q.x() - a.x()) * (b.y() - a.y())) / d;
        if (u < 0 || v < 0 || u + v > 1) {
            return false;
        }
        return a.z() + u * (b.z() - a.z()) + v * (c.z() - a.z()) > q.z();
    }

    private V3 at(final float[] p, final int i) {
        return new V3(p[3 * i], p[3 * i + 1], p[3 * i + 2]);
    }
}
