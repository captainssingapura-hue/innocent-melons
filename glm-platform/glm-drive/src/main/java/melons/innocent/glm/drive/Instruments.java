package melons.innocent.glm.drive;

import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.Locale;

/**
 * The instruments, drawn over the picture as a car's display is: along the foot on the left, the revs as a bar from
 * idle to the red line, green, amber toward the top and red at it, the revs written over it; in the middle, the gear,
 * large, and whether the gearbox changes by itself (A) or at the driver's word (M); on the right, the speed; over it,
 * the cruise control's speed while it is set; and along the top, which key does what.
 */
public record Instruments() {

    private static final Vec3 GREEN = new Vec3(0.08, 0.5, 0.14);
    private static final Vec3 AMBER = new Vec3(0.7, 0.32, 0.02);
    private static final Vec3 RED = new Vec3(0.75, 0.05, 0.03);
    private static final Vec3 DIM = new Vec3(0.55, 0.62, 0.66);
    private static final Vec3 WHITE = new Vec3(0.92, 0.95, 0.97);
    /** How high its letters are, small and large; where its foot is; how long and thick the rev bar is. */
    private static final double SMALL = 0.042;
    private static final double LARGE = 0.075;
    private static final double GEAR = 0.2;
    private static final double FOOT = -0.9;
    private static final double BAR = 0.95;
    private static final double THICK = 0.035;
    private static final double SEGMENTS = 40;
    /** How far up the revs the bar turns amber. */
    private static final double AMBER_FROM = 0.82;
    private static final String KEYS = "W THROTTLE  S BRAKE  E UP  Q DOWN  SHIFT A/M  C CRUISE  R/F +5/-5";

    /** {@code driven}'s instruments by {@code pass} over {@code over}. */
    public void drawn(final Pass pass, final Overlay.Over over, final Drivetrain driven) {
        final double edge = over.aspect();
        final double idle = driven.engine().idle();
        final double red = driven.engine().redline();
        final double share = Math.clamp((driven.rpm() - idle) / (red - idle), 0, 1.02);
        final double left = -edge + 0.08;
        for (int k = 0; k < SEGMENTS; k++) {
            final double at = k / SEGMENTS;
            final double x = left + at * BAR;
            final Vec3 colour = at >= 0.97 ? RED : at >= AMBER_FROM ? AMBER : GREEN;
            over.line(pass, x, FOOT, x + BAR / SEGMENTS * 0.7, FOOT, THICK, at <= share ? colour : DIM.times(0.12));
        }
        over.text(pass, String.format(Locale.ROOT, "%5.0f RPM", driven.rpm()), left, FOOT + 0.05, LARGE,
                share >= 0.97 ? RED : WHITE);
        final String gear = driven.gear();
        over.text(pass, gear, -over.wide(gear, GEAR) / 2, FOOT - 0.03, GEAR, gear.equals("N") ? AMBER : WHITE);
        final String box = driven.automatic() ? "A" : "M";
        over.text(pass, box, over.wide(gear, GEAR) / 2 + 0.04, FOOT, LARGE, driven.automatic() ? GREEN : AMBER);
        final String speed = String.format(Locale.ROOT, "%3.0f KM/H", driven.kmh());
        over.text(pass, speed, edge - 0.08 - over.wide(speed, LARGE), FOOT + 0.02, LARGE, WHITE);
        if (driven.cruise() > 0) {
            final String cruise = String.format(Locale.ROOT, "CRUISE %3.0f", driven.cruise());
            over.text(pass, cruise, edge - 0.08 - over.wide(cruise, SMALL), FOOT + 0.14, SMALL, AMBER);
        }
        over.text(pass, KEYS, -over.wide(KEYS, SMALL * 0.8) / 2, 0.92, SMALL * 0.8, DIM);
    }
}
