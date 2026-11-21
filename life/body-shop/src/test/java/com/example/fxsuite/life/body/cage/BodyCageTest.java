package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Physique;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A body's cage, a man's and a woman's: one closed surface, a ball's shape however bent (each edge between two faces,
 * each way once, so all facing the same way, and out); the left the right's mirror; smoothed, a body's bulk; fitted,
 * its skin within a few millimetres of its points as drawn.
 */
class BodyCageTest {

    @Test
    void itIsOneClosedSurfaceFacingOut() {
        for (final Physique p : Physique.values()) {
            final Cage c = BodyCage.INSTANCE.drawn(p, 1);
            final Map<Long, Integer> directed = new HashMap<>();
            for (final int[] f : c.faces()) {
                for (int i = 0; i < f.length; i++) {
                    directed.merge(((long) f[i] << 32) | f[(i + 1) % f.length], 1, Integer::sum);
                }
            }
            for (final Map.Entry<Long, Integer> e : directed.entrySet()) {
                final int a = (int) (e.getKey() >> 32);
                final int b = (int) (long) e.getKey();
                assertEquals(1, e.getValue(), p + ": " + c.name(a) + " to " + c.name(b) + " twice the same way");
                assertTrue(directed.containsKey(((long) b << 32) | a), p + ": " + c.name(a) + " to " + c.name(b)
                        + " has a face on one side only");
            }
            final int edges = directed.size() / 2;
            assertEquals(2, c.size() - edges + c.faces().size(), p + ": a ball's shape");
            final Subdivision.Level l = Subdivision.INSTANCE.times(Subdivision.INSTANCE.of(c, 1), 2);
            final double v = volume(l);
            assertTrue(v > 30_000 && v < 150_000, p + ": a body's bulk, facing out: " + v + " cm3");
        }
    }

    @Test
    void itsLeftIsItsRightMirrored() {
        final Cage c = BodyCage.INSTANCE.drawn(Physique.WOMAN, 1);
        for (int i = 0; i < c.size(); i++) {
            final V3 p = c.points().get(i);
            final V3 m = new V3(-p.x(), p.y(), p.z());
            boolean found = false;
            for (final V3 q : c.points()) {
                found |= q.minus(m).length() < 1e-6;
            }
            assertTrue(found, c.name(i) + " has no fellow");
        }
    }

    @Test
    void fittedItsSkinPassesNearItsPoints() {
        final Cage drawn = BodyCage.INSTANCE.drawn(Physique.MAN, 0.8);
        final double miss = Fitting.INSTANCE.miss(Fitting.INSTANCE.fitted(drawn, 0.8), drawn, 0.8);
        assertTrue(miss < 0.5, "missed by " + miss + " cm");
    }

    private double volume(final Subdivision.Level l) {
        double v = 0;
        for (final int[] f : l.faces()) {
            for (int i = 1; i + 1 < f.length; i++) {
                v += l.point(f[0]).dot(l.point(f[i]).cross(l.point(f[i + 1]))) / 6;
            }
        }
        return v;
    }
}
