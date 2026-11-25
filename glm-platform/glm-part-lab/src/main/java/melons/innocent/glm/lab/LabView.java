package melons.innocent.glm.lab;

import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import melons.innocent.glm.core.Assembled;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The lab's view, on the card: what the scene shows ({@link LabDrawn}), on the ground where the ground is shown and
 * floating where it is not, the grid round it where the grid is shown; casting its shadows where they are asked for.
 * Closed, all of it is gone from the card.
 */
public final class LabView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much ground it covers, as a share of the scene. */
    private static final int PIXELS = 4096;
    private static final double SHADED = 2.5;

    private final Laid laid;
    private final LabDrawn drawn;
    private final Cast cast;
    private final Shaded shadows;
    private final List<Runnable> closing = new ArrayList<>();

    /** {@code built}'s parts, on {@code card}, its layouts as {@code laid} says. */
    public LabView(final Card card, final Laid laid, final Assembled built) {
        this.laid = laid;
        try {
            this.drawn = new LabDrawn(card, laid, built);
            closing.add(drawn::close);
            this.cast = new Cast(card, laid);
            closing.add(cast::close);
            this.shadows = new Shaded(card, laid, PIXELS);
            closing.add(shadows::close);
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The ways its shadows can be found, on the card it draws on. */
    public List<Shadowing> offered() {
        return shadows.offered();
    }

    /** The grid sent afresh for {@code scene}. */
    public void ruled(final LabScene.Settled scene) {
        drawn.ruled(scene);
    }

    /**
     * One frame drawn on {@code target}: {@code scene}, seen by {@code camera} in {@code time}'s light and under its
     * sky; with shadows, found {@code by} that way, where {@code shadowed}.
     */
    public void drawn(final Target target, final LabScene.Settled scene, final Camera camera, final Daylight time,
                      final boolean shadowed, final Shadowing by) {
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> casting = pass -> drawn.solid(pass, camera, scene);
        final Consumer<Pass> all = pass -> {
            if (scene.show().ground()) {
                cast.ground(pass, camera);
            }
            casting.accept(pass);
            drawn.marked(pass, camera, scene);
            drawn.seenThrough(pass, camera, scene);
        };
        if (shadowed) {
            final double across = Math.max(1, scene.size() * SHADED);
            final Beam beam = new Beam(scene.middle(), time.light().sunTowards(), across, across);
            shadows.drawn(target, time.sky(), framed, by, beam, casting, all);
        } else {
            target.drawn(time.sky(), framed.filling((double) target.width() / target.height()), all);
        }
    }

    @Override
    public void close() {
        closing.reversed().forEach(Runnable::run);
        closing.clear();
    }
}
