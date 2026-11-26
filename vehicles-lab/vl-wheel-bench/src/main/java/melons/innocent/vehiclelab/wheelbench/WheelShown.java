package melons.innocent.vehiclelab.wheelbench;

import melons.innocent.vehiclelab.wheels.RimDesign;
import melons.innocent.vehiclelab.wheels.WheelSize;

/**
 * What the wheels bench shows, as its window said it last: a wheel of {@code design}, at {@code size}, turning or
 * still. Said on the bench's bus by the window, taken up by the view.
 *
 * @param design  its rim's design
 * @param size    its size
 * @param turning whether it is turning
 */
public record WheelShown(RimDesign design, WheelSize size, boolean turning) {

    /** The retro saloon's to begin with: the classic rim, 225/50 R17, still. */
    public WheelShown() {
        this(RimDesign.CLASSIC, new WheelSize(225, 50, 17), false);
    }
}
