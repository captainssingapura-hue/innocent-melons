package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Pillars where they are wanted, and only there: under each point something wants holding up from, a pillar of steel
 * {@link #SQUARE} square, finished in {@code frame}, unless the roof of one of {@code boxes} is right under it, bearing
 * it. A pillar stands on the roof of the highest of {@code boxes} under it, or on the ground, where height is nought,
 * and reaches up to what it holds up. So whatever is laid out, nothing is left hanging, and nothing stands where it is
 * not needed.
 *
 * @param boxes what may bear on one another
 * @param frame what the pillars are finished in
 */
record Pillars(List<Container> boxes, Finish frame) {

    /** How square a pillar is; how near a roof must be to an underside to bear it. */
    static final double SQUARE = 20;
    static final double TOUCH = 1;

    /** Pillars under each of {@code points}, up to {@code under}, wherever nothing else holds it; none under it. */
    List<Part> under(final List<Vec2> points, final double under) {
        final List<Part> parts = new ArrayList<>();
        for (final Vec2 p : points) {
            final boolean borne = boxes.stream().anyMatch(b -> Math.abs(b.top() - under) < TOUCH
                    && b.covers(p.x(), p.z()));
            if (borne) {
                continue;
            }
            final double foot = boxes.stream().filter(b -> b.top() < under - TOUCH && b.covers(p.x(), p.z()))
                    .mapToDouble(Container::top).max().orElse(0);
            if (under - foot >= TOUCH) {
                parts.add(new Part(new Elements.Column(), frame.on(Timbers.INSTANCE.block("pillar", Role.STRUCTURE,
                        frame.material(), p.x() - SQUARE / 2, p.x() + SQUARE / 2, p.z() - SQUARE / 2,
                        p.z() + SQUARE / 2, foot, under))));
            }
        }
        return parts;
    }

    /** Pillars under each of {@code boxes}, wherever nothing else holds it. */
    List<Part> all() {
        final List<Part> parts = new ArrayList<>();
        boxes.forEach(b -> parts.addAll(under(b.bearings(SQUARE / 2), b.under())));
        return parts;
    }
}
