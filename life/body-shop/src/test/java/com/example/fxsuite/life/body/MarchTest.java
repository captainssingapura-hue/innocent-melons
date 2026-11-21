package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The marches as the drill has them: a step of some 75 cm at the drill's pace, carried on by the feet; the parade step's
 * leg kicked out straight, its sole level, some 25 cm up, the swinging foot clear of the ground; the forward fist before
 * the body, in toward its middle.
 */
class MarchTest {

    private static final Proportions BODY = Proportions.PAPERS;

    @Test
    void eachStepsTheDrillsPaceCarriedOnByItsFeet() {
        for (final Move m : new Move[] {Move.QUICK_MARCH, Move.PARADE_STEP}) {
            final Walker w = new Walker(BODY);
            w.move(m);
            assertEquals(75, w.stride(0) / 2, 3, m + "'s step");
            final double seconds = 6;
            for (int k = 0; k < seconds * 120; k++) {
                w.step(1.0 / 120, 0, false);
            }
            assertEquals(w.marching(), w.ahead() / seconds, w.marching() * 0.08, m + "'s speed");
        }
    }

    @Test
    void theParadeStepsLegKickedOutStraightItsSoleLevel() {
        // the right leg at its highest, the left foot down
        double highest = 0;
        double at = 0;
        for (double c = 0.6; c < 1; c += 0.01) {
            final double y = foot(rig(Move.PARADE_STEP, c), 0).stream().mapToDouble(V3::y).min().orElseThrow();
            if (y > highest) {
                highest = y;
                at = c;
            }
        }
        assertTrue(highest > 18 && highest < 32, "the foot kicked " + highest + " cm up");
        final Rig r = rig(Move.PARADE_STEP, at);
        assertTrue(r.pose().get(Part.RIGHT_SHIN, Dof.KNEE) < 6, "the knee straight");
        final List<V3> f = foot(r, 0);
        final double level = Math.toDegrees(Math.atan2(f.get(0).y() - f.get(1).y(), f.get(1).z() - f.get(0).z()));
        assertTrue(Math.abs(level) < 15, "the sole level: " + level);
        for (double c = 0.58; c < 0.98; c += 0.02) {
            for (final V3 p : foot(rig(Move.PARADE_STEP, c), 0)) {
                assertTrue(p.y() > -0.5, "the swinging foot clear at " + c + ": " + p);
            }
        }
    }

    @Test
    void theForwardFistBeforeTheBodyInTowardItsMiddle() {
        for (final Move m : new Move[] {Move.QUICK_MARCH, Move.PARADE_STEP}) {
            // the left foot striking: the right arm forward
            final Rig r = rig(m, 0.5);
            final V3 hand = r.place(Part.RIGHT_HAND, V3.ZERO);
            final V3 chest = r.frame(Part.CHEST).at();
            assertTrue(hand.z() > chest.z() + 8, m + ": the fist before the body, " + hand);
            assertTrue(Math.abs(hand.x()) < 12, m + ": in toward the middle, " + hand);
        }
    }

    private Rig rig(final Move m, final double c) {
        return new Rig(BODY, March.INSTANCE.at(m, c, 0, false));
    }

    /** The heel, the balls and the toes of the right foot ({@code 0}) or the left ({@code 5}). */
    private List<V3> foot(final Rig r, final int from) {
        return r.contacts().subList(from, from + 5);
    }
}
