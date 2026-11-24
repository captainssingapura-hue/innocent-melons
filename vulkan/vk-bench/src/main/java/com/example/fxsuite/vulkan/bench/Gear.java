package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Body;

/**
 * The gears of the avatar's jets, each the fastest it flies in it: from a walking pace over the trees to a fast
 * road's. The engine flies a body at so much of its own top speed as it is told to go ahead, and does not mind
 * being told more than all of it, so a gear is only how hard the keys are taken to push: the simulation is as it
 * is. The third is about the engine's own top speed, and is the gear a walk begins in.
 */
public enum Gear {
    FIRST(20),
    SECOND(40),
    THIRD(70),
    FOURTH(100),
    FIFTH(150);

    /** A kilometre an hour, in centimetres a second. */
    private static final double KMH = 100_000.0 / 3_600;

    private final double fastest;

    Gear(final double fastest) {
        this.fastest = fastest;
    }

    /** How fast it flies at most, in kilometres an hour. */
    public double fastest() {
        return fastest;
    }

    /** How hard the engine is told to go, for going flat out in this gear: so much of its own top speed. */
    public double pushed() {
        return fastest * KMH / Body.BOOST;
    }

    /** The gear above this, or this if it is the top one. */
    public Gear higher() {
        return values()[Math.min(ordinal() + 1, values().length - 1)];
    }

    /** The gear below this, or this if it is the bottom one. */
    public Gear lower() {
        return values()[Math.max(ordinal() - 1, 0)];
    }

    /** So many centimetres a second, in kilometres an hour. */
    public double hourly(final double speed) {
        return speed / KMH;
    }
}
