package melons.innocent.testkit.loose;

/**
 * How long a figure's stride is at a speed, for drawing its legs: one full cycle, two steps, in centimetres, longer
 * the faster it goes. The figures are the old engine's, whose gait the bot's limbs are still drawn from; a gait of
 * the core's own replaces both.
 */
record Paced() {

    private static final double SHORT = 150;
    private static final double LONGER = 110;
    private static final double RUNNING = 760;

    /** The length of a full stride at so many centimetres a second. */
    double stride(final double speed) {
        return SHORT + LONGER * Math.min(1, Math.abs(speed) / RUNNING);
    }
}
