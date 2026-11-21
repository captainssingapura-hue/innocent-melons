package com.example.fxsuite.life.body;

/**
 * How a humanoid goes: walking and running, as fast as asked ({@link Gait}); or marching in step, a drill's, as fast as
 * its steps come and as long as they are ({@link March}): the quick march, 齐步, some 119 steps a minute; the parade
 * step, 正步, some 112, each leg kicked out straight. The marches' paces the drill's, 75 cm a step.
 */
public enum Move {
    WALK("Walking and running", 0, 0),
    QUICK_MARCH("Quick march (齐步)", 119, 0.6),
    PARADE_STEP("Parade step (正步)", 112, 0.56);

    private final String label;
    private final double steps;
    private final double stance;

    Move(final String label, final double steps, final double stance) {
        this.label = label;
        this.steps = steps;
        this.stance = stance;
    }

    public String label() {
        return label;
    }

    /** Whether it is a march, in step, at its own pace whatever is asked. */
    public boolean marching() {
        return this != WALK;
    }

    /** Strides a second, a march's: two steps to a stride. */
    public double cadence() {
        return steps / 120;
    }

    /** How much of a stride each foot is down, a march's. */
    public double stance() {
        return stance;
    }

    @Override
    public String toString() {
        return label;
    }
}
