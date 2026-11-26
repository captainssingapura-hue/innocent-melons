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
 * wherever it is driven to, whole, its front wheels steered and every wheel turning as it rolls ({@link Wheeling}),
 * its lamps lit as it is driven ({@link Lamps}); and over the picture its speed, its engine's revs and gear, and the
 * keys that drive it. Closed, it is gone from the card.
 */
final class CarDrawn implements AutoCloseable {

    private static final Vec3 WHITE = new Vec3(0.92, 0.95, 0.97);
    private static final Vec3 DIM = new Vec3(0.55, 0.62, 0.66);
    private static final String KEYS = "W THROTTLE  S BRAKE  A/D STEER  SPACE HANDBRAKE  SHIFT CAREFUL  C VIEW"
            + "  V COCKPIT  R BACK  F LIGHTS  G INDICATE";
    /** How many kilometres an hour a centimetre a second is; how high its figures are written. */
    private static final double KMH = 0.036;
    private static final double LARGE = 0.075;
    private static final double SMALL = 0.034;

    private final Assembled built;
    private final OnShow shown;
    private final Wheeling wheeling;
    private PlatformDrawn drawn;

    /** {@code built}, shown as {@code shown} says, its wheels moving as {@code wheeling} says. */
    CarDrawn(final Assembled built, final OnShow shown, final Wheeling wheeling) {
        this.built = built;
        this.shown = shown;
        this.wheeling = wheeling;
    }

    /** Put on {@code card}, its layouts as {@code laid} says. */
    void carded(final Card card, final Laid laid) {
        drawn = new PlatformDrawn(card, laid, built);
    }

    /**
     * Drawn by {@code pass} as {@code camera} sees it, carried by {@code by} to where it is driven, its front wheels
     * turned {@code steer} degrees to the right and the car rolled {@code rolled} centimetres, its lamps lit as
     * {@code lamps} has them.
     */
    void drawn(final Pass pass, final Camera camera, final Mat4 by, final double steer, final double rolled,
               final Lamps lamps) {
        drawn.drawn(pass, camera, shown, by, part -> wheeling.moved(part, steer, rolled), lamps::lit);
        drawn.seenThrough(pass, camera, shown, by, part -> wheeling.moved(part, steer, rolled), lamps::lit);
    }

    /**
     * Over the picture {@code over}: its speed, {@code ahead} centimetres a second; its engine's revs, {@code rpm}, in
     * gear {@code gear}; and its keys.
     */
    void overlaid(final Pass pass, final Overlay.Over over, final double ahead, final double rpm, final int gear) {
        final String speed = String.format(Locale.ROOT, "%3.0f KM/H", Math.abs(ahead) * KMH);
        over.text(pass, speed, over.aspect() - 0.08 - over.wide(speed, LARGE), -0.9, LARGE, WHITE);
        final String revs = String.format(Locale.ROOT, "%5.0f RPM  %s", rpm, ahead < -1 ? "R" : String.valueOf(gear));
        over.text(pass, revs, -over.aspect() + 0.08, -0.9, LARGE, WHITE);
        over.text(pass, KEYS, -over.wide(KEYS, SMALL) / 2, 0.92, SMALL, DIM);
    }

    /**
     * Over the picture {@code over}, high on the right: its lamps as {@code lit} says them, the view it is seen in, and
     * how far it has rolled in metres.
     */
    void said(final Pass pass, final Overlay.Over over, final String view, final double rolled, final String lit) {
        final String line = String.format(Locale.ROOT, "%s%s  %6.0f M", lit.isEmpty() ? "" : lit + "   ", view,
                rolled / 100);
        over.text(pass, line, over.aspect() - 0.08 - over.wide(line, SMALL), 0.8, SMALL, DIM);
    }

    @Override
    public void close() {
        if (drawn != null) {
            drawn.close();
            drawn = null;
        }
    }
}
