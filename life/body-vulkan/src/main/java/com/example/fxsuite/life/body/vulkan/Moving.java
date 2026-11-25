package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.Move;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Walker;

/**
 * The body bench's body moving, on the card's thread: going round its stride as fast as asked, or stopped at a point
 * of it, as a {@link Motion} says; where its bones are held now, how high its soles are off the ground, and where in
 * its stride it has got to. The motion bench's walker, its body the male model's, as the bench's is.
 */
public final class Moving {

    /** How far its soles stand clear of the ground, cm, as on the motion bench. */
    private static final double SOLE = 0.5;

    private final Walker walker = new Walker(Proportions.MODEL);
    private Move move = walker.move();
    private double lift;

    /** Moved on {@code seconds}, as {@code motion} says: on round its stride if it is going, else stood where it is. */
    public void moved(final Motion motion, final double seconds) {
        if (motion.move() != move) {
            move = motion.move();
            walker.move(move);
        }
        final double v = move.marching() ? walker.marching() : motion.speed();
        if (motion.playing()) {
            walker.step(seconds * motion.slow(), v, false);
        } else {
            walker.at(motion.at(), v, false);
        }
        lift = walker.flying(v) + SOLE;
    }

    /** Where its bones are held now. */
    public Rig rig() {
        return walker.rig();
    }

    /** How high its soles are lifted off the ground, cm. */
    public double lift() {
        return lift;
    }

    /** Where in its stride it is, 0 to 1. */
    public double cycle() {
        return walker.cycle();
    }
}
