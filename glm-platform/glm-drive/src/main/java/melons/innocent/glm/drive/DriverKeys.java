package melons.innocent.glm.drive;

import com.example.fxsuite.vulkan.device.Key;
import com.example.fxsuite.vulkan.device.Window;

import java.util.EnumSet;
import java.util.Set;

/**
 * The driver's keys on the card's window, read once a frame: W or Up the throttle and S or Down the brake, each eased
 * on as it is held and off as it is let go, as a foot presses a pedal; E a change up and Q a change down; Shift the
 * gearbox switched between automatic and manual; C the cruise control set or let go, R and F its speed raised and
 * lowered. A key asking for something asks once as it goes down, however long it is held.
 */
public final class DriverKeys {

    /** How fast a pedal goes down and comes up, a share each second. */
    private static final double PRESS = 2.5;
    private static final double LIFT = 4;

    private final Set<Key> held = EnumSet.noneOf(Key.class);
    private double throttle;
    private double brake;

    /** What the driver does now, {@code seconds} since last asked, as {@code window}'s keys say. */
    public Driver read(final Window window, final double seconds) {
        final boolean open = window.held(Key.W) || window.held(Key.UP);
        final boolean stop = window.held(Key.S) || window.held(Key.DOWN);
        throttle = pedal(throttle, open, seconds);
        brake = pedal(brake, stop, seconds);
        return new Driver(throttle, brake, pressed(window, Key.E), pressed(window, Key.Q), pressed(window, Key.SHIFT),
                pressed(window, Key.C), pressed(window, Key.R), pressed(window, Key.F));
    }

    private double pedal(final double was, final boolean down, final double seconds) {
        return Math.clamp(was + (down ? PRESS : -LIFT) * seconds, 0, 1);
    }

    /** Whether {@code key} went down since the last frame. */
    private boolean pressed(final Window window, final Key key) {
        final boolean now = window.held(key);
        final boolean was = held.contains(key);
        if (now) {
            held.add(key);
        } else {
            held.remove(key);
        }
        return now && !was;
    }
}
