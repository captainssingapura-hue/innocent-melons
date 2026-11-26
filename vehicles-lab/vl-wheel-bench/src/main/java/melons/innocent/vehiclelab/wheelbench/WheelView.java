package melons.innocent.vehiclelab.wheelbench;

import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import melons.innocent.vehiclelab.drawn.Coats;
import melons.innocent.vehiclelab.forms.Coat;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.wheels.Tyre;

import java.util.List;
import java.util.function.Consumer;

/**
 * The wheels bench's view, on the card: the wheel on show standing on level ground at the middle, its axle across,
 * its face toward the right, its rim in cast alloy and its tyre in rubber, turned as far as it has rolled; casting its
 * shadow where shadows are asked for. A wheel of another design or size is sent to the card afresh, the last gone.
 * Closed, all of it is gone.
 */
final class WheelView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much ground it covers, in centimetres. */
    private static final int PIXELS = 4096;
    private static final double SHADED = 300;
    /** How far out the hub's face is from the wheel's middle; how finely its round shapes are drawn. */
    private static final double MOUNT = 1;
    private static final int ROUND = 48;
    private static final Coat ALLOY = new Coat(Coat.Kind.ALUMINIUM);
    private static final Coat RUBBER = new Coat(Coat.Kind.RUBBER, "#1b1d20");

    private final Card card;
    private final Laid laid;
    private final Coats coats;
    private final Cast cast;
    private final Shaded shadows;
    private WheelShown showing;
    private Shaped rim;
    private Shaped tyre;

    /** On {@code card}, its layouts as {@code laid} says. */
    WheelView(final Card card, final Laid laid) {
        this.card = card;
        this.laid = laid;
        this.coats = new Coats(card, laid);
        this.cast = new Cast(card, laid);
        this.shadows = new Shaded(card, laid, PIXELS);
    }

    /** The ways its shadows can be found. */
    List<Shadowing> offered() {
        return shadows.offered();
    }

    /**
     * One frame drawn on {@code target}: {@code shown}, rolled {@code roll} degrees, seen by {@code camera} in
     * {@code time}'s light; with shadows, found {@code by} that way, where {@code shadowed}.
     */
    void drawn(final Target target, final WheelShown shown, final double roll, final Camera camera,
               final Daylight time, final boolean shadowed, final Shadowing by) {
        fitted(shown);
        final Mat4 at = new Mat4(new Vec3(0, shown.size().radius(), 0)).times(rolled(roll));
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> casting = pass -> {
            coats.drawn(pass, rim, at, camera, ALLOY);
            coats.drawn(pass, tyre, at, camera, RUBBER);
        };
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            casting.accept(pass);
        };
        if (shadowed) {
            final Beam beam = new Beam(new Vec3(0, 0, 0), time.light().sunTowards(), SHADED, SHADED);
            shadows.drawn(target, time.sky(), framed, by, beam, casting, all);
        } else {
            target.drawn(time.sky(), framed.filling((double) target.width() / target.height()), all);
        }
    }

    /** {@code shown} sent to the card, if it is not what is there already. */
    private void fitted(final WheelShown shown) {
        if (shown.equals(showing)) {
            return;
        }
        if (showing == null || !shown.design().equals(showing.design()) || !shown.size().equals(showing.size())) {
            gone();
            final Shapes shapes = new Shapes(ROUND);
            rim = new Shaped(card, laid, shown.design().rim(shapes).mesh(shown.size(), MOUNT));
            tyre = new Shaped(card, laid, new Tyre(shown.size(), shapes).mesh());
        }
        showing = shown;
    }

    /** Turned {@code roll} degrees about its axle, its top going ahead. */
    private Mat4 rolled(final double roll) {
        final double a = Math.toRadians(roll);
        return new Mat4(new double[] {1, 0, 0, 0, 0, Math.cos(a), -Math.sin(a), 0, 0, Math.sin(a), Math.cos(a), 0,
            0, 0, 0, 1});
    }

    private void gone() {
        if (rim != null) {
            rim.close();
            tyre.close();
        }
    }

    @Override
    public void close() {
        gone();
        shadows.close();
        cast.close();
        coats.close();
    }
}
