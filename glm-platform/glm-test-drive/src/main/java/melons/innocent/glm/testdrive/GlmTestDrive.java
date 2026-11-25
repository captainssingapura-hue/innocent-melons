package melons.innocent.glm.testdrive;

import melons.innocent.glm.bench.OnShow;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.engine.EngineRange;
import melons.innocent.glm.model.BodyFigures;
import melons.innocent.glm.model.EngineBay;
import melons.innocent.glm.model.Finishes;
import melons.innocent.glm.model.GlmPlatform;
import melons.innocent.glm.model.MadeOf;
import melons.innocent.glm.model.Proportions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The GLM car test driven on the test track ({@link TestTrack}): the bare vehicle, its engine in its bay and covered,
 * its front wheels steering and every wheel turning, its engine heard but in a run that ends by itself, is filmed or
 * has its time set, driven from the keyboard by the avatar engine's own land vehicle, its test engine
 * ({@link TestDriving}), in the
 * walks' windows, with their sky, shadows, kept runs, scripts and films. {@code -Dglm.engine=} the engine in it, L4,
 * L5, V6, V8 or V12, or none, the V6 unless; {@code -Dbench.hidden=} groups not shown, UNDERBODY for the body, say.
 */
public final class GlmTestDrive {

    private static final String TITLE = "The GLM car, test driven";

    public static void main(final String[] args) throws IOException {
        new GlmTestDrive().run();
    }

    private void run() throws IOException {
        final Proportions proportions = new Proportions();
        final GlmChassis chassis = new GlmChassis(proportions);
        final Optional<EngineFigures> engine = engine();
        final Assembled built = new Assembly(new GlmPlatform(proportions, new BodyFigures(),
                engine.map(e -> new EngineBay(e, true)).orElse(null)).platform()).assembled();
        final TestTrack track = new TestTrack();
        track.kit(TITLE).walk(new TestDriving(chassis, built, new OnShow(new Finishes().coats(), hidden(),
                new MadeOf()), new Wheeling(proportions), engine, quiet(), track.start(chassis.ride()))).run();
    }

    /** The engine in its bay, as the run says, the V6 unless; or none. */
    private Optional<EngineFigures> engine() {
        final String called = System.getProperty("glm.engine", "V6");
        return new EngineRange().all().stream().filter(e -> e.name().equalsIgnoreCase(called)).findFirst();
    }

    /** Whether nobody is listening: a run that ends by itself, is filmed, or has its time set. */
    private boolean quiet() {
        return !System.getProperty("bench.settle", "").isBlank() || !System.getProperty("bench.script", "").isBlank()
                || !System.getProperty("bench.film", "").isBlank();
    }

    /** The groups the run says not to show. */
    private Set<Group> hidden() {
        final String told = System.getProperty("bench.hidden", "");
        return told.isBlank() ? Set.of() : Arrays.stream(told.split(",")).map(String::strip).map(Group::valueOf)
                .collect(Collectors.toSet());
    }
}
