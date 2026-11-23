package com.example.fxsuite.vulkan.bench.play;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * What one step of a walk is told, whoever tells it: the keys held through it, by the engine's names for them;
 * those that went down in it, once; how far the avatar is turned in it and the view tilted, in degrees; whether
 * the avatar takes off or lands on its jets in it; and what the chase camera does in it: swung round (1 for E, -1
 * for Q, nothing for neither), brought nearer or further by so many notches of the wheel, or put back behind.
 *
 * <p>Everything the simulation is told, and everything that moves the chase camera, is here, so that a walk told
 * the same steps again is the same walk, step for step, whether a script told them, someone playing, or a run kept
 * and read back.
 */
public record Step(Set<String> held, List<String> pressed, double turn, double tilt, boolean flies, int circling,
                   double scrolled, boolean behind) {

    public Step {
        held = Set.copyOf(new TreeSet<>(held));
        pressed = List.copyOf(pressed);
        if (circling < -1 || circling > 1) {
            throw new IllegalArgumentException("the view swings one way, the other, or not at all: " + circling);
        }
    }

    /** A step in which nothing is held, pressed, turned or done. */
    public Step() {
        this(Set.of(), List.of(), 0, 0, false, 0, 0, false);
    }

    /**
     * Whether a step told this after {@code before} has to be said again to be told the same: it differs in what
     * goes on (what is held, the turn, the tilt, the swing), or something is done in it once.
     */
    public boolean saidAfter(final Step before) {
        return !held.equals(before.held) || turn != before.turn || tilt != before.tilt
                || circling != before.circling || !pressed.isEmpty() || flies || scrolled != 0 || behind;
    }

    /** What goes on into the step after it, with nothing done again that was done once. */
    public Step goingOn() {
        return new Step(held, List.of(), turn, tilt, false, circling, 0, false);
    }
}
