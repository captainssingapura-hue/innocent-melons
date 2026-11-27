package melons.innocent.glm.bench;

import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.PartAt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The bench's view, on the card: the platform put together, standing on level ground where its own frame has it, its
 * groups shown and finished as what is on show says, casting its shadow on the ground where shadows are asked for.
 * Closed, all of it is gone from the card.
 */
public final class GlmView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much ground it covers, in centimetres. */
    private static final int PIXELS = 4096;
    private static final double SHADED = 1_200;
    /** Each part where it was put. */
    private static final Function<PartAt, Mat4> STILL = part -> new Mat4();

    private final Laid laid;
    private final PlatformDrawn platform;
    private final Cast cast;
    private final Shaded shadows;
    private final Vec3 middle;
    private final List<Runnable> closing = new ArrayList<>();

    /** {@code built}, on {@code card}, its layouts as {@code laid} says, its shadows' picture about {@code middle}. */
    public GlmView(final Card card, final Laid laid, final Assembled built, final Vec3 middle) {
        this.laid = laid;
        this.middle = middle;
        try {
            this.platform = new PlatformDrawn(card, laid, built);
            closing.add(platform::close);
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

    /**
     * One frame drawn on {@code target}: {@code shown}, the whole of it moved by {@code moved}, leaned over, say, seen
     * by {@code camera} in {@code time}'s light and under its sky; with shadows, found {@code by} that way, where
     * {@code shadowed}.
     */
    public void drawn(final Target target, final OnShow shown, final Mat4 moved, final Camera camera,
                      final Daylight time, final boolean shadowed, final Shadowing by) {
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> casting = pass -> platform.drawn(pass, camera, shown, moved, STILL);
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            casting.accept(pass);
            platform.seenThrough(pass, camera, shown, moved, STILL);
        };
        if (shadowed) {
            final Beam beam = new Beam(middle, time.light().sunTowards(), SHADED, SHADED);
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
