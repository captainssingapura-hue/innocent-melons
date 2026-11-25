package melons.innocent.glm.engine;

/**
 * When a cylinder's valves open and how far, through its four strokes, counted in degrees of crank from its firing,
 * at the top of its compression: the exhaust opening 50 degrees before the bottom of the power stroke and shutting 10
 * after the top, the inlet opening 5 before that top and shutting 50 after the bottom of the inlet stroke, as a
 * turbocharged engine's cams are timed, estimates; each lifting its valve an eighth of the bore at the most, smoothly
 * up and down again, as a cam lifts it, its most half way.
 *
 * @param figures the engine's figures
 */
record ValveTiming(EngineFigures figures) {

    private static final double INLET_OPENS = 355;
    private static final double INLET_SHUTS = 590;
    private static final double EXHAUST_OPENS = 130;
    private static final double EXHAUST_SHUTS = 370;

    /** How far a valve is lifted at the most. */
    double most() {
        return figures.bore() * 0.125;
    }

    /** How far through the cycle from firing an inlet's or an exhaust's valve is lifted the most. */
    double peak(final boolean inlet) {
        return inlet ? (INLET_OPENS + INLET_SHUTS) / 2 : (EXHAUST_OPENS + EXHAUST_SHUTS) / 2;
    }

    /** How long an inlet's or an exhaust's valve is open, in degrees of crank. */
    double open(final boolean inlet) {
        return inlet ? INLET_SHUTS - INLET_OPENS : EXHAUST_SHUTS - EXHAUST_OPENS;
    }

    /** How far an inlet's or an exhaust's valve is lifted {@code since} degrees of crank after its cylinder fired. */
    double lift(final boolean inlet, final double since) {
        return lifted(inlet, since - peak(inlet));
    }

    /** How far it is lifted {@code from} degrees of crank on from when it is lifted the most, either way. */
    double lifted(final boolean inlet, final double from) {
        final double d = ((from % Firing.CYCLE) + Firing.CYCLE * 1.5) % Firing.CYCLE - Firing.CYCLE / 2;
        final double half = open(inlet) / 2;
        return Math.abs(d) >= half ? 0 : most() * (1 + Math.cos(Math.PI * d / half)) / 2;
    }
}
