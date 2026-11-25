package melons.innocent.glm.enginebench;

import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.drive.Drivetrain;
import melons.innocent.glm.drive.Instruments;
import melons.innocent.glm.engine.EngineInternals;
import melons.innocent.glm.model.Finishes;
import melons.innocent.glm.model.MadeOf;
import melons.innocent.vehiclelab.drawn.Coats;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The engine bench's view, on the card: the engine on show, its block as glass, finished or hidden, and every part
 * inside drawn where it has moved to for the crank's angle, standing on the ground, casting its shadows; over it all,
 * the instruments of the car it drives. A new engine is sent to the card afresh, the old one gone. Closed, all of it
 * is gone.
 */
final class EngineView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much ground it covers. */
    private static final int PIXELS = 4096;
    private static final double SHADED = 120;
    private static final Coat GLASS = new Coat(Coat.Kind.GLASS);

    private final Card card;
    private final Laid laid;
    private final Coats coats;
    private final Cast cast;
    private final Shaded shadows;
    private final Overlay overlay;
    private final Instruments instruments = new Instruments();
    private final Finishes finishes = new Finishes();
    private final Map<PartAt, Shaped> parts = new IdentityHashMap<>();
    private Assembled built;
    private EngineInternals inside;
    private Part root;

    /** On {@code card}, its layouts as {@code laid} says. */
    EngineView(final Card card, final Laid laid) {
        this.card = card;
        this.laid = laid;
        this.coats = new Coats(card, laid);
        this.cast = new Cast(card, laid);
        this.shadows = new Shaded(card, laid, PIXELS);
        this.overlay = new Overlay(card, laid);
    }

    /** The ways its shadows can be found. */
    List<Shadowing> offered() {
        return shadows.offered();
    }

    /** {@code engine}, put together as {@code assembled} on its block {@code on}, sent afresh, the last gone. */
    void showing(final EngineInternals engine, final Assembled assembled, final Part on) {
        this.root = on;
        parts.values().forEach(Shaped::close);
        parts.clear();
        this.inside = engine;
        this.built = assembled;
        assembled.placed().keySet().forEach(p -> parts.put(p, new Shaped(card, laid, p.shape().form())));
    }

    /**
     * One frame drawn on {@code target}: the engine, the crank turned {@code turned} degrees, its block shown as
     * {@code block}, made of what {@code madeOf} says, lifted {@code lift} off the floor, seen by {@code camera} in
     * {@code time}'s light; with shadows, found {@code by} that way, where {@code shadowed}; over it, the instruments
     * of {@code driven}.
     */
    void drawn(final Target target, final double turned, final EngineRun.Block block, final MadeOf madeOf,
               final Vec3 lift, final Camera camera, final Daylight time, final boolean shadowed,
               final Shadowing by, final Drivetrain driven) {
        final double aspect = (double) target.width() / target.height();
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> solid = pass -> parts(pass, camera, turned, block, madeOf, lift, false);
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            solid.accept(pass);
            parts(pass, camera, turned, block, madeOf, lift, true);
            instruments.drawn(pass, overlay.over(camera, aspect), driven);
        };
        if (shadowed) {
            final Beam beam = new Beam(lift, time.light().sunTowards(), SHADED, SHADED);
            shadows.drawn(target, time.sky(), framed, by, beam, solid, all);
        } else {
            target.drawn(time.sky(), framed.filling(aspect), all);
        }
    }

    /** Its parts, those seen through or those not, each where it has moved to. */
    private void parts(final Pass pass, final Camera camera, final double turned, final EngineRun.Block block,
                       final MadeOf madeOf, final Vec3 lift, final boolean seenThrough) {
        for (final PartAt p : built.placed().keySet()) {
            final boolean isBlock = p.part().equals(root);
            if (isBlock && block == EngineRun.Block.HIDDEN) {
                continue;
            }
            final Coat coat = isBlock && block == EngineRun.Block.GLASS ? GLASS : finishes.of(p.part(), madeOf);
            if (coats.seenThrough(coat) == seenThrough) {
                final Mat4 at = new Mat4(lift.plus(built.at(p))).times(inside.moved(p.part(), turned));
                coats.drawn(pass, parts.get(p), at, camera, coat);
            }
        }
    }

    @Override
    public void close() {
        parts.values().forEach(Shaped::close);
        parts.clear();
        overlay.close();
        shadows.close();
        cast.close();
        coats.close();
    }
}
