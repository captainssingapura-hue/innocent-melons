package melons.innocent.glm.bench;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Dial;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
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

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * The GLM platform as a place to walk round, as an avatar: the car at its own size, the walk counting in centimetres
 * as it does, standing on the walk's ground a few metres ahead of where the avatar starts, its front away, a little
 * to the right; in its way as a box as long, wide and high as the car, so it is walked round, or climbed onto. Three
 * choices in the walk's window: which engine is in its bay, or none; its engine's covers on or off; and its body shown
 * or not, to see in. Choosing another engine puts the car on the card afresh. Closed, it is gone from the card.
 */
public final class GlmPlace implements Place {

    /** Where the car's own nought, its front axle's middle, stands in the walk, which counts in centimetres too. */

    private static final Vec3 STANDS = new Vec3(120, 0, 800);
    private static final List<String> ENGINES = List.of("L4", "L5", "V6", "V8", "V12", "None");

    private final Card card;
    private final Laid laid;
    private final Dial engine;
    private final Dial covers = new Dial("Engine covers", List.of("On", "Off"), 0);
    private final Dial body = new Dial("Body", List.of("Shown", "Hidden"), 0);
    private int showing = -1;
    private PlatformDrawn drawn;
    private Assembled built;
    private boolean moved;

    /** The car, on {@code card}, its layouts as {@code laid} says, its engine as the run says, the V6 unless. */
    public GlmPlace(final Card card, final Laid laid) {
        this.card = card;
        this.laid = laid;
        final String told = System.getProperty("glm.engine", "V6").toUpperCase(Locale.ROOT);
        this.engine = new Dial("Engine", ENGINES, Math.max(0, ENGINES.stream().map(e -> e.toUpperCase(Locale.ROOT))
                .toList().indexOf(told)));
        put();
    }

    @Override
    public List<Dial> dials() {
        return List.of(engine, covers, body);
    }

    @Override
    public void readied(final Camera camera) {
        if (engine.step() != showing) {
            put();
            moved = true;
        }
    }

    @Override
    public Optional<Obstacles> rearranged() {
        if (!moved) {
            return Optional.empty();
        }
        moved = false;
        return Optional.of(inTheWay());
    }

    @Override
    public Obstacles inTheWay() {
        final double[] reach = new double[] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
            -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        built.placed().forEach((part, at) -> part.shape().form().points().forEach(p -> {
            final Vec3 w = STANDS.plus(at.plus(p));
            reach[0] = Math.min(reach[0], w.x());
            reach[1] = Math.min(reach[1], w.y());
            reach[2] = Math.min(reach[2], w.z());
            reach[3] = Math.max(reach[3], w.x());
            reach[4] = Math.max(reach[4], w.y());
            reach[5] = Math.max(reach[5], w.z());
        }));
        return new Obstacles(List.of(new Block((reach[0] + reach[3]) / 2, (reach[2] + reach[5]) / 2,
                (reach[3] - reach[0]) / 2, (reach[5] - reach[2]) / 2, 0, Math.max(0, reach[1]), reach[4])));
    }

    @Override
    public String said() {
        return String.format(Locale.ENGLISH, "The GLM platform, %d parts: %s, its covers %s", built.placed().size(),
                engine.step() == ENGINES.size() - 1 ? "no engine" : "the " + ENGINES.get(engine.step())
                        + " in its bay", covers.step() == 0 ? "on" : "off");
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        drawn.drawn(pass, camera, shown());
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        drawn.seenThrough(pass, camera, shown());
    }

    @Override
    public void close() {
        if (drawn != null) {
            drawn.close();
            drawn = null;
        }
    }

    /** The car with the engine the dial is at put on the card, the last taken off. */
    private void put() {
        showing = engine.step();
        final EngineFigures chosen = new EngineRange().all().stream()
                .filter(e -> e.name().equals(ENGINES.get(showing))).findFirst().orElse(null);
        built = new Assembly(new GlmPlatform(new Proportions(), new BodyFigures(), chosen == null ? null
                : new EngineBay(chosen, true)).platform()).assembled();
        close();
        drawn = new PlatformDrawn(card, laid, built, STANDS);
    }

    /** What is on show, as the dials say: every group, but the covers and the body where they are off. */
    private OnShow shown() {
        final Set<Group> hidden = EnumSet.noneOf(Group.class);
        if (covers.step() == 1) {
            hidden.add(Group.COVERS);
        }
        if (body.step() == 1) {
            hidden.add(Group.UNDERBODY);
        }
        return new OnShow(new Finishes().coats(), hidden, new MadeOf());
    }
}
