package melons.innocent.glm.testdrive;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.glm.bench.OnShow;
import melons.innocent.glm.bench.PlatformDrawn;
import melons.innocent.glm.core.Assembled;

import java.util.Locale;

/**
 * The GLM car on the card as it is test driven: put together as it is made, the bare vehicle and its engine, drawn
 * wherever it is driven to, whole; and over the picture its speed and the keys that drive it. Closed, it is gone from
 * the card.
 */
final class CarDrawn implements AutoCloseable {

    private static final Vec3 WHITE = new Vec3(0.92, 0.95, 0.97);
    private static final Vec3 DIM = new Vec3(0.55, 0.62, 0.66);
    private static final String KEYS = "W THROTTLE  S BRAKE  A/D STEER  SPACE HANDBRAKE  SHIFT CAREFUL  Q/E VIEW";
    /** How many kilometres an hour a centimetre a second is. */
    private static final double KMH = 0.036;

    private final Assembled built;
    private final OnShow shown;
    private PlatformDrawn drawn;

    /** {@code built}, shown as {@code shown} says. */
    CarDrawn(final Assembled built, final OnShow shown) {
        this.built = built;
        this.shown = shown;
    }

    /** Put on {@code card}, its layouts as {@code laid} says. */
    void carded(final Card card, final Laid laid) {
        drawn = new PlatformDrawn(card, laid, built);
    }

    /** Drawn by {@code pass} as {@code camera} sees it, carried by {@code by} to where it is driven. */
    void drawn(final Pass pass, final Camera camera, final Mat4 by) {
        drawn.drawn(pass, camera, shown, by);
        drawn.seenThrough(pass, camera, shown, by);
    }

    /** Its speed, {@code ahead} centimetres a second, and its keys, over the picture {@code over}. */
    void overlaid(final Pass pass, final Overlay.Over over, final double ahead) {
        final String speed = String.format(Locale.ROOT, "%3.0f KM/H", Math.abs(ahead) * KMH);
        over.text(pass, speed, over.aspect() - 0.08 - over.wide(speed, 0.075), -0.9, 0.075, WHITE);
        over.text(pass, KEYS, -over.wide(KEYS, 0.034) / 2, 0.92, 0.034, DIM);
    }

    @Override
    public void close() {
        if (drawn != null) {
            drawn.close();
            drawn = null;
        }
    }
}
