package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * How an engine's crank is laid out, so it fires evenly: where each cylinder's crankpin stands round the crank, its
 * angle from straight up at rest; in line, a four's pins flat, a five's 72 degrees apart; in a V, the two banks'
 * rods side by side on one pin, a V8's crank cross-plane and a V12's six throws 120 degrees apart, but a 90 degree
 * V6's pins split, each bank's 30 degrees from the other's, so its three throws fire evenly too. And how big its
 * journals, pins and rods are, as a share of the bore: estimates.
 *
 * @param figures the engine's figures
 */
record CrankFigures(EngineFigures figures) {

    /**
     * Each throw, front to back, laid so the engine fires as its makers' does (see {@link Firing}): a four's flat, so
     * 1-3-4-2; a five's 72 degrees apart, so 1-2-4-5-3; a V6's 120, its pins split, so 1-4-3-6-2-5; a V8's
     * cross-plane, so 1-3-7-2-6-5-4-8; a V12's six 120 apart, so 1-7-5-11-3-9-6-12-2-8-4-10.
     */
    private static final List<Double> FIVE = List.of(0.0, 216.0, 144.0, 72.0, 288.0);
    private static final List<Double> FOUR = List.of(0.0, 180.0, 180.0, 0.0);
    private static final List<Double> EIGHT = List.of(0.0, 90.0, 270.0, 180.0);
    private static final List<Double> TWELVE = List.of(0.0, 240.0, 120.0, 120.0, 240.0, 0.0);
    private static final List<Double> SIX = List.of(0.0, 240.0, 120.0);
    /** How far a V6's split pins are apart, each way from their throw. */
    private static final double SPLIT = 15;

    /** How far round a main journal is, a crankpin and a gudgeon pin. */
    double main() {
        return figures.bore() * 0.32;
    }

    double pin() {
        return figures.bore() * 0.29;
    }

    double gudgeon() {
        return figures.bore() * 0.135;
    }

    /** How wide a connecting rod's big end is: in a V, as far as the banks are set apart, so two share a pin. */
    double rodWide() {
        return figures.banks() == 2 ? figures.offset() - 0.05 : 2.2;
    }

    /**
     * How far from the crank's axis what turns on it reaches: a web round its pin, or a rod's big end, its cap's bolts
     * standing out beside the pin and down past it ({@link Cylinder}).
     */
    double swing() {
        return throwing() + Math.max(pin() + 1, Math.hypot(pin() + 0.9, pin() + 0.5) + 0.35);
    }

    /** How far the crank throws a pin from its axis: half the stroke. */
    double throwing() {
        return figures.stroke() / 2;
    }

    /** Whether the V's pins are split, a throw for each bank. */
    boolean split() {
        return figures.banks() == 2 && figures.perBank() == 3 && figures.vee() == 90;
    }

    /** The angle of the pin bank {@code bank}'s cylinder {@code k} runs on, from straight up at rest, in degrees. */
    double angle(final int bank, final int k) {
        final double throwAt = throwsOf().get(k);
        if (split()) {
            return throwAt + (bank == 0 ? SPLIT : -SPLIT);
        }
        return throwAt;
    }

    /** Where that pin's middle is, under its rod, in the block's frame, at rest. */
    Vec3 pin(final int bank, final int k) {
        return pin(bank, k, 0);
    }

    /**
     * The same, the crank turned {@code turned} degrees on from rest, the way it runs: clockwise, seen from its front,
     * as the engines it is after turn, where +x is to the right.
     */
    Vec3 pin(final int bank, final int k, final double turned) {
        final double a = Math.toRadians(angle(bank, k) + turned);
        return new Vec3(throwing() * Math.sin(a), throwing() * Math.cos(a), figures.along(bank, k));
    }

    /**
     * How long the pin bank {@code bank}'s cylinder {@code k} runs on is, along the crank: its own rod's width, or,
     * where two rods share it, both; from where along it starts.
     */
    double[] span(final int bank, final int k) {
        final double w = rodWide();
        if (figures.banks() == 1 || split()) {
            return new double[]{figures.along(bank, k) - w / 2 - 0.1, figures.along(bank, k) + w / 2 + 0.1};
        }
        return new double[]{figures.along(0, k) - w / 2 - 0.1, figures.along(1, k) + w / 2 + 0.1};
    }

    private List<Double> throwsOf() {
        return switch (figures.perBank()) {
            case 3 -> SIX;
            case 4 -> figures.banks() == 1 ? FOUR : EIGHT;
            case 5 -> FIVE;
            default -> TWELVE;
        };
    }
}
