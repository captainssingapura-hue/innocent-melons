package melons.innocent.glm.enginebench;

import com.example.fxsuite.avatar.sound.Engine;
import com.example.fxsuite.avatar.sound.Exhaust;
import com.example.fxsuite.avatar.sound.Turbo;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.engine.Firing;

import java.util.List;
import java.util.Map;

/**
 * The GLM engines' own voices, as the engines' voice hears an engine: each cylinder firing when and in the order its
 * crank and cam make it ({@link Firing}), into its own bank's pipe; and how each idles, revs and rings, after the
 * engines it is after, as estimates: the four after the 2.0 litre turbocharged four, the five after the 2.5 litre
 * five, the V6 after the 2.9 and 3.0 litre turbocharged V6, the V8 after the 4.0 litre twin turbo V8, its crank
 * cross-plane so each bank's pulses come unevenly, and the V12, after none, its two banks even sixes. Their turbos
 * sit in the V, fed by both banks, so a V's pipes are mixed a little. It revs as quickly as what turns in it is light:
 * of all that turns, the crank, rods and pistons are taken as some two fifths, the rest the flywheel and all the
 * engine drives, so in titanium it revs up some fifth quicker than in steel.
 */
public final class EngineVoices {

    /** How much of all that turns is the crank, rods and pistons. */
    private static final double TURNING = 0.4;

    /** How each sounds, by its name in the range. */
    private static final Map<String, Tone> TONES = Map.of(
            "L4", new Tone(720, 6500, 8500, 5000, new double[] {190, 520, 1350}, 3.5, 3.2, 1.6, 520, 1),
            "L5", new Tone(780, 7000, 8200, 5000, new double[] {125, 340, 950}, 3.5, 3.4, 1.5, 480, 1),
            "V6", new Tone(650, 6700, 7600, 4800, new double[] {150, 410, 1100}, 3.5, 3.6, 1.4, 430, 0.85),
            "V8", new Tone(600, 6800, 7000, 4500, new double[] {100, 280, 760}, 3, 4.0, 1.2, 360, 0.55),
            "V12", new Tone(650, 6800, 7200, 4600, new double[] {130, 360, 980}, 3.5, 3.2, 1.3, 420, 0.9));

    /** {@code figures}' voice, what turns in it made of {@code made}, turbocharged if {@code turbo}. */
    public Engine voiced(final EngineFigures figures, final Material made, final boolean turbo) {
        final Tone tone = TONES.getOrDefault(figures.name(), TONES.get("V6"));
        final Firing firing = new Firing(figures);
        final List<Integer> order = firing.order();
        final int all = order.size();
        final double[] when = new double[all];
        final int[] bank = new int[all];
        final int[] fired = new int[all];
        final double[] runners = new double[all];
        final int first = order.getFirst();
        final double from = firing.fires(firing.bank(first), firing.k(first));
        for (int i = 0; i < all; i++) {
            final int n = order.get(i);
            when[i] = (firing.fires(firing.bank(n), firing.k(n)) - from + Firing.CYCLE) % Firing.CYCLE;
            bank[i] = firing.bank(n);
            fired[i] = n;
            runners[n - 1] = runner(figures, firing.k(n));
        }
        final double light = 1 - TURNING + TURNING * made.density() / Material.STEEL.density();
        final Engine voice = new Engine("GLM_" + figures.name(), "GLM " + figures.name(),
                "Fires " + firing.written() + ", every " + Math.round(Firing.CYCLE / all) + " degrees of crank.", 4,
                when, bank, tone.far(), tone.idle(), tone.redline(), tone.climb() / light, tone.fall() / light,
                tone.pipes(), tone.ring(), tone.pulse(), tone.rasp(), tone.intake())
                .piped(new Exhaust(fired, runners, figures.banks() == 2 ? Exhaust.H_PIPE : Exhaust.NONE));
        return turbo ? voice.with(Turbo.STANDARD) : voice;
    }

    /**
     * How long cylinder {@code k}'s runner is, to its bank's collector: in line, the head's own manifold, the end
     * cylinders' longest; in a V, short, down into the V's turbo, the front's and the back's a little longer.
     */
    private double runner(final EngineFigures figures, final int k) {
        final double middle = (figures.perBank() - 1) / 2.0;
        final double out = Math.abs(k - middle) / Math.max(1, middle);
        return figures.banks() == 1 ? 0.32 + 0.22 * out : 0.30 + 0.08 * out;
    }

    /**
     * How an engine idles, revs and rings: its idle, red line, how fast it revs up free and falls back, its pipes'
     * notes, how long they ring, its pulse, its rasp, its intake's note, and how loud its second bank is.
     */
    private record Tone(double idle, double redline, double climb, double fall, double[] pipes, double ring,
                        double pulse, double rasp, double intake, double far) {
    }
}
