package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.device.Key;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Window;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Who flies the view, as the keys and the mouse in the card's window say, as the avatar flies on its jets: W and S
 * ahead and back, A and D aside, Space up and Ctrl down, Shift five times as fast; dragged, the mouse turns the eye
 * round and tips it; the wheel draws the land bigger or smaller. The higher the eye, the faster it goes, so the land
 * goes by on the screen as fast at any height: a tenth of its height each second, never slower than {@link #SLOWEST}.
 */
public record Pilot() implements StatelessFunctionalObject {

    public static final Pilot INSTANCE = new Pilot();

    /** How fast the eye goes for each unit of its height, each second; never slower than so many centimetres. */
    private static final double PACE = 0.1;
    private static final double SLOWEST = 300;
    private static final double QUICKER = 5;
    /** How long a step may be taken to be, at most, in seconds, so a frame held up does not throw the eye far. */
    private static final double LONGEST = 0.1;

    /**
     * {@code flight} flown for {@code seconds} as the keys held and the mouse moved in {@code window} say,
     * {@code times} as fast as at first.
     */
    public Flight flown(final Flight flight, final Window window, final double seconds, final double times) {
        final Moved mouse = window.mouse();
        final Flight looked = (mouse.dragged() ? flight.dragged(mouse.across(), mouse.down()) : flight)
                .scrolled(mouse.scrolled());
        final double speed = Math.max(SLOWEST, Math.abs(looked.height()) * PACE)
                * (window.held(Key.SHIFT) ? QUICKER : 1) * times * Math.min(seconds, LONGEST);
        final double ahead = (window.held(Key.W) ? 1 : 0) - (window.held(Key.S) ? 1 : 0);
        final double aside = (window.held(Key.D) ? 1 : 0) - (window.held(Key.A) ? 1 : 0);
        final double up = (window.held(Key.SPACE) ? 1 : 0) - (window.held(Key.CONTROL) ? 1 : 0);
        return ahead == 0 && aside == 0 && up == 0 ? looked
                : looked.flown(ahead * speed, aside * speed, up * speed);
    }
}
