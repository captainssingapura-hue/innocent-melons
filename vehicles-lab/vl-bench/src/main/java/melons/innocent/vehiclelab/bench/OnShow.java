package melons.innocent.vehiclelab.bench;

import melons.innocent.vehiclelab.motorcycle.Livery;

/**
 * What the bench shows, as its window said it last: the boxer finished as {@code livery} says, its bars turned
 * {@code steer} degrees, its wheels turning or still. Said on the bench's bus by the window, taken up by the view.
 *
 * @param livery  how it is finished
 * @param steer   how far its bars are turned, in degrees
 * @param rolling whether its wheels are turning
 */
public record OnShow(Livery livery, double steer, boolean rolling) {

    /** The boxer as it comes, its bars straight, its wheels still. */
    public OnShow() {
        this(new Livery(), 0, false);
    }
}
