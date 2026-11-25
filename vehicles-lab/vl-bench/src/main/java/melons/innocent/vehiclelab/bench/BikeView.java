package melons.innocent.vehiclelab.bench;

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
import melons.innocent.vehiclelab.drawn.BoxerDrawn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The bench's view, on the card: the boxer standing on level ground at the middle, its front toward along, drawn as
 * {@link BoxerDrawn} draws it, finished and steered as what is on show says, its wheels rolled as far as they have
 * turned; and, where shadows are asked for, casting its shadow on the ground. Closed, all of it is gone from the card.
 */
public final class BikeView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much ground it covers, in centimetres. */
    private static final int PIXELS = 4096;
    private static final double SHADED = 800;

    private final Laid laid;
    private final BoxerDrawn boxer;
    private final Cast cast;
    private final Shaded shadows;
    private final List<Runnable> closing = new ArrayList<>();

    /** The view, on {@code card}, its layouts as {@code laid} says. */
    public BikeView(final Card card, final Laid laid) {
        this.laid = laid;
        try {
            this.boxer = new BoxerDrawn(card, laid);
            closing.add(boxer::close);
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
     * One frame drawn on {@code target}: {@code shown}, its wheels rolled {@code roll} degrees, seen by {@code camera}
     * in {@code time}'s light and under its sky; with shadows, found {@code by} that way, where {@code shadowed}.
     */
    public void drawn(final Target target, final OnShow shown, final double roll, final Camera camera,
                      final Daylight time, final boolean shadowed, final Shadowing by) {
        final Mat4 placed = new Mat4();
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> casting = pass -> boxer.drawn(pass, camera, placed, shown.steer(), roll, shown.livery());
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            casting.accept(pass);
            boxer.seenThrough(pass, camera, placed, shown.steer(), roll, shown.livery());
        };
        if (shadowed) {
            final Beam beam = new Beam(new Vec3(0, 0, 0), time.light().sunTowards(), SHADED, SHADED);
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
