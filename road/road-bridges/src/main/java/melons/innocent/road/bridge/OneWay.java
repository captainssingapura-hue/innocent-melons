package melons.innocent.road.bridge;

import melons.innocent.road.surface.Section;
import melons.innocent.road.surface.Strip;

import java.util.ArrayList;
import java.util.List;

/**
 * A ramp's road, one way only: so many lanes going its way, each so wide, a shoulder so wide either side of them, or
 * none; its pavement falling {@link #FALL} away from its line, its lines painted on it.
 *
 * @param lanes    how many lanes
 * @param lane     how wide each is
 * @param shoulder how wide each shoulder is
 */
record OneWay(int lanes, double lane, double shoulder) {

    /** How steeply its pavement falls away from its line. */
    static final double FALL = 0.025;

    /** It across. */
    Section section() {
        final List<Strip> strips = new ArrayList<>();
        if (shoulder > 0) {
            strips.add(new Strip(Strip.Kind.SHOULDER, shoulder));
        }
        for (int k = 0; k < lanes; k++) {
            strips.add(new Strip(Strip.Kind.LANE, lane));
        }
        if (shoulder > 0) {
            strips.add(new Strip(Strip.Kind.SHOULDER, shoulder));
        }
        return new Section(strips, FALL, true);
    }
}
