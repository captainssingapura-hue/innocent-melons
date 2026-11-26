package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.bench.Roamer;
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
import melons.innocent.glm.model.Santana;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A GLM car to be test driven wherever a walk goes, on a test track or anywhere else: the retro saloon on its own
 * steel shell if {@code santana}, the large SUV if not; put together, its engine in its bay, seen in its own
 * finishes, its wheels steered and turning, its lamps lit as it is driven; to be moved by the walk from wherever it is
 * started. As the run says: {@code -Dglm.engine=} the engine in it, L4, L5, V6, V8 or V12, or none, the V6 unless;
 * {@code -Dbench.hidden=} groups not shown; {@code -Dglm.view=} the view to begin in, CHASE_FAR unless; kept quiet in
 * a run that ends by itself, is filmed or has its time set.
 *
 * @param santana whether it is the retro saloon
 */
public record GlmDrive(boolean santana) {

    /** How long the retro saloon is, bumper to bumper, made larger as it is. */
    private static final double SANTANA_LENGTH = 482;

    /** The car the run asks for: the retro saloon if it says {@code -Dglm.car=santana}, the large SUV if not. */
    public GlmDrive() {
        this("santana".equalsIgnoreCase(System.getProperty("glm.car", "")));
    }

    /** How the avatar engine knows it. */
    public GlmChassis chassis() {
        return santana ? new GlmChassis(proportions(), SANTANA_LENGTH) : new GlmChassis(proportions());
    }

    /** It, for a walk to move, started at {@code start}, standing as its chassis rides there. */
    public Roamer roamer(final Place start) {
        final Santana car = new Santana();
        final Optional<EngineFigures> engine = engine();
        final EngineBay bay = engine.map(e -> new EngineBay(e, true)).orElse(null);
        final Assembled built = new Assembly((santana ? car.platform(bay)
                : new GlmPlatform(proportions(), new BodyFigures(), bay)).platform()).assembled();
        final DriveViews.View view = DriveViews.View.valueOf(System.getProperty("glm.view", "CHASE_FAR"));
        final Finishes finishes = santana ? car.finishes() : new Finishes();
        return new TestDriving(chassis(), built, new OnShow(finishes.coats(), hidden(), new MadeOf()),
                new Wheeling(proportions()), engine, quiet(), view, start);
    }

    private Proportions proportions() {
        return santana ? new Santana().proportions() : new Proportions();
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
